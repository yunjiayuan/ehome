package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.GoodsCenterService;
import com.busi.service.HomeShopOtherService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
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
 * 二货商城商品收藏浏览相关接口
 * author：ZhaoJiaJie
 * create time：2020-07-13 15:07:59
 */
@RestController
public class HomeShopOtherController extends BaseController implements HomeShopOtherApiController {

    @Autowired
    HomeShopOtherService collectService;

    @Autowired
    private GoodsCenterService goodsCenterService;

    /***
     * 新增
     * @param collect
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addHSCollect(@Valid @RequestBody HomeShopGoodsCollection collect, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        HomeShopGoodsCollection collect1 = null;
        HomeShopGoods posts = null;
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
        if (!CommonUtils.checkFull(posts.getImgUrl())) {
            String[] img = posts.getImgUrl().split(",");
            collect.setImgUrl(img[0]);//用第一张图做封面
        }
        collect.setBasicDescribe(posts.getDetails());
        collect.setSpecs(posts.getSpecs());
        collect.setPrice(posts.getPrice());
        collectService.addCollection(collect);

        //更新收藏次数
//        posts.setCollectionCount(posts.getCollectionCount() + 1);
//        goodsCenterService.updateCollection(posts);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @param ids
     * @Description: 删除我的收藏
     * @return:
     */
    @Override
    public ReturnData delHSCollect(@PathVariable String ids) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //查询数据库
        int num = collectService.delCollection(ids.split(","), CommonUtils.getMyId());
        if (num <= 0) {
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
    public ReturnData findHSCollect(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeShopGoodsCollection> pageBean;
        pageBean = collectService.findCollectionList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * @Description: 删除我的浏览记录
     * @return:
     * @param ids
     */
    @Override
    public ReturnData delHSLook(@PathVariable String ids) {
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "ids参数有误", new JSONObject());
        }
        //查询数据库
        int num = collectService.delLook(ids.split(","), CommonUtils.getMyId());
        if (num <= 0) {
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
    public ReturnData findHSLook(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeShopGoodsLook> pageBean;
        pageBean = collectService.findLookList(CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 统计收藏、浏览次数
     * @return
     */
    @Override
    public ReturnData getHSNumber() {
        long userId = CommonUtils.getMyId();
        // 统计收藏量
        int collect = 0;
        collect = collectService.findUserCollect(userId);
        // 统计浏览量
        int look = 0;
        look = collectService.findUserLook(userId);

        Map<String, Integer> numMap = new HashMap<>();
        numMap.put("look", look);
        numMap.put("collect", collect);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", numMap);
    }
}
