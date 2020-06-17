package com.busi.service;

import com.busi.dao.ChildModelDao;
import com.busi.entity.ChildModelPwd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: ehome
 * @description: 儿童锁
 * @author: ZHaoJiaJie
 * @create: 2020-06-16 23:26:10
 */
@Service
public class ChildModelService {

    @Autowired
    private ChildModelDao homeAlbumDao;

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPwd(ChildModelPwd homeAlbumPwd) {
        return homeAlbumDao.addPwd(homeAlbumPwd);
    }

    /***
     * 更新密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePwd(ChildModelPwd homeAlbumPwd) {
        return homeAlbumDao.updatePwd(homeAlbumPwd);
    }

    /***
     * 根据用户ID查询
     * @return
     */
    public ChildModelPwd findById(long userId) {
        return homeAlbumDao.findById(userId);
    }

    /***
     * 删除密码
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delPwd(long userId) {
        return homeAlbumDao.delPwd(userId);
    }
}
