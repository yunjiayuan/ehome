package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ShopFloorGoodsService;
import com.busi.service.ShopFloorOrdersService;
import com.busi.service.ShopFloorOtherService;
import com.busi.service.ShopFloorShoppingCartService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 楼店商品信息相关接口 如：发布商品 管理商品 商品上下架等等
 * author：ZhaoJiaJie
 * create time：2019-11-19 13:31:20
 */
@RestController
public class ShopFloorGoodsController extends BaseController implements ShopFloorGoodsApiController {


    @Autowired
    MqUtils mqUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    ShopFloorOtherService collectService;

    @Autowired
    private ShopFloorGoodsService goodsCenterService;

    @Autowired
    ShopFloorOrdersService shopFloorOrdersService;

    @Autowired
    private ShopFloorShoppingCartService shopFloorShoppingCartService;

    /***
     * 发布商品
     * @param homeShopGoods
     * @return
     */
    @Override
    public ReturnData addFloorGoods(@Valid @RequestBody ShopFloorGoods homeShopGoods, BindingResult bindingResult) {
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
        homeShopGoods.setAuditType(1);
//        homeShopGoods.setSellType(1);
        homeShopGoods.setReleaseTime(new Date());
        homeShopGoods.setRefreshTime(new Date());
        goodsCenterService.add(homeShopGoods);

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
    public ReturnData changeFloorGoods(@Valid @RequestBody ShopFloorGoods homeShopGoods, BindingResult bindingResult) {
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
        homeShopGoods.setRefreshTime(new Date());
        goodsCenterService.update(homeShopGoods);

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
    public ReturnData changeFloorGoods(@PathVariable String ids, @PathVariable long userId, @PathVariable int sellType) {
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
    public ReturnData delFloorGoods(@PathVariable String ids, @PathVariable long userId) {
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
    public ReturnData getFloorGoods(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        ShopFloorGoods posts = null;
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
        if (userInfo != null) {
            posts.setName(userInfo.getName());
            posts.setHead(userInfo.getHead());
            posts.setProTypeId(userInfo.getProType());
            posts.setHouseNumber(userInfo.getHouseNumber());
        }
        //新增浏览记录
        ShopFloorGoodsLook look = new ShopFloorGoodsLook();
        look.setTime(new Date());
        look.setGoodsId(id);
        look.setUserId(CommonUtils.getMyId());
        look.setGoodsName(posts.getGoodsTitle());
        String imgUrl = "";   //图片
        if (CommonUtils.checkFull(posts.getGoodsCoverUrl())) {
            String[] img = posts.getImgUrl().split(",");
            imgUrl = img[0];//用第一张图做封面
        } else {
            imgUrl = posts.getGoodsCoverUrl();//图片
        }
        look.setImgUrl(imgUrl);
        //判断是否有折扣
        double cost = 0.00; //商品价格
        if (posts.getDiscountPrice() > 0) {
            cost = posts.getDiscountPrice();//折扣价
        } else {
            cost = posts.getPrice();//原价
        }
        look.setPrice(cost);
        look.setBasicDescribe(posts.getBasicDescribe());
        look.setSpecs(posts.getSpecs());
        collectService.addLook(look);
        posts.setLookCount(posts.getLookCount() + 1);
        goodsCenterService.updateSee(posts);
        int collection = 0;//是否收藏过此商品  0没有  1已收藏
        Map<String, Object> map = CommonUtils.objectToMap(posts);
        //验证是否收藏过
        ShopFloorGoodsCollection flag = collectService.findUserId(id, CommonUtils.getMyId());
        if (flag != null) {
            collection = 1;//1已收藏
        }
        map.put("collection", collection);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 分页查询商品(用户调用)
     * @param sort  排序条件:0默认销量倒序，1最新发布
     * @param discount  0全部，1只看折扣
     * @param price  默认-1不限制 0价格最低，1价格最高
     * @param stock  默认-1所有 0有货 1没货
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param levelOne  一级分类:默认值为0,-2为不限
     * @param levelTwo  二级分类:默认值为0,-2为不限
     * @param levelThree  三级分类:默认值为0,-2为不限
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findFloorGoodsList(@PathVariable int sort, @PathVariable int discount, @PathVariable int price, @PathVariable int stock, @PathVariable int minPrice, @PathVariable int maxPrice, @PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShopFloorGoods> pageBean = null;
        pageBean = goodsCenterService.findDishesSortList(discount, sort, price, stock, minPrice, maxPrice, levelOne, levelTwo, levelThree, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 分页查询商品（商家调用）
     * @param sort  查询条件:-1全部  0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param time  时间：0倒序 1正序
     * @param levelOne  一级分类:默认值为0,-2为不限
     * @param levelTwo  二级分类:默认值为0,-2为不限
     * @param levelThree  三级分类:默认值为0,-2为不限
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findFGoodsList(@PathVariable int sort, @PathVariable int stock, @PathVariable int time, @PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<ShopFloorGoods> pageBean = null;
        pageBean = goodsCenterService.findFGoodsList(sort, stock, time, levelOne, levelTwo, levelThree, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询推荐商品
     * @param type  类别: 0详情界面推荐 1购物车界面推荐，2我的界面猜你喜欢
     * @param levels  分类组合 1,2,3 type=0时有效
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findRecommendList(@PathVariable int type, @PathVariable String levels, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        String levelOne = "";           //商品1级分类
        String levelTwo = "";           //商品2级分类
        String levelThree = "";           //商品3级分类
        List cartList = null;
        PageBean<ShopFloorGoods> pageBean = null;
        if (!CommonUtils.checkFull(levels)) {
            if (type == 0) {
                String[] s = levels.split(",");
                levelOne = s[0];
                levelTwo = s[1];
                levelThree = s[2];
            }
            if (type == 1) {
                cartList = shopFloorShoppingCartService.findList(CommonUtils.getMyId());
                if (cartList != null && cartList.size() > 0) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (i < 10) {
                            ShopFloorShoppingCart goods = (ShopFloorShoppingCart) cartList.get(i);
                            if (goods != null) {
                                levelOne += goods.getLevelOne() + ",";
                                levelTwo += goods.getLevelTwo() + ",";
                                levelThree += goods.getLevelThree() + ",";
                            }
                        }
                    }
                }
            }
            if (type == 2) {
                cartList = shopFloorOrdersService.findIdentity(CommonUtils.getMyId());//全部
                if (cartList != null && cartList.size() > 0) {
                    for (int i = 0; i < cartList.size(); i++) {
                        if (i < 5) {
                            ShopFloorOrders goods = (ShopFloorOrders) cartList.get(i);
                            if (goods != null && !CommonUtils.checkFull(goods.getGoods())) {
                                String[] strings = goods.getGoods().split(";");
                                for (int j = 0; j < strings.length; j++) {
                                    String[] s = strings[7].split("_");
                                    levelOne += s[0] + ",";
                                    levelTwo += s[1] + ",";
                                    levelThree += s[2] + ",";
                                }
                            }
                        }
                    }
                }
            }
        }
        pageBean = goodsCenterService.findRecommendList(levelOne, levelTwo, levelThree, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param
     * @Description: 统计商品上下架数量
     * @return:
     */
    @Override
    public ReturnData statisticsGoods() {

        int sellCont1 = goodsCenterService.findNum(0);//上架
        int sellCont2 = goodsCenterService.findNum(1);//下架

        Map<String, Integer> map = new HashMap<>();
        map.put("sellCont1", sellCont1);
        map.put("sellCont2", sellCont2);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增商品描述
     * @param goodsDescribe
     * @return
     */
    @Override
    public ReturnData addFGDescribe(@Valid @RequestBody ShopFloorGoodsDescribe goodsDescribe, BindingResult
            bindingResult) {
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
    public ReturnData changeFGDescribe(@Valid @RequestBody ShopFloorGoodsDescribe goodsDescribe, BindingResult
            bindingResult) {
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
    public ReturnData delFGDescribe(@PathVariable long id, @PathVariable long userId) {
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
    public ReturnData getFGDescribe(@PathVariable long id) {
        ShopFloorGoodsDescribe dishes = goodsCenterService.disheSdetails(id);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", dishes);
    }
}
