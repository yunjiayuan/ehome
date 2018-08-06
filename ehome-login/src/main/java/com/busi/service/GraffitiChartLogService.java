package com.busi.service;

import com.busi.dao.GraffitiChartLogDao;
import com.busi.entity.GraffitiChartLog;
import com.busi.entity.PageBean;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * 涂鸦记录Service
 * author：SunTianJie
 * create time：2018/6/26 12:36
 */
@Service
public class GraffitiChartLogService {

    @Autowired
    private GraffitiChartLogDao graffitiChartLogDao;

    /***
     * 新增
     * @param graffitiChartLog
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int add(GraffitiChartLog graffitiChartLog){
        return  graffitiChartLogDao.add(graffitiChartLog);
    }


    /***
     * 分页条件查询
     * @param myId 要查询的用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<GraffitiChartLog> findList(long myId , int page, int count) {
        List<GraffitiChartLog> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = graffitiChartLogDao.findList(myId);
        return PageUtils.getPageBean(p,list);
    }

}
