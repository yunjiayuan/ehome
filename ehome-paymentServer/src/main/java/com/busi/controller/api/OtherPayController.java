package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.payment.alipay.AlipayConfig;
import com.busi.payment.alipay.AlipayUtils;
import com.busi.payment.unionpay.UnionPayConfig;
import com.busi.payment.unionpay.sdk.AcpService;
import com.busi.payment.unionpay.sdk.SDKConfig;
import com.busi.payment.unionpay.sdk.SDKConstants;
import com.busi.payment.weixin.WeixinConfig;
import com.busi.payment.weixin.WeixinUtils;
import com.busi.service.RechargeOrderService;
import com.busi.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * 第三方支付平台 加签 回调相关接口
 * author：SunTianJie
 * create time：2018/8/31 14:18
 */
@Slf4j
@RestController
public class OtherPayController extends BaseController implements OtherPayApiController {

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private RechargeOrderService rechargeOrderService;

    @Autowired
    private MqUtils mqUtils;

    /***
     *  第三方平台加签接口
     * @param platformType 第三方支付平台类型  1：支付宝, 2：微信, 3银联, 4银联token版
     * @param payType      支付类型 1：充值，2：预留...
     * @param sum          充值金额 小数点后两位
     * @return
     */
    @Override
    public ReturnData rechargeDataSign(@PathVariable int platformType,@PathVariable int payType,@PathVariable double sum) {
        //验证码参数
        if(platformType<1||platformType>3){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"platformType参数有误",new JSONObject());
        }
        if(payType!=1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"payType参数有误",new JSONObject());
        }
        if(sum<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"sum参数有误",new JSONObject());
        }
        //开始加签
        String new_out_trade_no = CommonUtils.getOrderNumber(CommonUtils.getMyId(),Constants.REDIS_KEY_PAY_ORDER_RECHARGE);//订单号
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+CommonUtils.getMyId() );
        if(userMap==null||userMap.size()<=0){
            //缓存中没有用户对象信息
            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"当前账号不存在或存在异常，建议重新登录",new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        String signData = "";
        if(platformType==1){//支付宝
            //调用加签工具进行加签操作
            String data = "{" +
//						"\"addOrderTime\":\""+addOrderTime+"\"," +
                    "\"timeout_express\":\"30m\"," +
                    "\"seller_id\":\"2088521070621170\"," +
                    "\"product_code\":\"QUICK_MSECURITY_PAY\"," +
                    "\"total_amount\":"+sum+"," +
                    "\"subject\":\"["+userMap.get("name")+"]进行充值操作"+""+"\"," +
                    "\"body\":\"用户["+myId+"进行充值业务,金额为:"+sum+"\"," +
                    "\"out_trade_no\":\""+new_out_trade_no+"\"" +
//						"\"userId\":"+myId +
                    "}";
            String notify_url = "";
            if(payType==1){//充值回调
                notify_url = AlipayConfig.RECHARGE_NOTIFY_URL;
            }else{
                //预留...
            }
            try {
                signData = AlipayUtils.dataSign(data,notify_url,myId);
            } catch (AlipayApiException e) {
                e.printStackTrace();
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"服务端加签操作失败，服务端异常！",new JSONObject());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"服务端加签操作失败，服务端异常！",new JSONObject());
            }
        }else if(platformType==2){//微信

            int newSum = (int)(sum*100);
            String notify_url = "";
            if(payType==1){//充值回调
                notify_url = WeixinConfig.RECHARGE_NOTIFY_URL;
            }else{
                //预留...
            }
            WeixinSignBean wsb = null;
            String sign = "";
            String newSign = "";
            SortedMap<String, String> signParams = new TreeMap<String, String>();
            signParams.put("appid", WeixinConfig.APP_ID);//app_id
            signParams.put("body","用户["+userMap.get("name")+"]进行充值业务");//商品参数信息
//				signParams.put("body","recharge:"+sum);//商品参数信息  本地测试用  解决中文乱码问题  线上无问题
            signParams.put("mch_id", WeixinConfig.MCH_ID);//微信商户账号
            signParams.put("nonce_str", CommonUtils.strToMD5(new_out_trade_no, 32));//32位不重复的编号 随机字符串
            signParams.put("notify_url", notify_url);//回调页面
            signParams.put("out_trade_no", new_out_trade_no);//商户订单号
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ip = "";
            if(attributes!=null){
                HttpServletRequest request = attributes.getRequest();
                ip = CommonUtils.getIpAddr(request);
            }
            signParams.put("spbill_create_ip",ip);//用户端实际ip
            signParams.put("total_fee",newSum+"");//支付金额 单位为分
            signParams.put("trade_type", "APP");//付款类型为APP
            signParams.put("attach", myId+"");//附加数据 用户参数传递
            sign = WeixinUtils.dataSign(signParams);//生成签名
            wsb = WeixinUtils.getWeixinSignBean(sign, signParams);
            if(wsb!=null){
                if("SUCCESS".equals(wsb.getResult_code())&&"SUCCESS".equals(wsb.getReturn_code())){
                    //第二次加签
                    long time = System.currentTimeMillis()/1000;
                    SortedMap<String, String> signParams2 = new TreeMap<String, String>();
                    signParams2.put("appid", WeixinConfig.APP_ID);//app_id
                    signParams2.put("partnerid", WeixinConfig.MCH_ID);//微信商户账号
                    signParams2.put("prepayid", wsb.getPrepay_id());//微信商户账号
                    signParams2.put("noncestr", wsb.getNonce_str());//回调页面
                    signParams2.put("timestamp", time+"");//时间戳
                    signParams2.put("package", "Sign=WXPay");//
                    newSign = WeixinUtils.dataSign( signParams2);//生成签名
                    signData = "appid="+wsb.getAppid()
                            +"&partnerid="+wsb.getMch_id()
                            +"&prepayid="+wsb.getPrepay_id()
                            +"&package=Sign=WXPay"
                            +"&noncestr="+wsb.getNonce_str()
                            +"&timestamp="+time
                            +"&sign="+newSign;
                }else{
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"服务端对用户[\"+myId+\"]“微信”充值数据加签请求失败，微信签名出现异常！",new JSONObject());
                }
            }
        }else if(platformType==3){//银联
            int newSum = (int)(sum*100);//单位转换成分
            Map<String, String> contentData = new HashMap<String, String>();
            /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
            contentData.put("version", UnionPayConfig.VERSION);//版本号 全渠道默认值
            contentData.put("encoding", UnionPayConfig.ENCODING_UTF8);//字符集编码 可以使用UTF-8,GBK两种方式
            contentData.put("signMethod", UnionPayConfig.SIGNMETHOD);//签名方法 目前只支持01：RSA方式证书加密
            contentData.put("txnType", UnionPayConfig.TXNTYPE);//交易类型 01:消费
            contentData.put("txnSubType", UnionPayConfig.TXNSUBTYPE);//交易子类 01：消费
            contentData.put("bizType", UnionPayConfig.BIZTYPE);//填写000201
            contentData.put("channelType", UnionPayConfig.CHANNELTYPE);//渠道类型 08手机
            /***商户接入参数***/
            contentData.put("merId", UnionPayConfig.MERID);//商户号码，请改成自己申请的商户号或者open上注册得来的777商户号测试
            contentData.put("accessType", "0");//接入类型，商户接入填0 ，不需修改（0：直连商户， 1： 收单机构 2：平台商户）
            contentData.put("orderId", new_out_trade_no);//商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
            contentData.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));//订单发送时间，取系统时间，格式为YYYYMMDDhhmmss，必须取当前时间，否则会报txnTime无效
            contentData.put("accType", "01");//账号类型 01：银行卡02：存折03：IC卡帐号类型(卡介质)
            contentData.put("txnAmt", newSum+"");//交易金额 单位为分，不能带小数点
            contentData.put("currencyCode", "156");//境内商户固定 156 人民币
            contentData.put("reqReserved", myId+"");//商户自定义保留域，交易应答时会原样返回
            //后台通知地址（需设置为外网能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，【支付失败的交易银联不会发送后台通知】
            //后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
            //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码
            //    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200或302，那么银联会间隔一段时间再次发送。总共发送5次，银联后续间隔1、2、4、5 分钟后会再次通知。
            //    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
            contentData.put("backUrl", UnionPayConfig.BACK_URL);
            /**对请求参数进行签名并发送http post请求，接收同步应答报文**/
            Map<String, String> reqData = AcpService.sign(contentData,UnionPayConfig.ENCODING_UTF8);//报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。
            String requestAppUrl = SDKConfig.getConfig().getAppRequestUrl();//交易请求url从配置文件读取对应属性文件acp_sdk.properties中的 acpsdk.backTransUrl
            Map<String, String> rspData = AcpService.post(reqData,requestAppUrl,UnionPayConfig.ENCODING_UTF8);//发送请求报文并接受同步应答（默认连接超时时间30秒，读取返回结果超时时间30秒）;这里调用signData之后，调用submitUrl之前不能对submitFromData中的键值对做任何修改，如果修改会导致验签不通过

            /**对应答码的处理，请根据您的业务逻辑来编写程序,以下应答码处理逻辑仅供参考------------->**/
            //应答码规范参考open.unionpay.com帮助中心 下载  产品接口规范  《平台接入接口规范-第5部分-附录》
            if(!rspData.isEmpty()){
                if(AcpService.validate(rspData, UnionPayConfig.ENCODING_UTF8)){
                    //验证签名成功
                    String respCode = rspData.get("respCode");
                    if(("00").equals(respCode)){
                        signData = rspData.get("tn");
                    }else{
                        return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"服务端加签操作失败，银联返回状态码异常："+respCode,new JSONObject());
                    }
                }else{//验证签名失败
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"服务端加签操作失败，服务端异常！",new JSONObject());
                }
            }else{
                //未返回正确的http状态
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"服务端加签操作中，未获取到银联返回报文或返回http状态码非200",new JSONObject());
            }
        }
//        else{//银联token版
//            //预留 后续开发
//
//        }
        //加签完成 生成充值订单
        RechargeOrder rechargeOrder =  new RechargeOrder();
        rechargeOrder.setOrderNumber(new_out_trade_no);
        rechargeOrder.setUserId(CommonUtils.getMyId());
        rechargeOrder.setMoney(sum);
        rechargeOrder.setPayStatus(0);//未支付状态
        rechargeOrder.setTime(new Date());
        //更新数据库
        rechargeOrderService.addRechargeOrder(rechargeOrder);
        //将订单放入缓存中  一天有效时间
        redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+CommonUtils.getMyId()+"_"+rechargeOrder.getOrderNumber(),CommonUtils.objectToMap(rechargeOrder),Constants.TIME_OUT_MINUTE_60_24_1);
        //响应客户端
        Map<String,String>  map = new HashMap<>();
        map.put("signData",signData);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 支付宝回调验签接口
     * @param alipayBean
     * @return
     */
    @Override
    public String checkAlipaySign(@RequestBody AlipayBean alipayBean) {
        long userId = Long.parseLong(alipayBean.getPassback_params());//用户ID
        double t_amount = Double.parseDouble(alipayBean.getTotal_amount());//金额
        Map<String, String> params = new HashMap<String, String>();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        Map<String, String[]> requestParams = request.getParameterMap();
        if(requestParams!=null){
            for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
                String name = iter.next();
                String[] values = requestParams.get(name);
                String valueStr = "";
                for (int i = 0; i < values.length; i++) {
                    valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
                }
                // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
                // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
                params.put(name, valueStr);
            }
        }
        boolean signVerified = false;
        try {
            signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.ALIPAY_CHARSET);
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("服务端处理支付宝平台发送用户[\"+userId+\"]充值操作的验签请求操作失败，验签失败！");
            return "fail";
        }
        if(signVerified){//验签通过 执行回调业务处理
            //获取未支付的订单
            Map<String,Object> rechargeOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+alipayBean.getOut_trade_no());
            if(rechargeOrderMap==null||rechargeOrderMap.size()<=0||Integer.parseInt(rechargeOrderMap.get("payStatus").toString())!=0){
                //缓存中不存在 查询数据库
                RechargeOrder rechargeOrder = rechargeOrderService.findRechargeOrder(userId,alipayBean.getOut_trade_no());
                if(rechargeOrder==null){//不存在 或已支付
                    log.info("支付宝-用户["+userId+"]验签失败,订单号：["+alipayBean.getOut_trade_no()+"]的订单信息不存在，无法进行验签操作!");
                    return "success";
                }
                if(rechargeOrder.getPayStatus()==1){//已支付
                    log.info("支付宝-用户["+userId+"]验签失败,订单号：["+alipayBean.getOut_trade_no()+"]的订单状态已支付，无法进行验签操作!");
                    return "success";
                }
                //更新缓存
                redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+rechargeOrder.getOrderNumber(),CommonUtils.objectToMap(rechargeOrder),Constants.TIME_OUT_MINUTE_60_24_1);
            }
            //更改状态 防止重复支付
            redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+alipayBean.getOut_trade_no(),"payStatus",1);
            //更新钱包和交易明细
            mqUtils.sendPurseMQ(userId,0,0,t_amount);
            //更新数据库 修改订单状态
            rechargeOrderService.updateRechargeOrder(userId,alipayBean.getOut_trade_no());
            //更新任务记录
            mqUtils.sendTaskMQ(userId,1,10);
            return "success";
        }
        log.info("验签失败，请重新充值！");
        return "fail";
    }

    /***
     * 微信回调验签接口
     * @param weixinSignBean
     * @return
     */
    @Override
    public String checkWeixinSign(@RequestBody WeixinSignBean weixinSignBean) {
        try {
            //开始解析 微信回调数据
            long userId = Long.parseLong(weixinSignBean.getAttach());
            String outTradeNo = weixinSignBean.getOut_trade_no();//订单号
            int total_fee = Integer.parseInt(weixinSignBean.getTotal_fee());
            //开始对微信数据和订单数据进行重新加签 并比对签名
            //将微信返回的数据 除去sign ,全部进行签名
            SortedMap<String, String> signParams = new TreeMap<String, String>();
            signParams.put("appid", weixinSignBean.getAppid());
            signParams.put("attach", weixinSignBean.getAttach());
            signParams.put("bank_type", weixinSignBean.getBank_type());
            signParams.put("cash_fee", weixinSignBean.getCash_fee());
            signParams.put("fee_type", weixinSignBean.getFee_type());
            signParams.put("is_subscribe", weixinSignBean.getIs_subscribe());
            signParams.put("mch_id", weixinSignBean.getMch_id());
            signParams.put("nonce_str", weixinSignBean.getNonce_str());
            signParams.put("openid", weixinSignBean.getOpenid());
            signParams.put("out_trade_no", outTradeNo);
            signParams.put("result_code", weixinSignBean.getResult_code());
            signParams.put("return_code", weixinSignBean.getReturn_code());
            signParams.put("time_end", weixinSignBean.getTime_end());
            signParams.put("total_fee", total_fee+"");//
            signParams.put("trade_type", weixinSignBean.getTrade_type());
            signParams.put("transaction_id", weixinSignBean.getTransaction_id());
            //重新加签
            String newSign = WeixinUtils.dataSign(signParams);//生成签名
            //验签
            if(!CommonUtils.checkFull(newSign)&&newSign.equals(weixinSignBean.getSign())){//验签成功 执行业务回调处理
                //获取未支付的订单
                Map<String,Object> rechargeOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+weixinSignBean.getAttach()+"_"+outTradeNo);
                if(rechargeOrderMap==null||rechargeOrderMap.size()<=0||Integer.parseInt(rechargeOrderMap.get("payStatus").toString())!=0){
                    //缓存中不存在 查询数据库
                    RechargeOrder rechargeOrder = rechargeOrderService.findRechargeOrder(userId,weixinSignBean.getOut_trade_no());
                    if(rechargeOrder==null){//不存在
                        log.info("微信-用户["+userId+"]验签失败,订单号：["+weixinSignBean.getOut_trade_no()+"]的订单信息不存在，无法进行验签操作!");
                        return "success";
                    }
                    if(rechargeOrder.getPayStatus()==1){//已支付
                        log.info("微信-用户["+userId+"]验签失败,订单号：["+weixinSignBean.getOut_trade_no()+"]的订单状态已支付，无法进行验签操作!");
                        return "success";
                    }
                    //更新缓存
                    redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+rechargeOrder.getOrderNumber(),CommonUtils.objectToMap(rechargeOrder),Constants.TIME_OUT_MINUTE_60_24_1);
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+outTradeNo,"payStatus",1);
                //更新钱包和交易明细
                double t_amount = total_fee/100.0;//单位是分 所以需要除以100
                mqUtils.sendPurseMQ(userId,0,0,t_amount);
                //更新数据库 修改订单状态
                rechargeOrderService.updateRechargeOrder(userId,weixinSignBean.getOut_trade_no());
                //更新任务记录
                mqUtils.sendTaskMQ(userId,1,10);
                return "success";
            }else{//不通过
                log.info("用户["+weixinSignBean.getAttach()+"]充值失败，验签失败,订单号为："+weixinSignBean.getOut_trade_no());
                return "fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    /***
     * 银联回调验签接口
     * @param unionpayBean
     * @return
     */
    @Override
    public String checkUnionPaySign(@RequestBody UnionpayBean unionpayBean) {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String encoding = request.getParameter(SDKConstants.param_encoding);
            // 获取银联通知服务器发送的后台通知参数
            Map<String, String> reqParam = new HashMap<String, String>();
            Enumeration<?> temp = request.getParameterNames();
            if (null != temp) {
                while (temp.hasMoreElements()) {
                    String en = (String) temp.nextElement();
                    String value = request.getParameter(en);
                    reqParam.put(en, value);
                    //在报文上送时，如果字段的值为空，则不上送<下面的处理为在获取所有参数数据时，判断若值为空，则删除这个字段>
                    //System.out.println("ServletUtil类247行  temp数据的键=="+en+"     值==="+value);
                    if (null == reqParam.get(en) || "".equals(reqParam.get(en))) {
                        reqParam.remove(en);
                    }
                }
            }
            Map<String, String> valideData = null;
            if (null != reqParam && !reqParam.isEmpty()) {
                Iterator<Entry<String, String>> it = reqParam.entrySet().iterator();
                valideData = new HashMap<String, String>(reqParam.size());
                while (it.hasNext()) {
                    Entry<String, String> e = it.next();
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    value = new String(value.getBytes(encoding), encoding);
                    valideData.put(key, value);
                }
            }
            //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
            if (!AcpService.validate(valideData, encoding)) {
                //验签失败，需解决验签问题
                log.info("充值失败,银联回调验签返回数据为空!");
                return "fail";
            } else {
                //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
                long userId =Long.parseLong(valideData.get("reqReserved"));
                String orderId =valideData.get("orderId"); //获取后台通知的数据，其他字段也可用类似方式获取
                int total_fee = Integer.parseInt(valideData.get("txnAmt"));//价格 单位分
                //执行回调业务处理
                //获取未支付的订单
                Map<String,Object> rechargeOrderMap = redisUtils.hmget(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+orderId);
                if(rechargeOrderMap==null||rechargeOrderMap.size()<=0||Integer.parseInt(rechargeOrderMap.get("payStatus").toString())!=0){
                    //缓存中不存在 查询数据库
                    RechargeOrder rechargeOrder = rechargeOrderService.findRechargeOrder(userId,orderId);
                    if(rechargeOrder==null){//不存在
                        log.info("银联-用户["+userId+"]验签失败,订单号：["+orderId+"]的订单信息不存在，无法进行验签操作!");
                        return "ok";
                    }
                    if(rechargeOrder.getPayStatus()==1){//已支付
                        log.info("银联-用户["+userId+"]验签失败,订单号：["+orderId+"]的订单状态已支付，无法进行验签操作");
                        return "ok";
                    }
                    //更新缓存
                    redisUtils.hmset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+rechargeOrder.getOrderNumber(),CommonUtils.objectToMap(rechargeOrder),Constants.TIME_OUT_MINUTE_60_24_1);
                }
                //更改状态 防止重复支付
                redisUtils.hset(Constants.REDIS_KEY_PAY_ORDER_RECHARGE+userId+"_"+orderId,"payStatus",1);
                //更新钱包和交易明细
                double t_amount = total_fee/100.0;//单位是分 所以需要除以100
                mqUtils.sendPurseMQ(userId,0,0,t_amount);
                //更新数据库 修改订单状态
                rechargeOrderService.updateRechargeOrder(userId,orderId);
                //更新任务记录
                mqUtils.sendTaskMQ(userId,1,10);
                return "ok";
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            log.info("验签失败，请重新充值!");
            return "fail";
        }catch (Exception e){
            e.printStackTrace();
            log.info("验签失败，请重新充值!");
            return "fail";
        }
    }
}
