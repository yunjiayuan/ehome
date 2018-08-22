package com.busi.service;

import com.busi.dao.DetailedUserInfoDao;
import com.busi.entity.DetailedUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户详细资料Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class DetailedUserInfoService {

    @Autowired
    private DetailedUserInfoDao detailedUserInfoDao;

    /***
     * 新增用户详细信息
     * @param detailedUserInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( DetailedUserInfo detailedUserInfo){
        return detailedUserInfoDao.add(detailedUserInfo);
    }

    /***
     * 根据用户ID查询用户详细信息
     * @param userId
     * @return
     */
    public DetailedUserInfo findUserDetailedById(long userId){
        return detailedUserInfoDao.findDetailedUserById(userId);
    }

    /***
     * 更新用户详细信息
     * @param detailedUserInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(DetailedUserInfo detailedUserInfo){
        return  detailedUserInfoDao.update(detailedUserInfo);
    }

}
