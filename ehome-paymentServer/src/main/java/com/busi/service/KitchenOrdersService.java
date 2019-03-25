package com.busi.service;

import com.busi.controller.BaseController;
import com.busi.entity.Pay;
import com.busi.entity.ReturnData;
import com.busi.utils.MqUtils;
import com.busi.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 厨房支付业务
 * author：SunTianJie
 * create time：2019/3/22 16:17
 */
@Service
public class KitchenOrdersService extends BaseController implements PayBaseService{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /**
     * 具体支付业务
     * @param pay      支付具体实体
     * @param purseMap 账户实体集合
     * @return
     */
    @Override
    public ReturnData pay(Pay pay, Map<String, Object> purseMap) {
        return null;
    }
}
