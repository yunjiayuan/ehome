package com.busi.servive;

import com.busi.dao.UserMembershipDao;
import com.busi.entity.UserMembership;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(UserMembership userMembership) {
        return userMembershipDao.update(userMembership);
    }

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update2(UserMembership userMembership) {
        return userMembershipDao.update2(userMembership);
    }

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update3(UserMembership userMembership) {
        return userMembershipDao.update3(userMembership);
    }

    /***
     * 条件查询会员信息
     * @return
     */
    public List<UserMembership> findMembershipList() {
        List<UserMembership> list;
        list = userMembershipDao.findMembershipList();
        return list;
    }

    /***
     * 条件查询会员信息
     * @return
     */
    public List<UserMembership> findMembershipList2() {
        List<UserMembership> list;
        list = userMembershipDao.findMembershipList2();
        return list;
    }

}
