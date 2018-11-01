package com.busi.service;

import com.busi.dao.HomeBlogLikeDao;
import com.busi.entity.HomeBlogLike;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 用户Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class HomeBlogLikeService {

    @Autowired
    private HomeBlogLikeDao homeBlogLikeDao;

    /***
     * 新增点赞接口
     * @param homeBlogLike
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addHomeBlogLike(HomeBlogLike homeBlogLike){
        return homeBlogLikeDao.addHomeBlogLike(homeBlogLike);
    }


    /***
     * 删除点赞接口
     * @param userId 将要删除的点赞用户ID
     * @param blogId 将要操作的生活圈ID
     * @return
     */
    public int delHomeBlogLike(long userId,long blogId){
        return homeBlogLikeDao.delHomeBlogLike(userId,blogId);
    }

    /***
     * 条件查询点赞列表接口
     * @param blogId     将要操作的生活圈ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    public PageBean<HomeBlogLike> findBlogListByFirend(long blogId,int page,int count){
        List<HomeBlogLike> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogLikeDao.findHomeBlogLike(blogId);
        return PageUtils.getPageBean(p,list);
    }

}
