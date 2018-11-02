package com.busi.service;

import com.busi.dao.HomeBlogAccessDao;
import com.busi.entity.HomeBlogAccess;
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
 * @description: 生活圈标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-01 17:39
 */
@Service
public class HomeBlogAccessService {

    @Autowired
    private HomeBlogAccessDao homeBlogAccessDao;

    /***
     * 新增标签
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeBlogAccess homeBlogAccess) {
        return homeBlogAccessDao.add(homeBlogAccess);
    }

    /***
     * 编辑标签
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeBlogAccess homeBlogAccess) {
        return homeBlogAccessDao.update(homeBlogAccess);
    }

    /***
     * 删除
     * @param tagId
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(long tagId, long userId) {
        return homeBlogAccessDao.del(tagId, userId);
    }

    /***
     * 查询指定标签
     * @return
     */
    public HomeBlogAccess find(long id) {
        return homeBlogAccessDao.find(id);
    }

    /***
     * 查询标签列表
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeBlogAccess> findList(long userId, int page, int count) {

        List<HomeBlogAccess> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeBlogAccessDao.findList(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计该用户标签数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return homeBlogAccessDao.findNum(userId);
    }

}
