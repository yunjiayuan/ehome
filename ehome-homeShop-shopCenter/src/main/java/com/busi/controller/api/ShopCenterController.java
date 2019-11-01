package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ShopCenterService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

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
        //调整商品分类（临时调用）
//        String[] arrayId = null;
//        List<GoodsCategory> list = null;
//        list = shopCenterService.findByUserId1();
//        for (int i = 0; i < list.size(); i++) {
//            GoodsCategory category = list.get(i);
//            if (category != null) {
//                arrayId = category.getArrayId().split("_");
//                int one = Integer.valueOf(arrayId[1]);
//                if (one >= 57 && one <= 65) {
//                    category.setLevelOne(1);//手机、数码
//                }
//                if (one >= 3 && one <= 7) {
//                    category.setLevelOne(2);//家用电器
//                }
//                if (one >= 66 && one <= 71) {
//                    category.setLevelOne(3);//家居家装
//                }
//                if (one >= 8 && one <= 14) {
//                    category.setLevelOne(4);//电脑、办公
//                }
//                if (one >= 72 && one <= 77) {
//                    category.setLevelOne(5);//厨具
//                }
//                if (one >= 15 && one <= 20) {
//                    category.setLevelOne(6);//个护化妆
//                }
//                if (one >= 78 && one <= 81) {
//                    category.setLevelOne(7);//服饰内衣
//                }
//                if (one == 21) {
//                    category.setLevelOne(8);//钟表
//                }
//                if (one >= 82 && one <= 83) {
//                    category.setLevelOne(9);//鞋靴
//                }
//                if (one >= 22 && one <= 31) {
//                    category.setLevelOne(10);//母婴
//                }
//                if (one >= 84 && one <= 89) {
//                    category.setLevelOne(11);//礼品箱包
//                }
//                if (one >= 32 && one <= 38) {
//                    category.setLevelOne(12);//食品饮料、保健食品
//                }
//                if (one >= 90 && one <= 100) {
//                    category.setLevelOne(13);//珠宝
//                }
//                if (one >= 39 && one <= 44) {
//                    category.setLevelOne(14);//汽车用品
//                }
//                if (one >= 101 && one <= 109) {
//                    category.setLevelOne(15);//运动健康
//                }
//                if (one >= 45 && one <= 56) {
//                    category.setLevelOne(16);//玩具乐器
//                }
//                if (one >= 110 && one <= 116) {
//                    category.setLevelOne(17);//彩票、旅行、充值、票务
//                }
        //其他one >= 0 && one <= 2  图书、音像、电子书刊
//                category.setLevelTwo(one);
//                if (arrayId.length == 3) {
//                    category.setLevelThree(Integer.valueOf(arrayId[2]));
//                    category.setLevelFour(-1);
//                    category.setLevelFive(-1);
//                } else if (arrayId.length == 4) {
//                    category.setLevelThree(Integer.valueOf(arrayId[2]));
//                    category.setLevelFour(Integer.valueOf(arrayId[3]));
//                    category.setLevelFive(-1);
//                } else if (arrayId.length == 5) {
//                    category.setLevelThree(Integer.valueOf(arrayId[2]));
//                    category.setLevelFour(Integer.valueOf(arrayId[3]));
//                    category.setLevelFive(Integer.valueOf(arrayId[4]));
//                } else {
//                    category.setLevelThree(-1);
//                    category.setLevelFour(-1);
//                    category.setLevelFive(-1);
//                }
//                shopCenterService.updateBusiness(category);
//            }
//        }
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
            map2.put("infoId", ik.getId());
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
        if (homeShopPersonalData.getIdCardType() != 0) {
            homeShopPersonalData.setIdCardExpireTime(null);
        }
        //判断是否已经新增过
        HomeShopPersonalData dishes = shopCenterService.findPersonalData(CommonUtils.getMyId());
        if (dishes != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        homeShopPersonalData.setAcState(1);  // 认证状态:0未认证,1审核中,2未通过,3已认证
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
        if (homeShopPersonalData.getIdCardType() != 0) {
            homeShopPersonalData.setIdCardExpireTime(null);
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

//        Map<String, Object> map = new HashMap<>();
//        map.put("data", dishes);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", dishes);
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
        Map<String, Object> map = new HashMap<>();
        HomeShopPersonalData dishes = shopCenterService.findPersonalData(userId);
        if (dishes == null) {
            map.put("acState", 0);// 认证状态:0未认证,1审核中,2未通过,3已认证
        } else {
            map.put("acState", dishes.getAcState());// 认证状态:0未认证,1审核中,2未通过,3已认证
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类  默认为0, -2为不限 :0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @param levelThree 商品3级分类  默认为0, -2为不限
     * @param levelFour 商品4级分类  默认为0, -2为不限
     * @param levelFive 商品5级分类  默认为0, -2为不限
     * @param letter 模糊搜索（首字母或中文）
     * @param page  页码 第几页
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findGoodsCategory(@PathVariable int levelOne, @PathVariable int levelTwo, @PathVariable int levelThree, @PathVariable int levelFour, @PathVariable int levelFive, @PathVariable String letter, @PathVariable int page, @PathVariable int count) {
        if (page < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "page参数有误", new JSONObject());
        }
        if (count < 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "count参数有误", new JSONObject());
        }
        List<GoodsCategory> list = null;
        List<GoodsBrandCategoryValue> brandList = null;
        PageBean<GoodsCategory> pageBean = null;
        List<GoodsCategory> list1 = new ArrayList();
        pageBean = shopCenterService.findList(levelOne, levelTwo, levelThree, levelFour, levelFive, letter, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
        }
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            //判断该类目下是否存在品牌
            for (int i = 0; i < list.size(); i++) {
                GoodsCategory goodsCategory1 = list.get(i);
                if (goodsCategory1 != null) {
                    if (goodsCategory1.getId() >= 2581 && goodsCategory1.getId() <= 3552) {
                        goodsCategory1.setBrand(1);
                    }
                }
            }
            //模糊查询时返回组合ID跟名称
            String name = null;
            if (!CommonUtils.checkFull(letter)) {
                for (int i = 0; i < list.size(); i++) {
                    GoodsCategory category = list.get(i);
                    if (category != null) {
                        GoodsCategory cate = null;
                        GoodsCategory goodsCategory = new GoodsCategory();
                        goodsCategory.setId(category.getId());//分类ID
                        goodsCategory.setBrand(category.getBrand());//是否包含品牌  0没有 1有
                        goodsCategory.setIds(category.getLevelOne() + "," + category.getLevelTwo() + "," + category.getLevelThree() + "," + category.getLevelFour() + "," + category.getLevelFive());
                        cate = shopCenterService.findList2(category.getLevelOne(), -1, -1, -1, -1);
                        name = cate.getName();
                        cate = shopCenterService.findList2(category.getLevelOne(), category.getLevelTwo(), -1, -1, -1);
                        name += ">>" + cate.getName();
                        if (category.getLevelThree() > -1) {
                            cate = shopCenterService.findList2(category.getLevelOne(), category.getLevelTwo(), category.getLevelThree(), -1, -1);
                            if (cate != null) {
                                name += ">>" + cate.getName();
                            }
                        }
                        if (category.getLevelFour() > -1) {
                            cate = shopCenterService.findList2(category.getLevelOne(), category.getLevelTwo(), category.getLevelThree(), category.getLevelFour(), -1);
                            if (cate != null) {
                                name += ">>" + cate.getName();
                            }
                        }
                        if (category.getLevelFive() > -1) {
                            cate = shopCenterService.findList2(category.getLevelOne(), category.getLevelTwo(), category.getLevelThree(), category.getLevelFour(), category.getLevelFive());
                            if (cate != null) {
                                name += ">>" + cate.getName();
                            }
                        }
                        goodsCategory.setName(name);
                        list1.add(goodsCategory);
                    }
                }
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list1);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 查询商品品牌
     * @param sortId 商品分类ID
     * @param letter 商品品牌首字母
     * @return
     */
    @Override
    public ReturnData findGoodsBrand(@PathVariable long sortId, @PathVariable String letter) {
        //开始查询
        List list = null;
        List list1 = null;
        String ids = "";
        list = shopCenterService.findCategoryValue(sortId);
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                GoodsBrandCategoryValue categoryValue = (GoodsBrandCategoryValue) list.get(i);
                if (categoryValue != null) {
                    if (i == list.size() - 1) {
                        ids += categoryValue.getBrandId();//品牌ID
                    } else {
                        ids += categoryValue.getBrandId() + ",";
                    }
                }
            }
        }
        if (!CommonUtils.checkFull(ids)) {
            list1 = shopCenterService.findBrands(ids.split(","), letter);
            if (list1 != null && list1.size() > 0) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list1);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONObject());
    }

    /***
     * 查询商品属性名称
     * @param goodCategoryId 商品分类id
     * @param goodsBrandId 品牌id
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findBrandProperty(@PathVariable long goodCategoryId, @PathVariable long goodsBrandId, @PathVariable int page, @PathVariable int count) {
        //开始查询
        PageBean<GoodsBrandProperty> pageBean = null;
        GoodsBrandCategoryValue categoryValue = shopCenterService.findRelation(goodCategoryId, goodsBrandId);
        if (categoryValue == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
        }
        pageBean = shopCenterService.findBrandProperty(goodCategoryId, categoryValue.getId(), page, count);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);

    }

    /***
     * 查询商品属性值
     * @param goodsBrandPropertyId 商品属性id
     * @param page
     * @param count
     * @return
     */
    @Override
    public ReturnData findBrandPropertyValue(@PathVariable long goodsBrandPropertyId, @PathVariable int page, @PathVariable int count) {
        //开始查询
        PageBean<GoodsBrandPropertyValue> pageBean = null;
        pageBean = shopCenterService.findBrandPropertyValue(goodsBrandPropertyId, page, count);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
