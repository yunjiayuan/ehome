package com.busi.service;

import com.busi.dao.CommunityEventReportingDao;
import com.busi.entity.CommunityEventReporting;
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
 * @description: 新冠状病毒报备service
 * @author: suntj
 * @create: 2020-03-18 11:32:23
 */
@Service
public class CommunityEventReportingService {

    @Autowired
    private CommunityEventReportingDao communityEventReportingDao;


    /***
     * 删除新冠状病毒报备
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delCommunityEventReporting(String[] ids,long userId) {
        return communityEventReportingDao.delCommunityEventReporting(ids,userId);
    }

    /***
     * 更新新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunityEventReporting(CommunityEventReporting communityEventReporting) {
        return communityEventReportingDao.changeCommunityEventReporting(communityEventReporting);
    }

    /***
     * 更新新冠状病毒报备审核状态
     * @param communityEventReporting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int toExamineCommunityEventReporting(CommunityEventReporting communityEventReporting) {
        return communityEventReportingDao.toExamineCommunityEventReporting(communityEventReporting);
    }

    /***
     * 新增新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCommunityEventReporting(CommunityEventReporting communityEventReporting) {
        return communityEventReportingDao.addCommunityEventReporting(communityEventReporting);
    }

    /***
     * 根据ID查询新冠状病毒报备
     * @param id
     * @return
     */
    public CommunityEventReporting findCommunityEventReporting(long id) {
        return communityEventReportingDao.findCommunityEventReporting(id);
    }

    /***
     * 查询新冠状病毒报备列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<CommunityEventReporting> findCommunityEventReportingList(long communityId,long houseUserId,long communityHouseId,int type,int page, int count) {
        List<CommunityEventReporting> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = communityEventReportingDao.findCommunityEventReportingList(communityId,houseUserId,communityHouseId,type);
        return PageUtils.getPageBean(p, list);
    }

}
