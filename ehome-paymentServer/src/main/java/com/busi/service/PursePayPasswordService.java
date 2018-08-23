package com.busi.service;

import com.busi.dao.PursePayPasswordDao;
import com.busi.entity.PursePayPassword;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 支付密码相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class PursePayPasswordService {

    @Autowired
    private PursePayPasswordDao pursePayPasswordDao;

    /***
     * 新增支付密码
     * @param pursePayPassword
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addPursePayPassword(PursePayPassword pursePayPassword){
        return pursePayPasswordDao.addPursePayPassword(pursePayPassword);
    }

    /***
     * 根据用户ID查询用户支付密码信息
     * @param userId
     * @return
     */
    public PursePayPassword findPursePayPassword(long userId){
        return pursePayPasswordDao.findPursePayPassword(userId);
    }

    /***
     * 更新支付密码
     * @param pursePayPassword
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updatePursePayPassword(PursePayPassword pursePayPassword){
        return  pursePayPasswordDao.updatePursePayPassword(pursePayPassword);
    }

}
