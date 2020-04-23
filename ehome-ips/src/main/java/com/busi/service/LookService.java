package com.busi.service;

import com.busi.dao.LookDao;
import com.busi.entity.Look;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: 浏览记录
 * @author: ZHaoJiaJie
 * @create: 2018-08-24 15:50
 */
@Service
public class LookService {

    @Autowired
    private LookDao lookDao;

    /***
     * 新增
     * @param look
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(Look look) {
        return lookDao.add(look);
    }

    /***
     * 删除
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int del(String[] ids, long userId) {
        return lookDao.del(ids, userId);
    }

    /***
     * 分页查询
     * @param myId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Look> findList(long myId, int page, int count) {

        List<Look> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = lookDao.findList(myId);

        return PageUtils.getPageBean(p, list);
    }
}
