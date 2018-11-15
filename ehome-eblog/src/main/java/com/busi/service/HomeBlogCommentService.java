package com.busi.service;

import com.busi.dao.HomeBlogCommentDao;
import com.busi.entity.HomeBlog;
import com.busi.entity.HomeBlogComment;
import com.busi.entity.HomeBlogMessage;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 生活圈评论
 * @author: ZHaoJiaJie
 * @create: 2018-11-05 17:06
 */
@Service
public class HomeBlogCommentService {

    @Autowired
    private HomeBlogCommentDao homeBlogCommentDao;

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addComment(HomeBlogComment homeBlogComment) {
        return homeBlogCommentDao.addComment(homeBlogComment);
    }

    /***
     * 新增消息
     * @param homeBlogMessage
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addMessage(HomeBlogMessage homeBlogMessage) {
        return homeBlogCommentDao.addMessage(homeBlogMessage);
    }

    /***
     * 查询指定评论
     * @return
     */
    public HomeBlogComment findById(long id, long blogId) {
        return homeBlogCommentDao.find(id, blogId);
    }

    /***
     * 更新评论删除状态
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeBlogComment homeBlogAccess) {
        return homeBlogCommentDao.update(homeBlogAccess);
    }

    /***
     * 查询评论列表
     * @param blogId  博文ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeBlogComment> findList(long blogId, int page, int count) {

        List<HomeBlogComment> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findList(blogId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询博文列表
     * @param blIds  博文IDS
     * @return
     */
    public List<HomeBlog> findIdList(String[] blIds) {

        List<HomeBlog> list;
        list = homeBlogCommentDao.findIdList(blIds);
        return list;
    }

    /***
     * 查询回复列表
     * @param contentId  评论ID
     * @return
     */
    public PageBean<HomeBlogComment> findReplyList(long contentId, int page, int count) {
        List<HomeBlogComment> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findReplyList(contentId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询回复列表
     * @param commentId  评论ID
     * @return
     */
    public List<HomeBlogComment> findMessList(long commentId) {

        List<HomeBlogComment> list;
        list = homeBlogCommentDao.findMessList(commentId);
        return list;
    }

    /***
     * 查询消息列表
     * @param type  查询类型  0所有 1未读 2已读
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeBlogMessage> findMessageList(int type, long userId, int page, int count) {
        List<HomeBlogMessage> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findMessageList(type, userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计该用户未读消息数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long getCount(long userId) {
        return homeBlogCommentDao.getCount(userId);
    }

    /***
     * 更新消息状态
     * @param userId
     * @return
     */
    public int updateState(long userId, String[] ids) {
        return homeBlogCommentDao.updateState(userId, ids);
    }

}
