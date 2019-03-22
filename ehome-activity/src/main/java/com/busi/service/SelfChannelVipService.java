package com.busi.service;

import com.busi.dao.SelfChannelVipDao;
import com.busi.entity.SelfChannelVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: ehome
 * @description: 自频道会员
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:57
 */
@Service
public class SelfChannelVipService {

    @Autowired
    private SelfChannelVipDao selfChannelVipDao;

    /***
     * 新增会员信息
     * @param selfChannelVip
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(SelfChannelVip selfChannelVip){
        return  selfChannelVipDao.add(selfChannelVip);
    }

    /***
     * 查询会员信息
     * @param userId
     * @return
     */
    public SelfChannelVip findDetails(long userId) {
        return selfChannelVipDao.findDetails(userId);
    }

}
