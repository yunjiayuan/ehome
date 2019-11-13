package com.busi.service;

import com.busi.dao.ShopFloorDao;
import com.busi.entity.ShopFloor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ShopFloor findByUserId(long userId) {
        return shopCenterDao.findByUserId(userId);
    }
}
