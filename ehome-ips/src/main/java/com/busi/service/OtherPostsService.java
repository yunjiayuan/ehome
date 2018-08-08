package com.busi.service;

import com.busi.dao.OtherPostsDao;
import com.busi.entity.OtherPosts;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * 其他公告Service
 * author：zhaojiajie
 * create time：2018-8-7 16:32:53
 */
@Service
public class OtherPostsService {

    @Autowired
    private OtherPostsDao otherPostsDao;
    /***
     * 新增
     * @param otherPosts
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( OtherPosts otherPosts){
        return otherPostsDao.add(otherPosts);
    }

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(long id ,long userId){
        return otherPostsDao.del(id,userId);
    }

    /***
     * 更新
     * @param otherPosts
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(OtherPosts otherPosts){
        return  otherPostsDao.update(otherPosts);
    }

    /***
     * 根据ID查询
     * @param id
     * @return
     */
    public OtherPosts findUserById(long id){
        return otherPostsDao.findUserById(id);
    }

    /***
     * 分页查询
     * @param userId 用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<OtherPosts> findList(long userId, int page, int count) {

        List<OtherPosts> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = otherPostsDao.findList(userId);

        return PageUtils.getPageBean(p,list);
    }
}
