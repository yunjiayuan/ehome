package com.busi.service;

import com.busi.dao.HomeBlogTagDao;
import com.busi.entity.HomeBlogUserTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: ehome
 * @description: 生活圈兴趣标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-02 17:19
 */
@Service
public class HomeBlogTagService {

    @Autowired
    private HomeBlogTagDao homeBlogTagDao;

    /***
     * 新增标签
     * @param homeBlogUserTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeBlogUserTag homeBlogUserTag) {
        return homeBlogTagDao.add(homeBlogUserTag);
    }

    /***
     * 编辑标签
     * @param homeBlogUserTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeBlogUserTag homeBlogUserTag) {
        return homeBlogTagDao.update(homeBlogUserTag);
    }

    /***
     * 查询userId标签
     * @return
     */
    public HomeBlogUserTag find(long userId) {
        return homeBlogTagDao.find(userId);
    }

}
