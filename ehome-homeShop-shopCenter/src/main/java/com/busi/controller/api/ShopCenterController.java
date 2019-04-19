package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.service.ShopCenterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

/**
 * 店铺信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * author：SunTianJie
 * create time：2019/4/17 15:31
 */
@RestController
public class ShopCenterController extends BaseController implements ShopCenterApiController {

    @Autowired
    private ShopCenterService shopCenterService;
}
