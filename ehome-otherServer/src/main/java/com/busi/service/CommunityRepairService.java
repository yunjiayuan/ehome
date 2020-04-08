package com.busi.service;

import com.busi.dao.CommunityRepairDao;
import com.busi.entity.CommunityRepair;
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
 * @description: 报修
 * @author: ZHaoJiaJie
 * @create: 2020-04-08 17:07:43
 */
@Service
public class CommunityRepairService {

    @Autowired
    private CommunityRepairDao epidemicSituationDao;

    /***
     * 新增
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSetUp(CommunityRepair communityHouse) {
        return epidemicSituationDao.addSetUp(communityHouse);
    }

    /***
     * 查询报修列表
     * @param type    type=0居委会  type=1物业
     * @param communityId   type=0时居委会ID  type=1时物业ID
     * @param userId   查询者
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<CommunityRepair> findSetUpList(int type, long communityId, long userId, int page, int count) {
        List<CommunityRepair> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findSetUpList(type, communityId, userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delSetUp(String[] ids) {
        return epidemicSituationDao.delSetUp(ids);
    }
}
