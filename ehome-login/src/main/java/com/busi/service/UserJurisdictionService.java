package com.busi.service;

import com.busi.dao.UserJurisdictionDao;
import com.busi.entity.UserJurisdiction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户权限相关（设置功能中的权限设置 包括房间锁和被访问权限）Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserJurisdictionService {

    @Autowired
    private UserJurisdictionDao userJurisdictionDao;

    /***
     * 新增
     * @param userJurisdiction
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(UserJurisdiction userJurisdiction){
        return  userJurisdictionDao.add(userJurisdiction);
    }

    /***
     * 更新
     * @param userJurisdiction
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(UserJurisdiction userJurisdiction){
        return  userJurisdictionDao.update(userJurisdiction);
    }

    /***
     * 查询
     * @param userId
     * @return
     */
    public UserJurisdiction findUserJurisdiction(long userId){
        return  userJurisdictionDao.findUserJurisdiction(userId);
    }

}
