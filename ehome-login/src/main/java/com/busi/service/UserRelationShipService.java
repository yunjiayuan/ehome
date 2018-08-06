package com.busi.service;

import com.busi.dao.UserRelationShipDao;
import com.busi.entity.PageBean;
import com.busi.entity.UserInfo;
import com.busi.entity.UserRelationShip;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 好友关系Service
 * author：SunTianJie
 * create time：2018/7/16 17:36
 */
@Service
public class UserRelationShipService {

    @Autowired
    private UserRelationShipDao userRelationShipDao;

    /***
     * 新增好友关系
     * @param userRelationShip
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( UserRelationShip userRelationShip){
        return userRelationShipDao.add(userRelationShip);
    }

    /***
     * 删除好友
     * @param myId 当前用户ID
     * @param friendId 将要删除的好友ID
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(long myId,long friendId){
        return userRelationShipDao.del(myId,friendId);
    }

    /***
     * 分页查询好友列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<UserRelationShip> findList(long userId,int page, int count) {
        List<UserRelationShip> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = userRelationShipDao.findList(userId);
        return PageUtils.getPageBean(p,list);
    }

    /***
     * 修改好友备注名称
     * @param userRelationShip
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateRemarkName(UserRelationShip userRelationShip){
        return userRelationShipDao.updateRemarkName(userRelationShip);
    }

    /***
     * 将好友移动到指定分组
     * 移入黑名单时groupId为固定值-3
     * 移出黑名单时groupId为固定值0
     * @param userRelationShip
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int moveFriend(UserRelationShip userRelationShip){
        return userRelationShipDao.moveFriend(userRelationShip);
    }

    /***
     * 验证指定双方是否为好友关系
     * @param userId   登录者ID
     * @param friendId 要验证的用户ID
     * @return true 是好友 false不是好友
     */
    public boolean checkFriend(long userId,long friendId){
        List<UserRelationShip> list = userRelationShipDao.checkFriend(userId,friendId);
        if(list!=null&&list.size()>0){
            return true;
        }
        return false;
    }
}
