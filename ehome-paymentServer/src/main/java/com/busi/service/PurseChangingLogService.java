package com.busi.service;

import com.busi.dao.PurseChangingLogDao;
import com.busi.entity.PageBean;
import com.busi.entity.Purse;
import com.busi.entity.PurseChangingLog;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 钱包交易明细相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class PurseChangingLogService {

    @Autowired
    private PurseChangingLogDao purseChangingLogDao;

    /***
     * 新增
     * @param purseChangingLog
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addPurseChangingLog( PurseChangingLog purseChangingLog){
        return purseChangingLogDao.addPurseChangingLog(purseChangingLog);
    }

    /***
     * 根据用户ID查询用户交易明细列表
     * @param userId
     * @return
     */
    public PageBean<PurseChangingLog> findPurseChangingLogList(long userId, int tradeType, int currencyType,
                                                               String beginTime, String endTime,
                                                               int page, int count){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date beginDate = null;
        Date endDate = null;
        try {
            if(!CommonUtils.checkFull(beginTime)){
                beginDate = sdf.parse(beginTime);
            }
            if(!CommonUtils.checkFull(beginTime)){
                endDate = sdf.parse(endTime);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<PurseChangingLog> list;
        Page p = PageHelper.startPage(page,count);//为此行代码下面的第一行sql查询结果进行分页
        list = purseChangingLogDao.findList(userId,tradeType,currencyType,beginDate,endDate);
        return PageUtils.getPageBean(p,list);
    }

}
