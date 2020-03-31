package com.busi.service;

import com.busi.dao.CommunityResidentTagDao;
import com.busi.entity.CommunityResidentTag;
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
 * @description: 居委会身份标签
 * @author: ZJJ
 * @create: 2020-03-30 16:08:21
 */
@Service
public class CommunityResidentTagService {
    @Autowired
    private CommunityResidentTagDao blogTagDao;

    /***
     * 新增标签
     * @param homeBlogTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(CommunityResidentTag homeBlogTag) {
        return blogTagDao.add(homeBlogTag);
    }

    /***
     * 编辑标签
     * @param homeBlogTag
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(CommunityResidentTag homeBlogTag) {
        return blogTagDao.update(homeBlogTag);
    }

    /***
     * 查询标签列表
     * @return
     */
    public PageBean<CommunityResidentTag> findList(long id, int page, int count) {
        List<CommunityResidentTag> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = blogTagDao.findList(id);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除居委会标签
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delTags(String[] ids) {
        return blogTagDao.delTags(ids);
    }
}
