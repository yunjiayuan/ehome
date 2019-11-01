package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.GoodsCenterService;
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
 * 商品信息相关接口 如：发布商品 管理商品 商品上下架等等
 * author：ZhaoJiaJie
 * create time：2019-7-25 13:46:18
 */
@RestController
public class GoodsCenterController extends BaseController implements GoodsCenterApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    private GoodsCenterService goodsCenterService;

    /***
     * 发布商品
     * @param homeShopGoods
     * @return
     */
    @Override
    public ReturnData addShopGoods(@Valid @RequestBody HomeShopGoods homeShopGoods, BindingResult bindingResult) {
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
        homeShopGoods.setAuditType(1);
        homeShopGoods.setSellType(1);
        homeShopGoods.setReleaseTime(new Date());
        homeShopGoods.setRefreshTime(new Date());
        goodsCenterService.add(homeShopGoods);

        //新增商品对应属性
        GoodsProperty property = new GoodsProperty();
        property.setGoodsId(homeShopGoods.getId());
        property.setName(homeShopGoods.getPropertyName());
//        property.setValue(homeShopGoods.getPropertyValue());
        goodsCenterService.addProperty(property);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeShopGoods.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新商品
     * @param homeShopGoods
     * @return
     */
    @Override
    public ReturnData changeShopGoods(@Valid @RequestBody HomeShopGoods homeShopGoods, BindingResult bindingResult) {
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
        //验证修改人权限
        if (CommonUtils.getMyId() != homeShopGoods.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + homeShopGoods.getUserId() + "]的商品信息", new JSONObject());
        }
        // 查询数据库
        HomeShopGoods posts = goodsCenterService.findUserById(homeShopGoods.getId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        homeShopGoods.setRefreshTime(new Date());
        goodsCenterService.update(homeShopGoods);

        //更新商品对应属性
        GoodsProperty property = new GoodsProperty();
        property.setGoodsId(homeShopGoods.getId());
        property.setName(homeShopGoods.getPropertyName());
//        property.setValue(homeShopGoods.getPropertyValue());
        goodsCenterService.updateProperty(property);

        if (!CommonUtils.checkFull(homeShopGoods.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeShopGoods.getUserId(), homeShopGoods.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", homeShopGoods.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 批量上下架商品
     * @param ids 商品ids(逗号分隔)
     * @param sellType 商品买卖状态 : 0上架，1下架
     * @return
     */
    @Override
    public ReturnData changeShopGoods(@PathVariable String ids, @PathVariable long userId, @PathVariable int sellType) {
        //验证参数
        if (userId <= 0 || CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限上下架用户[" + userId + "]的商品信息", new JSONObject());
        }
        goodsCenterService.changeShopGoods(ids.split(","), sellType);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * @Description: 批量删除商品
     * @return:
     */
    @Override
    public ReturnData delShopGoods(@PathVariable String ids, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的商品信息", new JSONObject());
        }
        goodsCenterService.updateDels(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public ReturnData getShopGoods(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        int num = 0;
        HomeShopGoods posts = null;
        Map<String, Object> otherPostsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_USEDDEAL + id + 0);
        if (otherPostsMap == null || otherPostsMap.size() <= 0) {
            posts = goodsCenterService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //商品是下架状态或者查看者不是本人时禁止查看
            if (posts.getSellType() != 0) {
                if (posts.getUserId() != CommonUtils.getMyId()) {
                    return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "您要查看的商品已下架或已被主人删除", new JSONObject());
                }
            }
            UserInfo userInfo = null;
            userInfo = userInfoUtils.getUserInfo(posts.getUserId());
            //查询商品对应属性
            GoodsProperty property = goodsCenterService.findProperty(id);
            posts.setPropertyName(property.getName());
//            posts.setPropertyValue(property.getValue());
            num = goodsCenterService.findNum(userInfo.getUserId(), 1);//已上架
            posts.setSellingNumber(num);
            if (userInfo != null) {
                posts.setName(userInfo.getName());
                posts.setHead(userInfo.getHead());
                posts.setProTypeId(userInfo.getProType());
                posts.setHouseNumber(userInfo.getHouseNumber());
            }
            //放入缓存
            otherPostsMap = CommonUtils.objectToMap(posts);
        } else {
            posts = (HomeShopGoods) CommonUtils.mapToObject(otherPostsMap, HomeShopGoods.class);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //商品是下架状态或者查看者不是本人时禁止查看
            if (posts.getSellType() != 0) {
                if (posts.getUserId() != CommonUtils.getMyId()) {
                    return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "您要查看的商品已下架或已被主人删除", new JSONObject());
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", otherPostsMap);
    }

    /***
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  查询条件:-1全部  0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param time  时间：0倒序 1正序
     * @param goodsSort  分类
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findGoodsList(@PathVariable int sort, @PathVariable long shopId, @PathVariable int stock, @PathVariable int time, @PathVariable long goodsSort, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeShopGoods> pageBean = null;
        pageBean = goodsCenterService.findDishesSortList(sort, shopId, stock, time, goodsSort, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 新增分类
     * @param goodsSort
     * @return
     */
    @Override
    public ReturnData addGoodsSort(@Valid @RequestBody GoodsSort goodsSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //判断该用户分类数量 最多20个
        int num = goodsCenterService.findSortNum(goodsSort.getShopId());
        if (num >= 20) {
            return returnData(StatusCode.CODE_DISHESSORT_KITCHEN_ERROR.CODE_VALUE, "分类超过上限,拒绝新增！", new JSONObject());
        }
        goodsCenterService.addGoodsSort(goodsSort);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 修改分类
     * @param goodsSort
     * @return
     */
    @Override
    public ReturnData changeGoodsSort(@Valid @RequestBody GoodsSort goodsSort, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        goodsCenterService.changeGoodsSort(goodsSort);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 批量修改商品分类
     * @param ids 商品id
     * @return
     */
    @Override
    public ReturnData editGoodsSort(@PathVariable String ids, @PathVariable long sortId, @PathVariable String sortName) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        goodsCenterService.editGoodsSort(ids.split(","), sortId, sortName);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除分类
     * @return:
     */
    @Override
    public ReturnData delGoodsSort(@PathVariable String ids) {
        //验证参数
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        goodsCenterService.delGoodsSort(ids.split(","));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询分类列表
     * @param id  店铺
     * @param find  0默认所有 1一级分类 2二级分类
     * @param sortId  分类ID(仅查询二级分类有效)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData getGoodsSortList(@PathVariable long id, @PathVariable int find, @PathVariable int sortId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<GoodsSort> pageBean = null;
        pageBean = goodsCenterService.getGoodsSortList(id, find, sortId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 新增商品描述
     * @param goodsDescribe
     * @return
     */
    @Override
    public ReturnData addGoodsDescribe(@Valid @RequestBody GoodsDescribe goodsDescribe, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        goodsCenterService.addGoodsDescribe(goodsDescribe);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", goodsDescribe.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新商品描述
     * @param goodsDescribe
     * @return
     */
    @Override
    public ReturnData changeGoodsDescribe(@Valid @RequestBody GoodsDescribe goodsDescribe, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        goodsCenterService.changeGoodsDescribe(goodsDescribe);
        if (!CommonUtils.checkFull(goodsDescribe.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(goodsDescribe.getUserId(), goodsDescribe.getDelImgUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除商品描述
     * @return:
     */
    @Override
    public ReturnData delGoodsDescribe(@PathVariable long id, @PathVariable long userId) {
        //数据库删除
        goodsCenterService.delGoodsDescribe(id, userId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询商品描述
     * @param id
     * @return
     */
    @Override
    public ReturnData getGoodsDescribe(@PathVariable long id) {
        GoodsDescribe dishes = goodsCenterService.disheSdetails(id);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", dishes);
    }
}
