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
import java.util.HashMap;
import java.util.Map;

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
        String imgUrl = "";   //图片
        if (CommonUtils.checkFull(posts.getGoodsCoverUrl())) {
            String[] img = posts.getImgUrl().split(",");
            imgUrl = img[0];//用第一张图做封面
        } else {
            imgUrl = posts.getGoodsCoverUrl();//图片
        }
        collect.setImgUrl(imgUrl);
        //判断是否有折扣
        double cost = 0.00; //商品价格
        if (posts.getDiscountPrice() > 0) {
            cost = posts.getDiscountPrice();//折扣价
        } else {
            cost = posts.getPrice();//原价
        }
        collect.setPrice(cost);
        collectService.addCollection(collect);

        //更新收藏次数
        posts.setCollectionCount(posts.getCollectionCount() + 1);
        goodsCenterService.updateCollection(posts);
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

    /***
     * 统计我的收藏、浏览数量
     * @return
     */
    @Override
    public ReturnData getCLnumber() {
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
