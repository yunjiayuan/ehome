package com.busi.service;

import com.busi.dao.UserMembershipDao;
import com.busi.entity.UserMembership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 会员Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserMembershipService {

    @Autowired
    private UserMembershipDao userMembershipDao;

    /***
     * 新增会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(UserMembership userMembership){
        return  userMembershipDao.add(userMembership);
    }

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(UserMembership userMembership){
        return  userMembershipDao.update(userMembership);
    }

    /***
     * 查询会员信息
     * @param userId
     * @return
     */
    public UserMembership findUserMembership(long userId){
        return  userMembershipDao.findUserMembership(userId);
    }

}
