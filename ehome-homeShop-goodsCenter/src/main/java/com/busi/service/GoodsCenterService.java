package com.busi.service;

import com.busi.dao.GoodsCenterDao;
import com.busi.entity.GoodsDescribe;
import com.busi.entity.GoodsSort;
import com.busi.entity.HomeShopGoods;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 商品信息相关Service
 * author：ZhaoJiaJie
 * create time：2019-7-26 13:46:07
 */
@Service
public class GoodsCenterService {

    @Autowired
    private GoodsCenterDao goodsCenterDao;

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeShopGoods homeShopGoods) {
        return goodsCenterDao.add(homeShopGoods);
    }

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long id, long userId) {
        return goodsCenterDao.del(id, userId);
    }

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeShopGoods homeShopGoods) {
        return goodsCenterDao.update(homeShopGoods);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public HomeShopGoods findUserById(long id) {
        return goodsCenterDao.findUserById(id);
    }

    /***
     * 更新删除状态
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(HomeShopGoods homeShopGoods) {
        return goodsCenterDao.updateDel(homeShopGoods);
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
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId, int type) {
        return goodsCenterDao.findNum(userId, type);
    }

    /***
     * 统计该用户分类数量
     * @param shopId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findSortNum(long shopId) {
        return goodsCenterDao.findSortNum(shopId);
    }

    /***
     * 新增分类
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addGoodsSort(GoodsSort homeShopGoods) {
        return goodsCenterDao.addGoodsSort(homeShopGoods);
    }

    /***
     * 修改分类
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeGoodsSort(GoodsSort homeShopGoods) {
        return goodsCenterDao.changeGoodsSort(homeShopGoods);
    }

    /***
     * 批量修改商品所属分类
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int editGoodsSort(String[] ids, long sortId, String sortName) {
        return goodsCenterDao.editGoodsSort(ids, sortId, sortName);
    }

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delGoodsSort(String[] ids) {
        return goodsCenterDao.delGoodsSort(ids);
    }

    /***
     * 查询分类列表
     * @param id  店铺
     * @param find  0默认所有 1一级分类 2子级分类
     * @param sortId  分类ID(仅查询子级分类有效)
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<GoodsSort> getGoodsSortList(long id, int find, int sortId, int page, int count) {

        List<GoodsSort> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = goodsCenterDao.getGoodsSortList(id, find, sortId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询商品
     * @param shopId  店铺ID
     * @param sort  排序条件:0出售中，1仓库中，2已预约
     * @param stock  库存：0倒序 1正序
     * @param time  时间：0倒序 1正序
     * @param goodsSort  分类
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeShopGoods> findDishesSortList(int sort, long shopId, int stock, int time, long goodsSort, int page, int count) {

        List<HomeShopGoods> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (stock == 1) {
            list = goodsCenterDao.findDishesSortList(sort, shopId, stock, goodsSort);
        } else {
            list = goodsCenterDao.findDishesSortList2(sort, shopId, time, goodsSort);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增商品描述
     * @param homeShopGoods
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addGoodsDescribe(GoodsDescribe homeShopGoods) {
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
    public int changeGoodsDescribe(GoodsDescribe homeShopGoods) {
        return goodsCenterDao.changeGoodsDescribe(homeShopGoods);
    }

    /***
     * 根据ID查询商品描述
     * @param id
     * @return
     */
    public GoodsDescribe disheSdetails(long id) {
        return goodsCenterDao.disheSdetails(id);
    }
}
