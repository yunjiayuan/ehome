package com.busi.service;

import com.busi.entity.Pay;
import com.busi.entity.ReturnData;

import java.util.Map;

/**
 * 支付父类service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
public interface PayBaseService {

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    ReturnData pay(Pay pay,Map<String,Object> purseMap);
}
