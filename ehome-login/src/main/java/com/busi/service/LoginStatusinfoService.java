package com.busi.service;

import com.busi.dao.LoginStatusInfoDao;
import com.busi.entity.LoginStatusInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户登录信息记录Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class LoginStatusinfoService {

    @Autowired
    private LoginStatusInfoDao loginStatusInfoDao;

    /***
     * 新增
     * @param loginStatusInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(LoginStatusInfo loginStatusInfo){
        return  loginStatusInfoDao.add(loginStatusInfo);
    }

}
