package com.busi.service;

import com.busi.dao.CommunityMessageDao;
import com.busi.entity.CommunityMessage;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 消息
 * author：zhaojiajie
 * create time：2020-03-24 18:32:41
 */
@Mapper
@Repository
public class CommunityMessageService {

    @Autowired
    private CommunityMessageDao homeBlogCommentDao;

    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addMessage(CommunityMessage homeBlogMessage) {
        return homeBlogCommentDao.addMessage(homeBlogMessage);
    }

    /***
     * 查询消息接口
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     用户ID
     * @param type       查询类型  0所有 1未读 2已读
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    public PageBean<CommunityMessage> findMessageList(int communityType, long communityId, int type, long userId, int page, int count) {
        List<CommunityMessage> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findMessageList(communityType, communityId, type, userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 获取未读消息
     * @param communityType     类别   0居委会  1物业
     * @param communityId     type=0时为居委会ID  type=1时为物业ID
     * @param userId     查询用户ID
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long getCount(int communityType, long communityId, long userId) {
        return homeBlogCommentDao.getCount(communityType, communityId, userId);
    }

    /***
     * 更新消息状态
     * @param userId
     * @return
     */
    public int updateState(int communityType, long communityId, long userId, String[] ids) {
        return homeBlogCommentDao.updateState(communityType, communityId, userId, ids);
    }

    /***
     * 更新消息状态
     * @param users
     * @return
     */
    public int updateState2(int communityType, long communityId, String[] users) {
        return homeBlogCommentDao.updateState2(communityType, communityId, users);
    }
}
