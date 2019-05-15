package com.busi.service;

import com.busi.dao.ShopCenterDao;
import com.busi.entity.HomeShopCenter;
import com.busi.entity.HomeShopPersonalData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /***
     * 根据用户ID查询个人信息
     * @param userId
     * @return
     */
    public HomeShopPersonalData findPersonalData(long userId) {
        return shopCenterDao.findPersonalData(userId);
    }
}
