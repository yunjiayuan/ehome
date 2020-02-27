package com.busi.service;

import com.busi.dao.ShopFloorCommentDao;
import com.busi.entity.PageBean;
import com.busi.entity.ShopFloorComment;
import com.busi.entity.ShopFloorGoods;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 搂店评论
 * @author: ZHaoJiaJie
 * @create: 2020-02-27 15:35:06
 */
@Service
public class ShopFloorCommentService {

    @Autowired
    private ShopFloorCommentDao homeBlogCommentDao;

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addComment(ShopFloorComment homeBlogComment) {
        return homeBlogCommentDao.addComment(homeBlogComment);
    }

    /***
     * 查询指定评论
     * @return
     */
    public ShopFloorComment findById(long id) {
        return homeBlogCommentDao.find(id);
    }

     /***
     * 更新评论回复数
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateCommentNum(ShopFloorComment homeBlogAccess) {
        return homeBlogCommentDao.updateCommentNum(homeBlogAccess);
    }

    /***
     * 更新评论数
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBlogCounts(ShopFloorGoods homeBlogAccess) {
        return homeBlogCommentDao.updateBlogCounts(homeBlogAccess);
    }


    /***
     * 更新评论删除状态
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(ShopFloorComment homeBlogAccess) {
        return homeBlogCommentDao.update(homeBlogAccess);
    }

    /***
     * 查询评论列表
     * @param goodsId  商品ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ShopFloorComment> findList(long goodsId, int page, int count) {

        List<ShopFloorComment> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findList(goodsId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询回复列表
     * @param contentId  评论ID
     * @return
     */
    public PageBean<ShopFloorComment> findReplyList(long contentId, int page, int count) {
        List<ShopFloorComment> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogCommentDao.findReplyList(contentId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询回复列表
     * @param commentId  评论ID
     * @return
     */
    public List<ShopFloorComment> findMessList(long commentId) {

        List<ShopFloorComment> list;
        list = homeBlogCommentDao.findReplyList(commentId);
        return list;
    }

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    public int updateReplyState(String[] ids) {
        return homeBlogCommentDao.updateReplyState(ids);
    }

    /***
     * 统计该评论下回复数量
     * @param commentId  评论ID
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long getReplayCount(long commentId) {
        return homeBlogCommentDao.getReplayCount(commentId);
    }
}
