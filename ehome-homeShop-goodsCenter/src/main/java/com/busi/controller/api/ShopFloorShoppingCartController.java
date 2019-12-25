package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorShoppingCart;
import com.busi.service.ShopFloorShoppingCartService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * @program: ehome
 * @description: 楼店购物车
 * @author: ZHaoJiaJie
 * @create: 2019-12-09 13:21
 */
@RestController
public class ShopFloorShoppingCartController extends BaseController implements ShopFloorShoppingCartApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopFloorShoppingCartService goodsCenterService;

    /***
     * 统计购物车商品
     * @param userId
     * @return
     */
    @Override
    public ReturnData findFSCartNum(@PathVariable long userId) {
        int num = 0;
        num = goodsCenterService.findNum(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新购物车
     * @param shopFloorGoods
     * @return
     */
    @Override
    public ReturnData changeFShoppingCart(@Valid @RequestBody ShopFloorShoppingCart shopFloorGoods, BindingResult bindingResult) {
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
            ShopFloorShoppingCart shoppingCart = null;
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
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + shopFloorGoods.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 加购物车
     * @param shopFloorGoods
     * @return
     */
    @Override
    public ReturnData addFShoppingCart(@Valid @RequestBody ShopFloorShoppingCart shopFloorGoods, BindingResult bindingResult) {
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
        ShopFloorShoppingCart shoppingCart = null;
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
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + shopFloorGoods.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除购物车商品
     * @return:
     */
    @Override
    public ReturnData delFShoppingCart(@PathVariable String ids, @PathVariable long userId) {
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
        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + userId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询购物车
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findFShoppingCart(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        List<ShopFloorShoppingCart> cartList = new ArrayList<>();
        cartList = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + userId, 0, -1);
        if (cartList == null || cartList.size() <= 0) {
            //查询数据库
            cartList = goodsCenterService.findList(userId);
            if (cartList != null && cartList.size() > 0) {
                //初始化一个map
//                Map<String, List<ShopFloorShoppingCart>> map = new HashMap<>();
//                for (ShopFloorShoppingCart user : cartList) {
//                    String key = user.getGoodsTitle();//店铺名称暂用标题代替
//                    if (map.containsKey(key)) {
//                        //map中存在以此id作为的key，将数据存放当前key的map中
//                        map.get(key).add(user);
//                    } else {
//                        //map中不存在以此id作为的key，新建key用来存放数据
//                        List<ShopFloorShoppingCart> userList = new ArrayList<>();
//                        userList.add(user);
//                        map.put(user.getGoodsTitle(), userList);//店铺名称暂用标题代替
//                    }
//                }
                //更新到缓存
                redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_CARTLIST + userId, cartList);
//                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, map);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, cartList);
    }
}
