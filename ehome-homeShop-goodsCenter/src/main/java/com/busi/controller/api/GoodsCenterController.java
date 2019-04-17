package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.service.GoodsCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品信息相关接口 如：发布商品 管理商品 商品上下架等等
 * author：SunTianJie
 * create time：2019/4/17 15:31
 */
@RestController
public class GoodsCenterController extends BaseController implements GoodsCenterApiController{

    @Autowired
    private GoodsCenterService goodsCenterService;
}
