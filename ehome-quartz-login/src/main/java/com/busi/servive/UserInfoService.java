package com.busi.servive;

import com.busi.dao.UserInfoDao;
import com.busi.entity.PageBean;
import com.busi.entity.UserInfo;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserInfoService {

    @Autowired
    private UserInfoDao userInfoDao;

    /***
     * 条件查找用户信息
     * @return
     */
    public List<UserInfo> findCondition() {
        List<UserInfo> list;
        list = userInfoDao.findCondition();
        return list;
    }
}
