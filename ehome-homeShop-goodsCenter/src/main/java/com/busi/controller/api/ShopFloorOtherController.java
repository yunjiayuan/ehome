package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ShopFloorGoodsService;
import com.busi.service.ShopFloorOtherService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * 楼店商品收藏浏览相关接口
 * author：ZhaoJiaJie
 * create time：2020-03-10 12:15:44
 */
@RestController
public class ShopFloorOtherController extends BaseController implements ShopFloorOtherApiController {

    @Autowired
    ShopFloorOtherService collectService;

    @Autowired
    private ShopFloorGoodsService goodsCenterService;

    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addCollect(@Valid @RequestBody ShopFloorGoodsCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        ShopFloorGoodsCollection collect1 = null;
        ShopFloorGoods posts = null;
        posts = goodsCenterService.findUserById(collect.getGoodsId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        collect1 = collectService.findUserId(collect.getGoodsId(), collect.getUserId());
        if (collect1 != null) {
            return returnData(StatusCode.CODE_IPS_COLLECTION.CODE_VALUE, "你已收藏过", new JSONObject());
        }
        collect.setTime(new Date());
        collect.setGoodsName(posts.getGoodsTitle());
        collect.setImgUrl(posts.getImgUrl());
        collect.setPrice(posts.getPrice());
        collectService.addCollection(collect);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @Description: 删除我的收藏
     * @return:
     */
    @Override
    public ReturnData delCollect(@PathVariable String ids) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //查询数据库
        int look = collectService.delCollection(ids.split(","), CommonUtils.getMyId());
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "收藏记录[" + ids + "]不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询我的收藏接口
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findCollect(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShopFloorGoodsCollection> pageBean;
        pageBean = collectService.findCollectionList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /**
     * @param ids
     * @Description: 删除我的浏览记录
     * @return:
     */
    @Override
    public ReturnData delLook(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //查询数据库
        int look = collectService.delLook(ids.split(","), CommonUtils.getMyId());
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "浏览记录[" + ids + "]不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 分页查询我的浏览记录接口
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findLook(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShopFloorGoodsLook> pageBean;
        pageBean = collectService.findLookList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }
}
