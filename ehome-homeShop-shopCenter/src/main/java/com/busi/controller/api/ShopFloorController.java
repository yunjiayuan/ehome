package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloor;
import com.busi.entity.ShopFloorBondOrders;
import com.busi.entity.UserAccountSecurity;
import com.busi.service.ShopFloorService;
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
 * @program: ehome
 * @description: 楼店信息相关接口 如：创建店铺 修改店铺信息 更改店铺状态等
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:23
 */
@RestController
public class ShopFloorController extends BaseController implements ShopFloorApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopFloorService shopCenterService;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增店铺
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData addShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断是否已有店铺
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOOR + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ShopFloor ik = (ShopFloor) CommonUtils.mapToObject(kitchenMap, ShopFloor.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增楼店失败，楼店已存在！", new JSONObject());
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
    public ReturnData changeShopFloor(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
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
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + homeShopCenter.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Override
    public ReturnData updShopFloorStatus(@Valid @RequestBody ShopFloor homeShopCenter, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户是否实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(homeShopCenter.getUserId());
        if (CommonUtils.checkFull(userAccountSecurity.getRealName()) || CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            return returnData(StatusCode.CODE_NOT_REALNAME.CODE_VALUE, "该用户未实名认证", new JSONObject());
        }
        //查询是否缴费
        ShopFloor dishes = shopCenterService.findByUserId(CommonUtils.getMyId());
        if (dishes == null || dishes.getPayState() != 1) {
            return returnData(StatusCode.CODE_BOND_NOT_AC.CODE_VALUE, "未缴纳保证金", new JSONObject());
        }
        shopCenterService.updateBusiness(homeShopCenter);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR + homeShopCenter.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询店铺信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findShopFloor(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOOR + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        //判断该用户是否实名
        int realName = 0;//未实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(userId);
        if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) && !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
            realName = 1;//已实名
        }
        kitchenMap.put("realName", realName);//未实名
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", kitchenMap);
    }

    /***
     * 查询店铺状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData findFloorState(@PathVariable long userId) {
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> kitchenMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOOR + CommonUtils.getMyId());
        if (kitchenMap == null || kitchenMap.size() <= 0) {
            ShopFloor kitchen2 = shopCenterService.findByUserId(CommonUtils.getMyId());
            if (kitchen2 != null) {
                //放入缓存
                kitchenMap = CommonUtils.objectToMap(kitchen2);
                redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOOR + kitchen2.getUserId(), kitchenMap, Constants.USER_TIME_OUT);
            }
        }
        ShopFloor ik = (ShopFloor) CommonUtils.mapToObject(kitchenMap, ShopFloor.class);
        Map<String, Object> map2 = new HashMap<>();
        if (ik == null) {
            map2.put("shopState", -1);//楼店不存在
        } else {
            map2.put("infoId", ik.getId());
            map2.put("shopState", ik.getShopState());  //-1楼店不存在  0未开店  1已开店
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map2);
    }

    /***
     * 新增保证金订单
     * @param shopFloorBondOrders
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addBondOrder(@Valid ShopFloorBondOrders shopFloorBondOrders, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        long time = new Date().getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);
        shopFloorBondOrders.setMoney(30000);
        shopFloorBondOrders.setOrderNumber(noRandom);
        shopFloorBondOrders.setTime(new Date());
        shopFloorBondOrders.setUserId(CommonUtils.getMyId());
        //放入缓存 5分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(shopFloorBondOrders);
        redisUtils.hmset(Constants.REDIS_KEY_BONDORDER + CommonUtils.getMyId() + "_" + shopFloorBondOrders.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_5);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", noRandom);
    }
}
