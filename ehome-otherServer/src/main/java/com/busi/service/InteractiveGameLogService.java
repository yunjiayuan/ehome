package com.busi.service;

import com.busi.dao.InteractiveGameLogDao;
import com.busi.entity.InteractiveGameLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 钱包信息相关Service
 * author：SunTianJie
 * create time：2018-8-16 11:46:00
 */
@Service
public class InteractiveGameLogService {

    @Autowired
    private InteractiveGameLogDao interactiveGameLogDao;

    /***
     * 新增
     * @param interactiveGameLog
     * @return
     */
    @Transactional(rollbackFor={RuntimeException.class, Exception.class})
    public int addInteractiveGameLog( InteractiveGameLog interactiveGameLog){
        return interactiveGameLogDao.addInteractiveGameLog(interactiveGameLog);
    }


}
