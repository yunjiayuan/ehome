package com.busi.utils;


import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.CertAlipayRequest;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayFundTransUniTransferModel;
import com.alipay.api.domain.Participant;
import com.alipay.api.request.AlipayFundTransUniTransferRequest;
import com.alipay.api.request.AlipayUserInfoAuthRequest;
import com.alipay.api.response.AlipayFundTransUniTransferResponse;
import com.alipay.api.response.AlipayUserInfoAuthResponse;
import com.busi.entity.CashOutOrder;
import lombok.extern.slf4j.Slf4j;

/**
 * 支付宝工具类
 * authorsuntj
 * Create time 2020/7/13 14:56
 */
@Slf4j
public class AlipayUtils {

    /***
     * 提现到支付宝
     * @param cashOutOrder
     */
    public static int cashOutToAli( CashOutOrder cashOutOrder){
        //请求参数数据格式(仅json)
        String format = "json";
        //请求使用的编码格式,如utf-8,gbk,gb2312等
        String charset = "utf-8";
        //商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
        String signType = "RSA2";
        CertAlipayRequest certAlipayRequest = new CertAlipayRequest();
        certAlipayRequest.setServerUrl(Constants.ALIPAY_URL);
        certAlipayRequest.setAppId(Constants.ALIPAY_APPID);
        certAlipayRequest.setPrivateKey(Constants.ALIPAY_PRIVATE_KEY);
        certAlipayRequest.setFormat(format);
        certAlipayRequest.setCharset(charset);
        certAlipayRequest.setSignType(signType);
        //证书路径必须是绝对路径
        //应用公钥证书绝对路径
        certAlipayRequest.setCertPath(Constants.ALIPAY_PUBLIC_KEY);
        //支付宝公钥证书绝对路径
        certAlipayRequest.setAlipayPublicCertPath(Constants.ALIPAY_ALIPAYCERTPUBLICKEY_KEY);
        //支付宝根证书绝对路径
        certAlipayRequest.setRootCertPath(Constants.ALIPAY_ALIPAYROOTCERT_KEY);
        AlipayClient alipayClient = null;
        try {
            alipayClient = new DefaultAlipayClient(certAlipayRequest);

            AlipayFundTransUniTransferRequest request = new AlipayFundTransUniTransferRequest();

            Participant payeeInfo = new Participant();
            payeeInfo.setIdentity(cashOutOrder.getOpenid());
            payeeInfo.setIdentityType("ALIPAY_LOGON_ID");
            payeeInfo.setName(cashOutOrder.getName());

            AlipayFundTransUniTransferModel model = new AlipayFundTransUniTransferModel();
            model.setOutBizNo(cashOutOrder.getId());
            model.setTransAmount(cashOutOrder.getMoney()+"");
            model.setProductCode("TRANS_ACCOUNT_NO_PWD");
            model.setBizScene("DIRECT_TRANSFER");
            model.setOrderTitle("提现");
            model.setPayeeInfo(payeeInfo);
            request.setBizModel(model);

            AlipayFundTransUniTransferResponse  response = alipayClient.certificateExecute(request);
            if (response.isSuccess()) {
                log.info("调用支付宝提现成功!");
                return 0;
            } else {
                log.info("调用支付宝提现失败："+response.toString());
                return 1;
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("调用支付宝提现失败!");
        }
        return 1;
    }
    /***
     * 获取支付宝登录授权签名
     * 给客户端使用
     */
    public static String getLoginSign(){
        AlipayClient alipayClient = new DefaultAlipayClient(Constants.ALIPAY_URL,Constants.ALIPAY_APPID,Constants.ALIPAY_PRIVATE_KEY,"json","utf-8",Constants.ALIPAY_PUBLIC_KEY,"RSA2");
        AlipayUserInfoAuthRequest request = new AlipayUserInfoAuthRequest();
        request.setBizContent("{" +
                "      \"scopes\":[" +
                "        \"auth_base\"" +
                "      ]," +
                "\"state\":\"init\"" +
                "  }");
        AlipayUserInfoAuthResponse response = null;
        try {
            response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                String sign = "";
                sign = response.getBody().substring(response.getBody().indexOf("&sign=")+6,response.getBody().indexOf("&version="));
                log.info("调用支付宝获取登录签名成功："+sign);
                return sign;
            } else {
                log.info("调用支付宝获取登录签名失败");
            }
        } catch (AlipayApiException e) {
            e.printStackTrace();
            log.info("调用支付宝获取登录签名失败");
        }
        return "";
    }
}
