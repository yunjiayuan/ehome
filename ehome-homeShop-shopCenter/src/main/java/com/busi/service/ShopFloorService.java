package com.busi.service;

import com.busi.dao.ShopFloorDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloor;
import com.busi.entity.YongHuiGoodsSort;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:47
 */
@Service
public class ShopFloorService {

    @Autowired
    private ShopFloorDao shopCenterDao;

    /***
     * 新建楼店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addHomeShop(ShopFloor homeShopCenter) {
        return shopCenterDao.addHomeShop(homeShopCenter);
    }

    /***
     * 更新楼店
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateHomeShop(ShopFloor homeShopCenter) {
        return shopCenterDao.updateHomeShop(homeShopCenter);
    }

    /***
     * 更新保证金支付状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePayStates(ShopFloor homeShopCenter) {
        return shopCenterDao.updatePayStates(homeShopCenter);
    }


    /***
     * 更新店铺营业状态
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBusiness(ShopFloor homeShopCenter) {
        return shopCenterDao.updateBusiness(homeShopCenter);
    }

    /***
     * 根据用户ID查询店铺状态
     * @param userId
     * @return
     */
    public ShopFloor findByUserId(long userId, String villageOnly) {
        return shopCenterDao.findByUserId(userId, villageOnly);
    }


    /***
     * 查询所有店铺
     * @return
     */
    public List<ShopFloor> findByIds(String[] villageOnly) {
        List<ShopFloor> list = null;
        list = shopCenterDao.findByIds(villageOnly);
        return list;
    }

    /***
     * 查询附近楼店
     * @param lat      纬度
     * @param lon      经度
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloor> findNearbySFList(double lat, double lon, int page, int count) {
        List<ShopFloor> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findNearbySFList(lat, lon);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询用户楼店
     * @param userId   用户
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<ShopFloor> findUserSFlist(long userId,  int page, int count) {
        List<ShopFloor> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = shopCenterDao.findUserSFlist(userId);
        return PageUtils.getPageBean(p, list);
    }


    /***
     * 新增永辉分类
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addYHSort(YongHuiGoodsSort homeShopCenter) {
        return shopCenterDao.addYHSort(homeShopCenter);
    }

    /***
     * 更新永辉分类
     * @param homeShopCenter
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeYHSort(YongHuiGoodsSort homeShopCenter) {
        return shopCenterDao.changeYHSort(homeShopCenter);
    }

    /***
     * 批量删除永辉分类
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delYHSort(String[] ids) {
        return shopCenterDao.delYHSort(ids);
    }

    /***
     * 查询商品分类
     * @param levelOne 商品1级分类   -2为不限
     * @param levelTwo 商品2级分类   -2为不限
     * @param levelTwo 商品3级分类   -2为不限
     * @param letter 商品分类首字母
     * @return
     */
    public List<YongHuiGoodsSort> findYHSort(int levelOne, int levelTwo, int levelThree, String letter) {
        List<YongHuiGoodsSort> list = null;
        list = shopCenterDao.findYHSort(levelOne, levelTwo, levelThree, letter);
        return list;
    }
}
