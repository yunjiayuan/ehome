package com.busi.utils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;

@Slf4j
public class WechatpayUtil{
    
    /**
     * @param model
     *            微信接口请求参数DTO对象
     * @return ResultEntity 返回结构体
     */
    public static void doTransfers(TransfersDto model) {
        try{
            // 1.计算参数签名
            String paramStr = WechatpayUtil.createLinkString(model);
            String mysign = paramStr + "&key=" + Constants.WEIXIN_API_KEY;
            String sign = DigestUtils.md5Hex(mysign).toUpperCase();

            // 2.封装请求参数
            StringBuilder reqXmlStr = new StringBuilder();
            reqXmlStr.append("<xml>");
            reqXmlStr.append("<mchid>" + model.getMchid() + "</mchid>");
            reqXmlStr.append("<mch_appid>" + model.getMch_appid() + "</mch_appid>");
            reqXmlStr.append("<nonce_str>" + model.getNonce_str() + "</nonce_str>");
            reqXmlStr.append("<check_name>" + model.getCheck_name() + "</check_name>");
            reqXmlStr.append("<openid>" + model.getOpenid() + "</openid>");
            reqXmlStr.append("<amount>" + model.getAmount() + "</amount>");
            reqXmlStr.append("<desc>" + model.getDesc() + "</desc>");
            reqXmlStr.append("<sign>" + sign + "</sign>");
            reqXmlStr.append("<partner_trade_no>" + model.getPartner_trade_no() + "</partner_trade_no>");
            reqXmlStr.append("<spbill_create_ip>" + model.getSpbill_create_ip() + "</spbill_create_ip>");
            reqXmlStr.append("</xml>");

            // 3.加载证书请求接口
            String result = HttpRequestHandler.httpsRequest(Constants.WEIXIN_URL, reqXmlStr.toString(),model, Constants.WEIXIN_CERT_PATH);
            if(result.contains("CDATA[SUCCESS]")){
                log.error("调用微信同步提现业务成功");
            }else{
                log.error("调用微信同步提现业务失败"+result);
            }
        }
        catch (Exception e){
            e.printStackTrace();
            log.error("调用微信同步提现业务失败"+e.getMessage());
        }
    }
    
    private static String createLinkString(TransfersDto model)
    {
        // 微信签名规则 https://pay.weixin.qq.com/wiki/doc/api/tools/mch_pay.php?chapter=4_3
        Map<String, Object> paramMap = new HashMap<String, Object>();
        
        // 订单号默认用商户号+时间戳+4位随机数+可以根据自己的规则进行调整
        model.setAppkey(Constants.WEIXIN_API_KEY);
        model.setNonce_str(WechatpayUtil.getNonce_str());
        model.setPartner_trade_no(model.getMchid()
                                  + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                                  + (int)((Math.random() * 9 + 1) * 1000));
        
        paramMap.put("mch_appid", model.getMch_appid());
        paramMap.put("mchid", model.getMchid());
        paramMap.put("openid", model.getOpenid());
        paramMap.put("amount", model.getAmount());
        paramMap.put("check_name", model.getCheck_name());
        paramMap.put("desc", model.getDesc());
        paramMap.put("partner_trade_no", model.getPartner_trade_no());
        paramMap.put("nonce_str", model.getNonce_str());
        paramMap.put("spbill_create_ip", model.getSpbill_create_ip());
        
        List<String> keys = new ArrayList(paramMap.keySet());
        Collections.sort(keys);
        String prestr = "";
        for (int i = 0; i < keys.size(); i++ )
        {
            String key = keys.get(i);
            Object value = (Object)paramMap.get(key);
            if (i == keys.size() - 1)
            {// 拼接时，不包括最后一个&字符
                prestr = prestr + key + "=" + value;
            }
            else
            {
                prestr = prestr + key + "=" + value + "&";
            }
        }
        return prestr;
    }

    private static String getNonce_str()
    {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++ )
        {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

}
