package com.busi.service;

import com.busi.dao.VisitViewDao;
import com.busi.entity.VisitView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 访问量Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class VisitViewService {

    @Autowired
    private VisitViewDao visitViewDao;

    /***
     * 新增访问量信息
     * @param visitView
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(VisitView visitView){
        return  visitViewDao.add(visitView);
    }

    /***
     * 更新访问量信息
     * @param visitView
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int update(VisitView visitView){
        return  visitViewDao.update(visitView);
    }

    /***
     * 查询访问量信息
     * @param userId
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public VisitView findVisitView(long userId){
        return  visitViewDao.findVisitView(userId);
    }

}
