package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HomeShopCenter;
import com.busi.entity.HomeShopPersonalData;
import com.busi.entity.ReturnData;
import com.busi.service.ShopCenterService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 店铺信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * author：ZHJJ
 * create time：2019/5/10 15:31
 */
@RestController
public class ShopCenterController extends BaseController implements ShopCenterApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopCenterService shopCenterService;

    /***
     * 新增店铺
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData addHomeShop(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有店铺
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOMESHOP + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            HomeShopCenter kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_HOMESHOP + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        HomeShopCenter ik = (HomeShopCenter) CommonUtils.mapToObject(kitchenMap, HomeShopCenter.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增家店失败，家店已存在！", new JSONObject());
        }
        homeShopCenter.setAddTime(new Date());
        homeShopCenter.setUserId(CommonUtils.getMyId());
        shopCenterService.addHomeShop(homeShopCenter);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("infoId", homeShopCenter.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 更新店铺
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData changeHomeShop(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        shopCenterService.updateHomeShop(homeShopCenter);
        if (!CommonUtils.checkFull(homeShopCenter.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeShopCenter.getUserId(), homeShopCenter.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOMESHOP + homeShopCenter.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData updHomeShopStatus(@Valid @RequestBody HomeShopCenter homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询是否认证通过
        HomeShopPersonalData dishes = shopCenterService.findPersonalData(homeShopCenter.getUserId());
        if (dishes != null && dishes.getAcState() == 3) {
            shopCenterService.updateBusiness(homeShopCenter);
            //清除缓存
            redisUtils.expire(Constants.REDIS_KEY_HOMESHOP + homeShopCenter.getUserId(), 0);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        } else {
            return returnData(StatusCode.CODE_PERSONALDATA_NOT_AC.CODE_VALUE, "个人信息未认证", new JSONObject());
        }
    }

    /***
     * 查询店铺信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHomeShop(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOMESHOP + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            HomeShopCenter kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_HOMESHOP + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询店铺营业状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHomeState(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_HOMESHOP + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            HomeShopCenter kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_HOMESHOP + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        HomeShopCenter ik = (HomeShopCenter) CommonUtils.mapToObject(kitchenMap, HomeShopCenter.class);
        Map<String, Object> map2 = new HashMap<>();
        if (ik == null) {
            map2.put("shopState", -1);//家店不存在
        } else {
            map2.put("shopState", ik.getShopState());  //-1家店不存在  0未开店  1已开店
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 新增个人信息
     * @param homeShopPersonalData
     * @return
     */
    @Override
    public ReturnData addPersonalData(@Valid @RequestBody HomeShopPersonalData homeShopPersonalData, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        homeShopPersonalData.setAddTime(new Date());
        homeShopPersonalData.setUserId(CommonUtils.getMyId());
        shopCenterService.addPersonalData(homeShopPersonalData);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeShopPersonalData.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新个人信息
     * @param homeShopPersonalData
     * @return
     */
    @Override
    public ReturnData changePersonalData(@Valid @RequestBody HomeShopPersonalData homeShopPersonalData, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        shopCenterService.updPersonalData(homeShopPersonalData);
        if (!CommonUtils.checkFull(homeShopPersonalData.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeShopPersonalData.getUserId(), homeShopPersonalData.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeShopPersonalData.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询个人信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findPersonalData(@PathVariable long userId) {
        HomeShopPersonalData dishes = shopCenterService.findPersonalData(userId);

        Map<String, Object> map = new HashMap<>();
        map.put("data", dishes);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 验证手机验证码
     * @param userId  用户Id
     * @param phone   手机号
     * @param code    验证码
     * @return
     */
    @Override
    public ReturnData verificationCode(@PathVariable long userId, @PathVariable String phone, @PathVariable String code) {
        //验证验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_HOMESHOP_USERINFO_CODE + userId + "_" + phone);
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        if (!serverCode.toString().equals(code)) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        //清除短信验证码
        redisUtils.expire(Constants.REDIS_KEY_USER_HOMESHOP_USERINFO_CODE + userId + "_" + phone, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询个人信息认证状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData findPersonalState(@PathVariable long userId) {
        HomeShopPersonalData dishes = shopCenterService.findPersonalData(userId);
        if (dishes == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "个人信息不存在！", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("acState", dishes.getAcState());// 认证状态:0未认证,1审核中,2未通过,3已认证
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
