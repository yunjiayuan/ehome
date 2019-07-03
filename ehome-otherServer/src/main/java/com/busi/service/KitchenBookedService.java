package com.busi.service;

import com.busi.dao.KitchenBookedDao;
import com.busi.entity.KitchenBooked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: ehome
 * @description: 厨房订座设置
 * @author: ZHaoJiaJie
 * @create: 2019-06-27 10:05
 */
@Service
public class KitchenBookedService {

    @Autowired
    private KitchenBookedDao kitchenBookedDao;

    /***
     * 新建厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.add(kitchenBooked);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public KitchenBooked findByUserId(long userId) {
        return kitchenBookedDao.findByUserId(userId);
    }

    /***
     * 更新厨房订座设置
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBooked(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.updateBooked(kitchenBooked);
    }

    /***
     * 更新厨房订座剩余数量
     * @param kitchenBooked
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePosition(KitchenBooked kitchenBooked) {
        return kitchenBookedDao.updatePosition(kitchenBooked);
    }
}
