package com.busi.service;

import com.busi.dao.ChatSquareDao;
import com.busi.entity.ChatSquare;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户聊天广场马甲Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class ChatSquareService {

    @Autowired
    private ChatSquareDao chatSquareDao;

    /***
     * 新增马甲
     * @param chatSquare
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( ChatSquare chatSquare){
        return chatSquareDao.add(chatSquare);
    }

    /***
     * 根据用户ID查询用户马甲信息
     * @param userId
     * @return
     */
    public ChatSquare findChatSquareByUserId(long userId){
        return chatSquareDao.findChatSquareByUserId(userId);
    }

    /***
     * 根据用户ID删除用户马甲
     * @param userId
     * @return
     */
    public int delChatSquareByUserId(long userId){
        return chatSquareDao.delChatSquareByUserId(userId);
    }

    /***
     * 更新用户马甲信息
     * @param chatSquare
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(ChatSquare chatSquare){
        return  chatSquareDao.update(chatSquare);
    }

    /***
     * 查询指定用户集合的马甲信息列表
     * @param userIds
     * @return
     */
    public List findChatSquareUserInfo(String[] userIds){
        return chatSquareDao.findChatSquareUserInfo(userIds);
    }

}
