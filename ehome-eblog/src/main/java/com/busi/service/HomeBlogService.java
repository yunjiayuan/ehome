package com.busi.service;

import com.busi.dao.HomeBlogDao;
import com.busi.entity.HomeBlog;
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
public class HomeBlogService {

    @Autowired
    private HomeBlogDao homeBlogDao;

    /***
     * 新增生活圈
     * @param homeBlog
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeBlog homeBlog) {
        return homeBlogDao.add(homeBlog);
    }

    /***
     * 更新生活圈评论数、点赞数、浏览量、转发量
     * @param homeBlog
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBlog(HomeBlog homeBlog) {
        return homeBlogDao.updateBlog(homeBlog);
    }

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id      生活圈ID
     * @param userId  登录者用户ID
     */
    public HomeBlog findBlogInfo(long id, long userId) {
        return homeBlogDao.findBlogInfo(id, userId, "," + userId + ",");
    }

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id      生活圈ID
     * @param userId  登录者用户ID
     */
    public HomeBlog findInfo(long id, long userId) {
        return homeBlogDao.findInfo(id, userId);
    }

    /***
     * 删除指定生活圈接口(只更新状态)
     * @param userId 生活圈发布者用户ID
     * @param id     将要被删除的生活圈
     * @return
     */
    public int delBlog(long id, long userId) {
        return homeBlogDao.delBlog(id, userId);
    }

    /***
     * 查询朋友圈和关注的人列表
     * @param myId          当前登录者用户ID
     * @param firendUserIds 好友用户ID组合
     * @param page          页码 第几页 起始值1
     * @param count         每页条数
     * @return
     */
    public PageBean<HomeBlog> findBlogListByFirend(long myId, String[] firendUserIds, int page, int count) {
        List<HomeBlog> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogDao.findBlogListByFirend(firendUserIds, myId, "," + myId + ",");
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据兴趣标签查询列表
     * @param tags       标签数组格式 1,2,3
     * @param searchType 博文类型：0所有 1只看视频
     * @param userId     当前登录者用户ID  用于判断权限
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    public PageBean<HomeBlog> findBlogListByTags(String[] tags, int searchType, long userId, int page, int count) {
        List<HomeBlog> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogDao.findBlogListByTags(tags, searchType, userId, "," + userId + ",");
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据指定用户ID查询列表
     * @param searchType 博文类型：0查自己 1查别人
     * @param userId     被查询用户ID
     * @return
     */
    public PageBean<HomeBlog> findBlogListByUserId(long userId, int searchType, int page, int count) {
        List<HomeBlog> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogDao.findBlogListByUserId(userId, "," + userId + ",", searchType);
        return PageUtils.getPageBean(p, list);
    }
}
