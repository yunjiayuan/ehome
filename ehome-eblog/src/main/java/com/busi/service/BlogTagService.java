package com.busi.service;

import com.busi.dao.BlogTagDao;
import com.busi.entity.HomeBlogTag;
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
 * @description: 生活圈兴趣标签
 * @author: suntj
 * @create: 2020-1-8 17:22:54
 */
@Service
public class BlogTagService {

    @Autowired
    private BlogTagDao blogTagDao;

    /***
     * 新增标签
     * @param homeBlogTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeBlogTag homeBlogTag) {
        return blogTagDao.add(homeBlogTag);
    }

    /***
     * 编辑标签
     * @param homeBlogTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeBlogTag homeBlogTag) {
        return blogTagDao.update(homeBlogTag);
    }

    /***
     * 查询标签列表
     * @return
     */
    public PageBean<HomeBlogTag> findList(int page, int count) {
        List<HomeBlogTag> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = blogTagDao.findList();
        return PageUtils.getPageBean(p,list);
    }

}
