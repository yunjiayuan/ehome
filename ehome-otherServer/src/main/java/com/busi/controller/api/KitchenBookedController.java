package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Kitchen;
import com.busi.entity.KitchenBooked;
import com.busi.entity.ReturnData;
import com.busi.service.KitchenBookedService;
import com.busi.service.KitchenService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-26 17:51
 */
@RestController
public class KitchenBookedController extends BaseController implements KitchenBookedApiController {

    @Autowired
    KitchenBookedService kitchenBookedService;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 新增订座信息
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData addKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Kitchen io = kitchenService.findByUserId(kitchenBooked.getUserId());
        if (io == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //更新厨房订座状态
//        kitchenService.updateBookedState(io);
        //清除缓存中的信息
//        redisUtils.expire(Constants.REDIS_KEY_KITCHEN + io.getUserId(), 0);
        //新增厨房订座信息
        kitchenBooked.setKitchenId(io.getId());
        kitchenBookedService.add(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查看订座设置详情
     * @param userId  商家ID
     * @return
     */
    @Override
    public ReturnData findKitchenBooked(@PathVariable long userId) {
        KitchenBooked kitchen = kitchenBookedService.findByUserId(userId);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchen);
    }

    /***
     * 编辑订座设置
     * @param kitchenBooked
     * @return
     */
    @Override
    public ReturnData changeKitchenBooked(@Valid @RequestBody KitchenBooked kitchenBooked, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        kitchenBookedService.updateBooked(kitchenBooked);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
