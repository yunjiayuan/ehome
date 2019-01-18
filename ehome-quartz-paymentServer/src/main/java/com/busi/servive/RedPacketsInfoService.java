package com.busi.servive;

import com.busi.dao.RedPacketsInfoDao;
import com.busi.entity.RedPacketsInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 红包信息相关Service
 * author：ZHJJ
 * create time：2018-8-16 11:46:00
 */
@Service
public class RedPacketsInfoService {

    @Autowired
    private RedPacketsInfoDao redPacketsInfoDao;

    /***
     * 查询接收红包时间为空的
     * @return
     */
    public List<RedPacketsInfo> findEmpty() {
        List<RedPacketsInfo> list;
        list = redPacketsInfoDao.findEmpty();
        return list;
    }

    /***
     * 红包过期后更新红包状态
     * @param redPacketsInfo
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateEmptyStatus(RedPacketsInfo redPacketsInfo) {
        return redPacketsInfoDao.updateEmptyStatus(redPacketsInfo);
    }

}
