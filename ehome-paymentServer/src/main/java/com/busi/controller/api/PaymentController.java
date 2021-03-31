package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 支付相关接口
 * author：SunTianJie
 * create time：2018/8/23 9:55
 */
@RestController
public class PaymentController extends BaseController implements PaymentApiController {

    @Autowired
    private RedisUtils redisUtils;

    private PayBaseService payBaseService;

    @Autowired
    private ExchangeOrderService exchangeOrderService;

    @Autowired
    private MemberOrderService memberOrderService;

    @Autowired
    private UserBankCardInfoService userBankCardInfoService;

    @Autowired
    private PurseInfoService purseInfoService;

    @Autowired
    private PursePayPasswordService pursePayPasswordService;

    @Autowired
    private RedPacketsInfoOrderService redPacketsInfoOrderService;

    @Autowired
    private UsedDealOrdersService usedDealOrdersService;

    @Autowired
    private KitchenOrdersService kitchenOrdersService;

    @Autowired
    private KitchenBookedOrdersService kitchenBookedOrdersService;

    @Autowired
    private SelfChannelVipLOrderService selfChannelVipOrderService;

    @Autowired
    private GoodNumberOrderService goodNumberOrderService;

    @Autowired
    private HourlyWorkerOrdersService hourlyWorkerOrdersService;

    @Autowired
    private ShopFloorBondOrdersService shopFloorBondOrdersService;

    @Autowired
    private ShopFloorOrdersService shopFloorOrdersService;

    @Autowired
    private ShopFloorMasterOrdersService shopFloorMasterOrdersService;

    @Autowired
    private ConsultationOrdersService consultationOrdersService;

    @Autowired
    private TransferAccountsInfoOrderService transferAccountsInfoOrderService;

    @Autowired
    private CashOutOrderService cashOutOrderService;

    @Autowired
    private TravelOrdersService travelOrdersService;

    @Autowired
    private HotelOrdersService hotelOrdersService;

    @Autowired
    private PharmacyOrderService pharmacyOrderService;

    @Autowired
    private HotelTourismBookedOrderService hotelTourismBookedOrderService;

    @Autowired
    private DoorwayBusinessOrderService doorwayBusinessOrderService;

    @Autowired
    private TalkToSomeoneOrderService talkToSomeoneOrderService;

    @Autowired
    private RentAhouseOrdersLService rentAhouseOrdersLService;
    /***
     * 获取私钥  一次一密，10分钟有效，使用后失效，只能使用一次
     * @return
     */
    @Override
    public ReturnData getPaymentKey() {
        String payKey = CommonUtils.strToMD5(CommonUtils.getToken()+CommonUtils.getClientId()+new Date().getTime()+CommonUtils.getRandom(6,0), 16);//支付秘钥key
        redisUtils.set(Constants.REDIS_KEY_PAYMENT_PAYKEY+CommonUtils.getMyId(),payKey,Constants.MSG_TIME_OUT_MINUTE_10);
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("paymentKey",payKey);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 查询支付密码设置信息和银行卡绑定信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findPayPasswordInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //验证身份
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误,无权限进行此操作",new JSONObject());
        }
        int payPwdStatus = 0;//0表示该账号尚未设置过支付密码，1表示已设置过支付密码
        int bindBankCardStatus = 0;//0银行卡未绑定，1表示银行卡已绑定
        Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+userId );
        if(payPasswordMap==null||payPasswordMap.size()<=0){
            PursePayPassword pursePayPassword = null;
            //缓存中没有用户对象信息 查询数据库
            pursePayPassword = pursePayPasswordService.findPursePayPassword(userId);
            if(pursePayPassword==null){
                pursePayPassword = new PursePayPassword();
                pursePayPassword.setUserId(userId);
            }else{
                payPwdStatus=1;
                pursePayPassword.setRedisStatus(1);
            }
            //更新缓存
            payPasswordMap = CommonUtils.objectToMap(pursePayPassword);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+userId,payPasswordMap,Constants.USER_TIME_OUT);
        }else{
            if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==1) {//数据库中有对应记录
                payPwdStatus=1;
            }
        }
        Map<String,Object> bankMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_BANKCARD+userId );
        if(bankMap==null||bankMap.size()<=0){
            UserBankCardInfo userBankCardInfo = null;
            //缓存中没有用户对象信息 查询数据库
            userBankCardInfo = userBankCardInfoService.findUserBankCardInfo(userId);
            if(userBankCardInfo==null){
                userBankCardInfo = new UserBankCardInfo();
                userBankCardInfo.setUserId(userId);
            }else{
                bindBankCardStatus=1;
                userBankCardInfo.setRedisStatus(1);
            }
            //更新缓存
            bankMap = CommonUtils.objectToMap(userBankCardInfo);
            redisUtils.hmset(Constants.REDIS_KEY_PAYMENT_BANKCARD+userId,bankMap,Constants.USER_TIME_OUT);
        }else{
            if(Integer.parseInt(bankMap.get("redisStatus").toString())==1) {//数据库中有对应记录
                bindBankCardStatus=1;
            }
        }
        //响应客户端
        Map<String,Object> map = new HashMap<>();
        map.put("payPwdStatus",payPwdStatus);
        map.put("bindBankCardStatus",bindBankCardStatus);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /**
     * 全平台统一支付接口
     * @param pay
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData pay(@Valid @RequestBody Pay pay, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证操作人权限
        if(CommonUtils.getMyId()!=pay.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限操作用户["+pay.getUserId()+"]的交易功能",new JSONObject());
        }
        //添加防暴力验证
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_PAY_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PAYPASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的支付密码错误次数过多，系统已自动停用支付功能一天，如有疑问请联系官方客服",new JSONObject());
        }
        //检测账户信息 是否正常
        Map<String,Object> purseMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PURSEINFO+pay.getUserId() );
        if(pay.getServiceType()!=4){//拆红包 不需要验证
            if(purseMap==null||purseMap.size()<=0){
                Purse purse = null;
                //缓存中没有用户对象信息 查询数据库
                purse = purseInfoService.findPurseInfo(pay.getUserId());
                if(purse==null){
                    return returnData(StatusCode.CODE_PURSE_NOT_ENOUGH_ERROR.CODE_VALUE,"您账户余额不足，无法进行相关支付操作",new JSONObject());
                }
                purseMap = CommonUtils.objectToMap(purse);
            }
        }

        //验证支付秘钥是否正确
        Object serverKey = redisUtils.getKey(Constants.REDIS_KEY_PAYMENT_PAYKEY+pay.getUserId());
        if(serverKey==null){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1);
            }
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE,"操作已过期，秘钥已过期，请稍后重试!",new JSONObject());
        }
        if(!pay.getPaymentKey().equals(serverKey.toString())){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1);
            }
            //清除秘钥
            redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY+pay.getUserId(),0);
            return returnData(StatusCode.CODE_TIME_OUT_ERROR.CODE_VALUE,"秘钥不正确，请稍后重试!",new JSONObject());
        }
        //清除秘钥
        redisUtils.expire(Constants.REDIS_KEY_PAYMENT_PAYKEY+pay.getUserId(),0);
        //检测是否设置过支付密码
        if(pay.getServiceType()!=4&&pay.getServiceType()!=21){//拆红包和接收转账 不需要支付密码
            Map<String,Object> payPasswordMap = redisUtils.hmget(Constants.REDIS_KEY_PAYMENT_PAYPASSWORD+pay.getUserId() );
            if(payPasswordMap==null||payPasswordMap.size()<=0){
                PursePayPassword ppp = null;
                //缓存中没有用户对象信息 查询数据库
                ppp = pursePayPasswordService.findPursePayPassword(pay.getUserId());
                if(ppp==null){
                    return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法进行当前操作!",new JSONObject());
                }
                payPasswordMap = CommonUtils.objectToMap(ppp);
            }else{
                if(Integer.parseInt(payPasswordMap.get("redisStatus").toString())==0) {//redisStatus==0说明数据中无此记录
                    return returnData(StatusCode.CODE_PAYPASSWORD_IS_NOT_EXIST_ERROR.CODE_VALUE,"您尚未设置过支付密码，无法进行当前操作!",new JSONObject());
                }
            }
            //支付密码是否正确
            String oldPassWord = CommonUtils.getPasswordBySalt(pay.getPayPassword(), payPasswordMap.get("payCode").toString());//生成加盐的新密码
            if(!oldPassWord.equals(payPasswordMap.get("payPassword").toString())){
                //密码有误 添加错误限制 防暴力破解
                if(CommonUtils.checkFull(errorCount)){//第一次错误
                    redisUtils.hset(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1,24*60*60);//设置1天后失效
                }else{
                    redisUtils.hashIncr(Constants.REDIS_KEY_PAY_ERROR_COUNT,pay.getUserId()+"",1);
                }
                return returnData(StatusCode.CODE_PAYPASSWORD_ERROR.CODE_VALUE,"您输入的支付密码有误",new JSONObject());
            }
        }
        //开始支付
        switch (pay.getServiceType()) {

            case 1://求助悬赏支付

                break;
            case 2://求助购买支付

                break;
            case 3://发个人红包
                payBaseService = redPacketsInfoOrderService;
                break;
            case 4://拆个人红包
                payBaseService = redPacketsInfoOrderService;
                break;
            case 5://钱包现金兑换家 币
                payBaseService = exchangeOrderService;
                break;
            case 6://购买创始元老级会员支付
                payBaseService = memberOrderService;
                break;
            case 7://公告栏需求汇二手购买订单支付
                payBaseService = usedDealOrdersService;
                break;
            case 8://家门口厨房购买订单支付
                payBaseService = kitchenOrdersService;
                break;
            case 9://购买元老级会员支付
                payBaseService = memberOrderService;
                break;
            case 10://购买普通会员支付
                payBaseService = memberOrderService;
                break;
            case 11://购买VIP高级会员支付
                payBaseService = memberOrderService;
                break;
            case 12://购买自频道会员支付
                payBaseService = selfChannelVipOrderService;
                break;
            case 13://购买靓号支付
                payBaseService = goodNumberOrderService;
                break;
            case 14://购买小时工支付
                payBaseService = hourlyWorkerOrdersService;
                break;
            case 15://购买订座点菜支付
                payBaseService = kitchenBookedOrdersService;
                break;
            case 16://缴纳开通楼店保证金订单支付
                payBaseService = shopFloorBondOrdersService;
                break;
            case 17://楼店用户购买订单支付
                payBaseService = shopFloorOrdersService;
                break;
            case 18://楼店商家补货订单支付
                payBaseService = shopFloorMasterOrdersService;
                break;
            case 19://医生律师咨询订单支付
                payBaseService = consultationOrdersService;
                break;
            case 20://发送转账
                payBaseService = transferAccountsInfoOrderService;
                break;
            case 21://接收转账
                payBaseService = transferAccountsInfoOrderService;
                break;
            case 22://提现到微信
                payBaseService = cashOutOrderService;
                break;
            case 23://提现到支付宝
                payBaseService = cashOutOrderService;
                break;
            case 24://提现到银行卡（预留）
                payBaseService = cashOutOrderService;
                break;
            case 25://支付旅游订单
                payBaseService = travelOrdersService;
                break;
            case 26://支付酒店民宿订单
                payBaseService = hotelOrdersService;
                break;
            case 27://支付买药订单
                payBaseService = pharmacyOrderService;
                break;
            case 28://支付旅游、酒店民宿中的订座订单
                payBaseService = hotelTourismBookedOrderService ;
                break;
            case 29://支付隐形商家中的商品订单
                payBaseService =  doorwayBusinessOrderService;
                break;
            case 30://支付找人倾诉中的订单
                payBaseService =  talkToSomeoneOrderService;
                break;
            case 31://支付需求汇中租房订单
                payBaseService =  rentAhouseOrdersLService;
                break;
            case 32://支付需求汇中购房订单
                payBaseService =  rentAhouseOrdersLService;
                break;
            default:
                break;
        }
        //执行支付业务
        return payBaseService.pay(pay,purseMap);
    }
}
