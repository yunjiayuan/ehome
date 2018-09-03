package com.busi.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * 银联回调接口中返回的实体对象
 * author：SunTianJie
 * create time：2018/9/3 13:57
 */
@Getter
@Setter
public class UnionpayBean {

    private String accessType;

    private String bizType;

    private String certId;

    private String currencyCode;

    private String encoding;

    private String merId;

    private String orderId;

    private String queryId;

    private String reqReserved;

    private String respCode;

    private String respMsg;

    private String settleAmt;

    private String settleCurrencyCode;

    private String settleDate;

    private String signMethod;

    private String signature;

    private String traceNo;

    private String traceTime;

    private String txnAmt;

    private String txnSubType;

    private String txnTime;

    private String txnType;
}
