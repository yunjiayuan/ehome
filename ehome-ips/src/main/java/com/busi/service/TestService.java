package com.busi.service;

import com.busi.dao.TestDao;
import com.busi.entity.PageBean;
import com.busi.entity.Test;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;


/**
 * 用户Service
 * author：zhaojiajie
 * create time：2018-7-24 16:57:24
 */
@Service
public class TestService {

    @Autowired
    private TestDao testDao;
    /***
     * 新增用户
     * @param test
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( Test test){
        return testDao.add(test);
    }

    /***
     * 删除
     * @param id 将要删除的ID
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int del(long id){
        return testDao.del(id);
    }

    /***
     * 更新
     * @param test
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(Test test){
        return  testDao.update(test);
    }

    /***
     * 根据用户ID查询userInfo信息
     * @param id
     * @return
     */
    public Test findUserById(long id){
        return testDao.findUserById(id);
    }

    /***
     * 分页条件查询
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Test> findList(long userId, int page, int count) {

        List<Test> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = testDao.findList(userId);

        return PageUtils.getPageBean(p,list);
    }
}
