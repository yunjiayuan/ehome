package com.busi.service;

import com.busi.dao.FollowInfoDao;
import com.busi.entity.FollowInfo;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 关注Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class FollowInfoService {

    @Autowired
    private FollowInfoDao followInfoDao;

    /***
     * 加关注
     * @param followInfo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addFollow(FollowInfo followInfo){
        return followInfoDao.addFollow(followInfo);
    }


    /***
     * 取消关注
     * @param userId       生活圈发布者用户ID
     * @param followUserId 将要被删除的生活圈
     * @return
     */
    public int delFollow(long userId,long followUserId){
        return followInfoDao.delFollow(userId,followUserId);
    }

    /***
     * 查询是否存在关注关系
     * @param userId       关注者用户ID
     * @param followUserId 被关注者用户ID
     */
    public FollowInfo findFollowInfo(long userId,long followUserId){
        return followInfoDao.findFollowInfo(userId,followUserId);
    }

    /***
     * 查询是否存在关注关系
     * @param userId  当前登录者用户ID
     */
    public int findFollowCounts(long userId){
        return followInfoDao.findFollowCounts(userId);
    }

    /***
     * 查询关注和粉丝列表
     * @param userId     将要查询的用户ID
     * @param searchType 0 表示查询我关注的人列表  1表示关注我的用户列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    public PageBean<FollowInfo> findFollowList(long userId,int searchType,int page,int count){
        List<FollowInfo> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = followInfoDao.findFollowList(userId,searchType);
        return PageUtils.getPageBean(p,list);
    }

}
