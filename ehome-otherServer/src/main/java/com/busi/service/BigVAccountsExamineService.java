package com.busi.service;

import com.busi.dao.BigVAccountsExamineDao;
import com.busi.entity.BigVAccountsExamine;
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
 * @description: 大V审核
 * @author: ZHaoJiaJie
 * @create: 2020-07-07 16:00:47
 */
@Service
public class BigVAccountsExamineService {

    @Autowired
    private BigVAccountsExamineDao homeAlbumDao;

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(BigVAccountsExamine homeAlbumPwd) {
        return homeAlbumDao.add(homeAlbumPwd);
    }

    /***
     * 根据用户ID查询
     * @return
     */
    public BigVAccountsExamine findById(long userId) {
        return homeAlbumDao.findById(userId);
    }

    /***
     * 更新
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAppealState(BigVAccountsExamine homeAlbumPwd) {
        return homeAlbumDao.changeAppealState(homeAlbumPwd);
    }

    /***
     * 查询列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<BigVAccountsExamine> findChildAppealList(int page, int count) {

        List<BigVAccountsExamine> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeAlbumDao.findChildAppealList();
        return PageUtils.getPageBean(p, list);
    }
}
