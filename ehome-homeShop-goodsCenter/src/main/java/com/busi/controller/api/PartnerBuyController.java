package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.PartnerBuyService;
import com.busi.service.ShopFloorOrdersService;
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
 * 合伙购相关接口
 * author：ZhaoJiaJie
 * create time：2020-04-17 17:25:58
 */
@RestController
public class PartnerBuyController extends BaseController implements PartnerBuyApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    private PartnerBuyService goodsCenterService;

    @Autowired
    ShopFloorOrdersService shopFloorOrdersService;

    /***
     * 发起合伙购
     * @param homeShopGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addPartnerBuy(@Valid @RequestBody PartnerBuyGoods homeShopGoods, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //处理特殊字符
        String title = homeShopGoods.getGoodsTitle();
        if (!CommonUtils.checkFull(title)) {
            String filteringTitle = CommonUtils.filteringContent(title);
            if (CommonUtils.checkFull(filteringTitle)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "标题不能为空并且不能包含非法字符！", new JSONArray());
            }
            homeShopGoods.setGoodsTitle(filteringTitle);
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, homeShopGoods.getProvince(), homeShopGoods.getCity(), homeShopGoods.getDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(homeShopGoods.getUserId());
        if (userInfo != null) {
            homeShopGoods.setPersonnel("#" + homeShopGoods.getUserId() + "#" + userInfo.getName() + userInfo.getHead());
        }
        homeShopGoods.setReleaseTime(new Date());
        homeShopGoods.setNumber(1);
        goodsCenterService.add(homeShopGoods);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeShopGoods.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询列表
     * @param sort  查询条件:0全部，1我发起的，2我参与的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findPartnerBuyList(@PathVariable int sort, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<PartnerBuyGoods> pageBean = null;
        pageBean = goodsCenterService.findDishesSortList(sort, CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public ReturnData getPartnerBuy(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        PartnerBuyGoods posts = null;
        posts = goodsCenterService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(posts.getUserId());
        if (userInfo != null) {
            posts.setName(userInfo.getName());
            posts.setHead(userInfo.getHead());
            posts.setProTypeId(userInfo.getProType());
            posts.setHouseNumber(userInfo.getHouseNumber());
        }
        Map<String, Object> map = CommonUtils.objectToMap(posts);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 加入合伙购
     * @param no 订单编号
     * @param id 合伙购ID
     * @return
     */
    @Override
    public ReturnData joinPartnerBuy(@PathVariable String no, @PathVariable long id) {
        //查询缓存 缓存中不存在 查询数据库
        UserInfo userInfo = null;
        ShopFloorOrders io = null;
        Map<String, Object> ordersMap = redisUtils.hmget(Constants.REDIS_KEY_SHOPFLOORORDERS + CommonUtils.getMyId() + "_" + no);
        if (ordersMap == null || ordersMap.size() <= 0) {
            io = shopFloorOrdersService.findNo(no);
            if (io == null) {
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
            }
            userInfo = userInfoUtils.getUserInfo(io.getBuyerId());
            if (userInfo != null) {
                io.setName(userInfo.getName());
                io.setHead(userInfo.getHead());
                io.setProTypeId(userInfo.getProType());
                io.setHouseNumber(userInfo.getHouseNumber());
            }
            //放入缓存
            ordersMap = CommonUtils.objectToMap(io);
            redisUtils.hmset(Constants.REDIS_KEY_SHOPFLOORORDERS + io.getBuyerId() + "_" + no, ordersMap, Constants.USER_TIME_OUT);
        }
        ShopFloorOrders ik = (ShopFloorOrders) CommonUtils.mapToObject(ordersMap, ShopFloorOrders.class);
        if (ik != null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "您要查看的订单不存在", new JSONObject());
        }
        if (ik.getOrdersType() == 1) {
            PartnerBuyGoods posts = null;
            posts = goodsCenterService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            if (posts.getState() == 1) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            userInfo = userInfoUtils.getUserInfo(ik.getBuyerId());
            if (userInfo != null) {
                posts.setPersonnel(posts.getPersonnel() + ";" + "#" + ik.getBuyerId() + "#" + userInfo.getName() + userInfo.getHead());
            }
            posts.setNumber(posts.getNumber() + 1);
            goodsCenterService.update(posts);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新合伙购状态
     * @param partnerBuyGoods
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData changePartnerBuy(@Valid @RequestBody PartnerBuyGoods partnerBuyGoods, BindingResult bindingResult) {
        partnerBuyGoods.setState(1);
        goodsCenterService.update(partnerBuyGoods);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", partnerBuyGoods.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
