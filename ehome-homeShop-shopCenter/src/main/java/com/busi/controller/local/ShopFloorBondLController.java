package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloor;
import com.busi.service.ShopFloorService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 楼店保证金订单
 * @author: ZHaoJiaJie
 * @create: 2019-11-13 11:08:12
 */
@RestController
public class ShopFloorBondLController extends BaseController implements ShopFloorBondLocalController {


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopFloorService shopCenterService;

    /**
     * @program: ehome
     * @description: 更新楼店保证金支付状态
     * @author: ZHaoJiaJie
     * @create: 2019-11-13 11:08:09
     */
    @Override
    public ReturnData updatePayStates(@RequestBody ShopFloor shopFloor) {

        shopCenterService.updatePayStates(shopFloor);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + shopFloor.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
