package com.busi.service;

import com.busi.dao.HomeBlogCommentDao;
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
     * 新增评论消息
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

}
