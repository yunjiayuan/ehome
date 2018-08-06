package com.busi.service;

import com.busi.dao.UserHeadNotesDao;
import com.busi.entity.UserHeadNotes;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 用户头像相册（主界面各房间封面）Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class UserHeadNotesService {

    @Autowired
    private UserHeadNotesDao userHeadNotesDao;

    /***
     * 新增
     * @param userHeadNotes
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(UserHeadNotes userHeadNotes){
        return userHeadNotesDao.add(userHeadNotes);
    }


    /***
     * 根据用户ID查询UserHeadNotes信息
     * @param userId
     * @return
     */
    public UserHeadNotes findUserHeadNotesById(long userId){
        return userHeadNotesDao.findUserHeadNotesById(userId);
    }


    /***
     * 更新用户房间封面
     * @param userHeadNotes
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateUserHeadNoteCover(UserHeadNotes userHeadNotes){
        return  userHeadNotesDao.updateUserHeadNoteCover(userHeadNotes);
    }

    /***
     * 更新用户欢迎视频和封面
     * @param userHeadNotes
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateWelcomeVideo(UserHeadNotes userHeadNotes){
        return  userHeadNotesDao.updateWelcomeVideo(userHeadNotes);
    }
}
