package com.busi.service;

import com.busi.dao.RedPacketsCensusDao;
import com.busi.entity.RedPacketsCensus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 红包统计信息相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class RedPacketsCensusService {

    @Autowired
    private RedPacketsCensusDao redPacketsCensusDao;

    /***
     * 新增
     * @param redPacketsCensus
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addRedPacketsCensus(RedPacketsCensus redPacketsCensus){
        return redPacketsCensusDao.addRedPacketsCensus(redPacketsCensus);
    }

    /***
     * 根据用户ID查询
     * @param userId
     * @return
     */
    public RedPacketsCensus findRedPacketsCensus(long userId){
        return redPacketsCensusDao.findRedPacketsCensus(userId);
    }

    /***
     * 更新
     * @param redPacketsCensus
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateredPacketsCensus(RedPacketsCensus redPacketsCensus){
        return  redPacketsCensusDao.updateredPacketsCensus(redPacketsCensus);
    }

}
