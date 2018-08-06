package com.busi.service;

import com.busi.dao.UserHeadAlbumDao;
import com.busi.entity.UserHeadAlbum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户个人资料界面的九张头像相册 Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserHeadAlbumService {

    @Autowired
    private UserHeadAlbumDao userHeadAlbumDao;

    /***
     * 新增
     * @param userHeadAlbum
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(UserHeadAlbum userHeadAlbum){
        return userHeadAlbumDao.add(userHeadAlbum);
    }

    /***
     * 根据用户ID查询UUserHeadAlbum信息
     * @param userId
     * @return
     */
    public UserHeadAlbum findUserHeadAlbumById(long userId){
        return userHeadAlbumDao.findUserHeadAlbumById(userId);
    }

    /***
     * 更新
     * @param userHeadAlbum
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateUserHeadAlbum(UserHeadAlbum userHeadAlbum){
        return  userHeadAlbumDao.updateUserHeadAlbum(userHeadAlbum);
    }

}
