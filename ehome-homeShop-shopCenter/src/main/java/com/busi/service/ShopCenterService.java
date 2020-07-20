package com.busi.service;

import com.busi.dao.ShopCenterDao;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 店铺信息相关Service
 * author：ZHJJ
 * create time：2019-5-13 11:05:07
 */
@Service
public class ShopCenterService {

    @Autowired
    private ShopCenterDao shopCenterDao;

    /***
     * 新建家店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addHomeShop(HomeShopCenter homeShopCenter) {
        return shopCenterDao.addHomeShop(homeShopCenter);
    }

    /***
     * 更新家店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHomeShop(HomeShopCenter homeShopCenter) {
        return shopCenterDao.updateHomeShop(homeShopCenter);
    }

    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(HomeShopCenter homeShopCenter) {
        return shopCenterDao.updateBusiness(homeShopCenter);
    }
    //调整商品分类（临时调用）
//    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
//    public int updateBusiness(GoodsCategory category) {
//        return shopCenterDao.updateBusiness(category);
//    }

    /***
     * 新增个人信息
     * @param shopPersonalData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPersonalData(HomeShopPersonalData shopPersonalData) {
        return shopCenterDao.addPersonalData(shopPersonalData);
    }

    /***
     * 更新个人信息
     * @param shopPersonalData
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updPersonalData(HomeShopPersonalData shopPersonalData) {
        return shopCenterDao.updPersonalData(shopPersonalData);
    }

    /***
     * 根据用户ID查询店铺状态
     * @param userId
     * @return
     */
    public HomeShopCenter findByUserId(long userId) {
        return shopCenterDao.findByUserId(userId);
    }

    //调整商品分类（临时调用）
    public List<GoodsCategory> findByUserId1() {
        return shopCenterDao.findByUserId1();
    }

    /***
     * 根据用户ID查询个人信息
     * @param userId
     * @return
     */
    public HomeShopPersonalData findPersonalData(long userId) {
        return shopCenterDao.findPersonalData(userId);
    }

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类  默认为0, -2为不限:0图书、音像、电子书刊  1手机、数码  2家用电器  3家居家装  4电脑、办公  5厨具  6个护化妆  7服饰内衣  8钟表  9鞋靴  10母婴  11礼品箱包  12食品饮料、保健食品  13珠宝  14汽车用品  15运动健康  16玩具乐器  17彩票、旅行、充值、票务
     * @param levelTwo 商品2级分类  默认为0, -2为不限
     * @param levelThree 商品3级分类  默认为0, -2为不限
     * @param levelFour 商品4级分类  默认为0, -2为不限
     * @param levelFive 商品5级分类  默认为0, -2为不限
     * @param letter 商品分类首字母
     * @param page  页码 第几页
     * @param count 每页条数
     * @return
     */
    public PageBean<GoodsCategory> findList(int levelOne, int levelTwo, int levelThree, int levelFour, int levelFive, String letter, int page, int count) {

        List<GoodsCategory> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(letter)) {
            list = shopCenterDao.findList2(letter);
        } else {
            list = shopCenterDao.findList(levelOne, levelTwo, levelThree, levelFour, levelFive);
        }
        return PageUtils.getPageBean(p, list);
    }

    public List<GoodsCategory> findGoodsCategoryId(int levelOne, int levelTwo, int levelThree, int levelFour, int levelFive) {
        List<GoodsCategory> list;
        list = shopCenterDao.findList(levelOne, levelTwo, levelThree, levelFour, levelFive);
        return list;
    }

    public GoodsCategory findList2(int levelOne, int levelTwo, int levelThree, int levelFour, int levelFive) {

        return shopCenterDao.findList3(levelOne, levelTwo, levelThree, levelFour, levelFive);
    }

    /***
     * 查询商品品牌
     * @param sortId 商品分类ID
     * @return
     */
    public List<GoodsBrandCategoryValue> findCategoryValue(String sortId) {
        return shopCenterDao.findCategoryValue(sortId.split(","));
    }

    /***
     * 查询商品品牌
     * @param ids 商品品牌ID
     * @param letter 商品品牌首字母
     * @return
     */
    public List<GoodsBrands> findBrands(String[] ids, String letter) {
        if (CommonUtils.checkFull(letter)) {
            letter = null;
        }
        return shopCenterDao.findBrands(ids, letter);
    }

    /***
     * 查询商品属性名称
     * @param goodCategoryId 商品分类id
     * @param goodsBrandId 品牌id
     * @param page
     * @param count
     * @return
     */
    public PageBean<GoodsBrandProperty> findBrandProperty(long goodCategoryId, long goodsBrandId, int page, int count) {
        List<GoodsBrandProperty> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findBrandProperty(goodCategoryId, goodsBrandId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询商品属性值
     * @param goodsBrandPropertyId 品牌商品属性值id
     * @param page
     * @param count
     * @return
     */
    public PageBean<GoodsBrandPropertyValue> findBrandPropertyValue(long goodsBrandPropertyId, int page, int count) {
        List<GoodsBrandPropertyValue> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findBrandPropertyValue(goodsBrandPropertyId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据分类&品牌ID查询分类&品牌关联ID
     * @param goodCategoryId
     * @return
     */
    public GoodsBrandCategoryValue findRelation(long goodCategoryId, long goodsBrandId) {
        return shopCenterDao.findRelation(goodCategoryId, goodsBrandId);
    }
}
