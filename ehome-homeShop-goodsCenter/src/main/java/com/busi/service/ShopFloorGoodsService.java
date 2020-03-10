package com.busi.service;

import com.busi.dao.ShopFloorGoodsDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloorGoods;
import com.busi.entity.ShopFloorGoodsDescribe;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店商品
 * @author: ZHaoJiaJie
 * @create: 2019-11-19 13:38
 */
@Service
public class ShopFloorGoodsService {

    @Autowired
    private ShopFloorGoodsDao goodsCenterDao;

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ShopFloorGoods homeShopGoods) {
        return goodsCenterDao.add(homeShopGoods);
    }

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDels(String[] ids) {
        return goodsCenterDao.updateDels(ids);
    }

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ShopFloorGoods homeShopGoods) {
        return goodsCenterDao.update(homeShopGoods);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public ShopFloorGoods findUserById(long id) {
        return goodsCenterDao.findUserById(id);
    }

    /***
     * 批量查询指定的商品
     * @param ids
     * @return
     */
    public List<ShopFloorGoods> findList(String[] ids) {
        List<ShopFloorGoods> list;
        list = goodsCenterDao.findList(ids);
        return list;
    }

    /***
     * 批量上下架商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeShopGoods(String[] ids, int sellType) {
        return goodsCenterDao.changeShopGoods(ids, sellType);
    }

    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(int type) {
        return goodsCenterDao.findNum(type);
    }

    /***
     * 分页查询商品(用户调用)
     * @param sort  排序条件:0默认销量倒序，1最新发布
     * @param discount  0全部，1只看折扣
     * @param price  默认-1不限制 0价格升序，1价格倒序
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
    public PageBean<ShopFloorGoods> findDishesSortList(int discount, int sort, int price, int stock, int minPrice, int maxPrice, int levelOne, int levelTwo, int levelThree, int page, int count) {

        List<ShopFloorGoods> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = goodsCenterDao.findDishesSortList(discount, sort, price, stock, minPrice, maxPrice, levelOne, levelTwo, levelThree);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询商品
     * @param sort  查询条件:-1全部 0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param time  时间：0倒序 1正序
     * @param levelOne  一级分类:默认值为0,-2为不限
     * @param levelTwo  二级分类:默认值为0,-2为不限
     * @param levelThree  三级分类:默认值为0,-2为不限
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShopFloorGoods> findFGoodsList(int sort, int stock, int time, int levelOne, int levelTwo,
                                                   int levelThree, int page, int count) {

        List<ShopFloorGoods> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (stock == 1) {
            list = goodsCenterDao.findFGoodsList(sort, stock, levelOne, levelTwo, levelThree);
        } else {
            list = goodsCenterDao.findFGoodsList2(sort, time, levelOne, levelTwo, levelThree);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增商品描述
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addGoodsDescribe(ShopFloorGoodsDescribe homeShopGoods) {
        return goodsCenterDao.addGoodsDescribe(homeShopGoods);
    }

    /***
     * 删除商品描述
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delGoodsDescribe(long id, long userId) {
        return goodsCenterDao.delGoodsDescribe(id, userId);
    }

    /***
     * 更新商品描述
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeGoodsDescribe(ShopFloorGoodsDescribe homeShopGoods) {
        return goodsCenterDao.changeGoodsDescribe(homeShopGoods);
    }

    /***
     * 根据ID查询商品描述
     * @param id
     * @return
     */
    public ShopFloorGoodsDescribe disheSdetails(long id) {
        return goodsCenterDao.disheSdetails(id);
    }

    /***
     * 更新浏览数
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateSee(ShopFloorGoods homeShopGoods) {
        return goodsCenterDao.updateSee(homeShopGoods);
    }
}
