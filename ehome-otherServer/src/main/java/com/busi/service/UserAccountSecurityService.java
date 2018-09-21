package com.busi.service;

import com.busi.dao.UserAccountSecurityDao;
import com.busi.entity.UserAccountSecurity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户账户安全Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserAccountSecurityService {

    @Autowired
    private UserAccountSecurityDao userAccountSecurityDao;

    /***
     * 新增
     * @param userAccountSecurity
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addUserAccountSecurity(UserAccountSecurity userAccountSecurity){
        return  userAccountSecurityDao.add(userAccountSecurity);
    }

    /***
     * 更新
     * @param userAccountSecurity
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateUserAccountSecurity(UserAccountSecurity userAccountSecurity){
        return  userAccountSecurityDao.update(userAccountSecurity);
    }

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    public UserAccountSecurity findUserAccountSecurityByUserId(long userId){
        return  userAccountSecurityDao.findUserAccountSecurityByUserId(userId);
    }

    /***
     * 根据phone查询
     * @param phone
     * @return
     */
    public UserAccountSecurity findUserAccountSecurityByPhone(String phone){
        return  userAccountSecurityDao.findUserAccountSecurityByPhone(phone);
    }

    /***
     * 查询第三方平台账号是否被绑定过
     * @param otherPlatformType
     * @param otherPlatformAccount
     * @return
     */
    public UserAccountSecurity findUserAccountSecurityByOther(int otherPlatformType,String otherPlatformAccount){
        return  userAccountSecurityDao.findUserAccountSecurityByOther(otherPlatformType,otherPlatformAccount);
    }

}
