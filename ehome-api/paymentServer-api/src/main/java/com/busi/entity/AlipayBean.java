package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 支付宝回调接口中返回的实体对象
 * author：SunTianJie
 * create time：2018/9/3 13:55
 */
@Getter
@Setter
public class AlipayBean {

    private String app_id;

    private String auth_app_id;

    private String body;

    private String buyer_id;

    private String buyer_logon_id;

    private String buyer_pay_amount;

    private String charset;

    private String fund_bill_list;

    private String gmt_create;

    private String gmt_payment;

    private String invoice_amount;

    private String notify_id;

    private String notify_time;

    private String notify_type;

    private String out_trade_no;//订单号

    private String point_amount;

    private String receipt_amount;

    private String seller_email;

    private String seller_id;

    private String sign;

    private String sign_type;

    private String subject;

    private String total_amount;

    private String trade_no;

    private String trade_status;

    private String version;

    private String passback_params;//此为用户自定义参数 目前只传 用户ＩＤ

}
