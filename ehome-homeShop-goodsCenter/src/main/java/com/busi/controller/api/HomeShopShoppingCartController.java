package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HomeShopShoppingCart;
import com.busi.entity.ReturnData;
import com.busi.service.HomeShopShoppingCartService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * 二货商城商品购物车相关接口
 * author：ZhaoJiaJie
 * create time：2020-07-13 13:25:13
 */
@RestController
public class HomeShopShoppingCartController extends BaseController implements HomeShopShoppingCartApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private HomeShopShoppingCartService goodsCenterService;

    /***
     * 统计购物车商品
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHSCartNum(@PathVariable long userId) {
        int num = 0;
        num = goodsCenterService.findNum(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 加购物车
     * @param shopFloorGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHShoppingCart(@Valid @RequestBody HomeShopShoppingCart shopFloorGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断购物车商品总数（最多一千种）
        int num = 0;
        num = goodsCenterService.findNum(shopFloorGoods.getUserId());
        if (num >= 1000) {//超出数量限制
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        if (shopFloorGoods.getNumber() >= 1) {
            HomeShopShoppingCart shoppingCart = null;
            shoppingCart = goodsCenterService.findId(shopFloorGoods.getUserId(), shopFloorGoods.getId());
            if (shoppingCart != null) {
                if (shoppingCart.getDeleteType() > 0) {//判断是否已删除
                    shoppingCart.setDeleteType(0);
                }
                goodsCenterService.update(shopFloorGoods);
            } else {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
        } else {
            String ids = shopFloorGoods.getId() + "";
            goodsCenterService.updateDels(ids.split(","));
        }
        //清空缓存列表
        redisUtils.expire(Constants.REDIS_KEY_HOMESHOP_CARTLIST + shopFloorGoods.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新购物车
     * @param shopFloorGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changeHShoppingCart(@Valid @RequestBody HomeShopShoppingCart shopFloorGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断购物车商品总数（最多一千种）
        int num = 0;
        num = goodsCenterService.findNum(shopFloorGoods.getUserId());
        if (num >= 1000) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        HomeShopShoppingCart shoppingCart = null;
        shoppingCart = goodsCenterService.findGoodsId(shopFloorGoods.getUserId(), shopFloorGoods.getGoodsId());
        if (shoppingCart == null) {//新增
            if (CommonUtils.checkFull(shopFloorGoods.getGoodsTitle()) || CommonUtils.checkFull(shopFloorGoods.getGoodsCoverUrl())) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            shopFloorGoods.setNumber(1);
            shopFloorGoods.setAddTime(new Date());
            goodsCenterService.add(shopFloorGoods);
        } else {//更新
            if (shoppingCart.getDeleteType() > 0) {//已删除的
                shoppingCart.setDeleteType(0);
            }
            shoppingCart.setNumber(shoppingCart.getNumber() + 1);
            goodsCenterService.update(shoppingCart);
        }
        //清空缓存列表
        redisUtils.expire(Constants.REDIS_KEY_HOMESHOP_CARTLIST + shopFloorGoods.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @param userId
     * @Description: 删除购物车商品
     * @return:
     */
    @Override
    public ReturnData delHShoppingCart(@PathVariable String ids, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的商品信息", new JSONObject());
        }
        goodsCenterService.updateDels(ids.split(","));
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_HOMESHOP_CARTLIST + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询购物车
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findHShoppingCart(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        List<HomeShopShoppingCart> cartList = new ArrayList<>();
        cartList = redisUtils.getList(Constants.REDIS_KEY_HOMESHOP_CARTLIST + userId, 0, -1);
        if (cartList == null || cartList.size() <= 0) {
            //查询数据库
            cartList = goodsCenterService.findList(userId);
            if (cartList != null && cartList.size() > 0) {
                //更新到缓存
                redisUtils.pushList(Constants.REDIS_KEY_HOMESHOP_CARTLIST + userId, cartList);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, cartList);
    }

    /***
     * 查询已删除商品
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findHSDeleteGoods(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        List<HomeShopShoppingCart> newList = null;
        List<HomeShopShoppingCart> cartList = null;
        cartList = goodsCenterService.findDeleteGoods(userId);
        if (cartList != null && cartList.size() > 10) {
            newList = cartList.subList(0, 10);//取前10条数据
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, newList);
        } else {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, cartList);
        }
    }
}
