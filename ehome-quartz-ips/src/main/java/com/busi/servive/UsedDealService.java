package com.busi.servive;

import com.busi.dao.UsedDealDao;
import com.busi.entity.PageBean;
import com.busi.entity.UsedDeal;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 二手
 * @author: ZHaoJiaJie
 * @create: 2018-09-18 17:41
 */
@Service
public class UsedDealService {

    @Autowired
    private UsedDealDao usedDealDao;

    /***
     * 更新公告状态
     * @param usedDeal
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateStatus(UsedDeal usedDeal) {
        return usedDealDao.updateStatus(usedDeal);
    }

    /***
     * 根据ID查询（定时任务）
     * @param id
     * @return
     */
    public UsedDeal findUserById2(long id) {
        return usedDealDao.findUserById2(id);
    }

}
