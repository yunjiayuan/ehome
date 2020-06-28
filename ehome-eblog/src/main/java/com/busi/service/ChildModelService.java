package com.busi.service;

import com.busi.dao.ChildModelDao;
import com.busi.entity.ChildModelPwd;
import com.busi.entity.ChildModelPwdAppeal;
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
 * @description: 儿童锁
 * @author: ZHaoJiaJie
 * @create: 2020-06-16 23:26:10
 */
@Service
public class ChildModelService {

    @Autowired
    private ChildModelDao homeAlbumDao;

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPwd(ChildModelPwd homeAlbumPwd) {
        return homeAlbumDao.addPwd(homeAlbumPwd);
    }

    /***
     * 更新密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePwd(ChildModelPwd homeAlbumPwd) {
        return homeAlbumDao.updatePwd(homeAlbumPwd);
    }

    /***
     * 根据用户ID查询
     * @return
     */
    public ChildModelPwd findById(long userId) {
        return homeAlbumDao.findById(userId);
    }

    /***
     * 删除密码
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delPwd(long userId) {
        return homeAlbumDao.delPwd(userId);
    }

    /***
     * 根据用户ID查询
     * @return
     */
    public List<ChildModelPwdAppeal> findByUserId(long userId) {
        List<ChildModelPwdAppeal> list = null;
        list = homeAlbumDao.findByUserId(userId);
        return list;
    }

    /***
     * 新增
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(ChildModelPwdAppeal homeAlbumPwd) {
        return homeAlbumDao.add(homeAlbumPwd);
    }

    /***
     * 更新
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeAppealState(ChildModelPwdAppeal homeAlbumPwd) {
        return homeAlbumDao.changeAppealState(homeAlbumPwd);
    }

    /***
     * 查询列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<ChildModelPwdAppeal> findChildAppealList(int page, int count) {

        List<ChildModelPwdAppeal> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeAlbumDao.findChildAppealList();
        return PageUtils.getPageBean(p, list);
    }
}
