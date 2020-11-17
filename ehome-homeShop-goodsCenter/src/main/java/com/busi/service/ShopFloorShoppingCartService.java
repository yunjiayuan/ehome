package com.busi.service;

import com.busi.dao.ShopFloorShoppingCartDao;
import com.busi.entity.ShopFloorShoppingCart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店购物车
 * @author: ZHaoJiaJie
 * @create: 2019-12-09 13:48
 */
@Service
public class ShopFloorShoppingCartService {
    @Autowired
    private ShopFloorShoppingCartDao goodsCenterDao;

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ShopFloorShoppingCart homeShopGoods) {
        return goodsCenterDao.add(homeShopGoods);
    }

    /***
     * 批量删除商品
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDels(long id) {
        return goodsCenterDao.updateDels(id);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDels(String[] ids, long id) {
        return goodsCenterDao.updateDels(ids, id);
    }

    /***
     * 批量删除购物车商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delGoods(String[] ids, long id) {
        return goodsCenterDao.delGoods(ids, id);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id) {
        return goodsCenterDao.del(id);
    }

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ShopFloorShoppingCart homeShopGoods) {
        return goodsCenterDao.update(homeShopGoods);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public List<ShopFloorShoppingCart> findList(long userId) {
        List<ShopFloorShoppingCart> list = null;
        list = goodsCenterDao.findList(userId);
        return list;
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public List<ShopFloorShoppingCart> findDeleteGoods(long userId) {
        List<ShopFloorShoppingCart> list = null;
        list = goodsCenterDao.findDeleteGoods(userId);
        return list;
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public List<ShopFloorShoppingCart> findDeleteGoodsList(long userId, long id) {
        List<ShopFloorShoppingCart> list = null;
        list = goodsCenterDao.findDeleteGoodsList(userId, id);
        return list;
    }

    /***
     * 根据用户goodsId查询
     * @param userId
     * @return
     */
    public ShopFloorShoppingCart findGoodsId(int type, long userId, long goodsId) {
        return goodsCenterDao.findGoodsId(type, userId, goodsId);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public ShopFloorShoppingCart findId(long userId, long id) {
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
