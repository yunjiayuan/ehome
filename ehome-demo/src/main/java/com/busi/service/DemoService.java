package com.busi.service;

import com.busi.dao.DemoDao;
import com.busi.entity.Demo;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/6/11 16:39
 */
@Service
public class DemoService {

    @Autowired
    private DemoDao demoDao;
    /***
     * 新增demo
     * @param demo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add( Demo demo){
        return demoDao.add(demo);
    }

    /***
     * 根据用户ID查询demo信息
     * @param id
     * @return
     */
    public Demo findDemoById(long id){
        return demoDao.findDemoById(id);
    }

    /***
     * 更新
     * @param demo
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(Demo demo){
        return  demoDao.update(demo);
    }

    /***
     * 删除
     * @param id
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int delete(long id){
        return demoDao.delete(id);
    }

    /***
     * 分页条件查询
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<Demo> findList(int page, int count) {
        List<Demo> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = demoDao.findList();
        return PageUtils.getPageBean(p,list);
    }
}
