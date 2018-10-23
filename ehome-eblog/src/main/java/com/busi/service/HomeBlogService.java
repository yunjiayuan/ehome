package com.busi.service;

import com.busi.dao.HomeBlogDao;
import com.busi.entity.HomeBlog;
import com.busi.entity.PageBean;
import com.busi.entity.UserInfo;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.ibatis.annotations.Select;
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
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(HomeBlog homeBlog){
        return homeBlogDao.add(homeBlog);
    }

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param id
     */
    public HomeBlog findBlogInfo(long id){
        return homeBlogDao.findBlogInfo(id);
    }

    /***
     * 删除指定生活圈接口(只更新状态)
     * @param userId 生活圈发布者用户ID
     * @param id     将要被删除的生活圈
     * @return
     */
    public int delBlog(long id,long userId){
        return homeBlogDao.delBlog(id, userId);
    }

    /***
     * 根据兴趣标签查询列表
     * @param tags       标签数组格式 1,2,3
     * @param searchType 博文类型：0所有 1只看视频
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    PageBean<HomeBlog> findBlogListByTags(String[] tags,int searchType,int page,int count){
        List<HomeBlog> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogDao.findBlogListByTags(tags,searchType);
        return PageUtils.getPageBean(p,list);
    }
}
