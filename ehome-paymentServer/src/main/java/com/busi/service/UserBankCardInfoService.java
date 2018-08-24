package com.busi.service;

import com.busi.dao.UserBankCardInfoDao;
import com.busi.entity.UserBankCardInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 银行卡相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class UserBankCardInfoService {

    @Autowired
    private UserBankCardInfoDao userBankCardInfoDao;

    /***
     * 新增银行卡
     * @param userBankCardInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addUserBankCardInfo(UserBankCardInfo userBankCardInfo){
        return userBankCardInfoDao.addUserBankCardInfo(userBankCardInfo);
    }

    /***
     * 根据用户ID查询用户银行卡信息
     * @param userId
     * @return
     */
    public UserBankCardInfo findUserBankCardInfo(long userId){
        return userBankCardInfoDao.findUserBankCardInfo(userId);
    }

    /***
     * 检测银行卡信息是否存在
     * @param bankCard
     * @return
     */
    public UserBankCardInfo findUserBankCardInfoByBankCard(String bankCard){
        return userBankCardInfoDao.findUserBankCardInfoByBankCard(bankCard);
    }


}
