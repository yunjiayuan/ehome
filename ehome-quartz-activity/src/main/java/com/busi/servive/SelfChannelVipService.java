package com.busi.servive;

import com.busi.dao.SelfChannelVipDao;
import com.busi.entity.SelfChannelVip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 查询会员信息
     * @return
     */
    public List<SelfChannelVip> findMembershipList() {
        List<SelfChannelVip> list;
        list = selfChannelVipDao.findMembershipList();
        return list;
    }

    /***
     * 更新会员信息
     * @param userMembership
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(SelfChannelVip userMembership) {
        return selfChannelVipDao.update(userMembership);
    }

}
