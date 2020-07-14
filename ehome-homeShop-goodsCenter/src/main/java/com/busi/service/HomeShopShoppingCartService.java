package com.busi.service;

import com.busi.dao.HomeShopShoppingCartDao;
import com.busi.entity.HomeShopShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 二货购物车
 * @author: ZhaoJiaJie
 * @create: 2020-07-13 13:35:09
 */
@Service
public class HomeShopShoppingCartService {
    @Autowired
    private HomeShopShoppingCartDao goodsCenterDao;

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeShopShoppingCart homeShopGoods) {
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
     * 批量删除购物车商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delGoods(String[] ids) {
        return goodsCenterDao.delGoods(ids);
    }

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeShopShoppingCart homeShopGoods) {
        return goodsCenterDao.update(homeShopGoods);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public List<HomeShopShoppingCart> findList(long userId) {
        List<HomeShopShoppingCart> list = null;
        list = goodsCenterDao.findList(userId);
        return list;
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public List<HomeShopShoppingCart> findDeleteGoods(long userId) {
        List<HomeShopShoppingCart> list = null;
        list = goodsCenterDao.findDeleteGoods(userId);
        return list;
    }

    /***
     * 根据用户goodsId查询
     * @param userId
     * @return
     */
    public HomeShopShoppingCart findGoodsId(long userId, long goodsId) {
        return goodsCenterDao.findGoodsId(userId, goodsId);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public HomeShopShoppingCart findId(long userId, long id) {
        return goodsCenterDao.findId(userId, id);
    }

    /***
     * 统计用户购物车商品数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return goodsCenterDao.findNum(userId);
    }
}
