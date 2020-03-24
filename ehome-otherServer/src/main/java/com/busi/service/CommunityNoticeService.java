package com.busi.service;

import com.busi.dao.CommunityNoticeDao;
import com.busi.entity.CommunityNotice;
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
 * @description: 公告
 * @author: ZHaoJiaJie
 * @create: 2020-03-23 16:55:51
 */
@Service
public class CommunityNoticeService {
    @Autowired
    private CommunityNoticeDao todayNoticeDao;

    /***
     * 新增
     * @param todayNotice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(CommunityNotice todayNotice) {
        return todayNoticeDao.add(todayNotice);
    }

    /***
     * 更新
     * @param todayNotice
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int editNotice(CommunityNotice todayNotice) {
        return todayNoticeDao.editNotice(todayNotice);
    }

    /***
     * 删除
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String ids) {
        return todayNoticeDao.del(ids);
    }

    /***
     * 查询列表
     */
    public PageBean<CommunityNotice> findList(long communityId, int type, int page, int count) {
        List<CommunityNotice> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = todayNoticeDao.findList(communityId, type);

        return PageUtils.getPageBean(p, list);
    }

}
