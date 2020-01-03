package com.busi.service;

import com.busi.dao.StarCertificationDao;
import com.busi.entity.StarCertification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: ehome
 * @description: 明星认证
 * @author: ZHaoJiaJie
 * @create: 2020-01-02 14:15
 */
@Service
public class StarCertificationService {

    @Autowired
    private StarCertificationDao starCertificationDao;

    /***
     * 新增
     * @param homeBlogUserTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(StarCertification homeBlogUserTag) {
        return starCertificationDao.add(homeBlogUserTag);
    }

    /***
     * 编辑
     * @param homeBlogUserTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(StarCertification homeBlogUserTag) {
        return starCertificationDao.update(homeBlogUserTag);
    }

    /***
     * 查询userId
     * @return
     */
    public StarCertification find(long userId) {
        return starCertificationDao.find(userId);
    }

}
