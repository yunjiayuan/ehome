package com.busi.service;

import com.busi.dao.GoodsCenterDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 商品信息相关Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class GoodsCenterService {

    @Autowired
    private GoodsCenterDao goodsCenterDao;


}
