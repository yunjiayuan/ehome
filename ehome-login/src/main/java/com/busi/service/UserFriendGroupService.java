package com.busi.service;

import com.busi.dao.UserFriendGroupDao;
import com.busi.entity.PageBean;
import com.busi.entity.UserFriendGroup;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友分组Service
 * author：SunTianJie
 * create time：2018/7/16 17:36
 */
@Service
public class UserFriendGroupService {

    @Autowired
    private UserFriendGroupDao userFriendGroupDao;

    /***
     * 新增好友分组
     * @param userFriendGroup
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( UserFriendGroup userFriendGroup){
        return userFriendGroupDao.add(userFriendGroup);
    }

    /***
     * 删除分组
     * @param myId 当前用户ID
     * @param groupId 将要删除的分组ID
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(long myId,long groupId){
        return userFriendGroupDao.del(myId,groupId);
    }

    /***
     * 分页查询分组列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<UserFriendGroup> findList(long userId,int page, int count) {
        List<UserFriendGroup> newList = new ArrayList<>();
        UserFriendGroup userFriendGroup1 = new UserFriendGroup();
        userFriendGroup1.setId(0);//家园好友
        userFriendGroup1.setGroupType(0);
        userFriendGroup1.setGroupName("家园好友");
        userFriendGroup1.setUserId(userId);
        newList.add(userFriendGroup1);

        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        List<UserFriendGroup> list = userFriendGroupDao.findList(userId);
        for(int i=0;i<list.size();i++){
            newList.add(list.get(i));
        }

        UserFriendGroup userFriendGroup2 = new UserFriendGroup();
        userFriendGroup2.setId(-3);//黑名单
        userFriendGroup2.setGroupType(0);
        userFriendGroup2.setGroupName("黑名单");
        userFriendGroup2.setUserId(userId);
        newList.add(userFriendGroup2);

        return PageUtils.getPageBean(p,newList);
    }

    /***
     * 修改分组名称
     * @param userFriendGroup
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int updateGroupName( UserFriendGroup userFriendGroup){
        return userFriendGroupDao.updateGroupName(userFriendGroup);
    }

}
