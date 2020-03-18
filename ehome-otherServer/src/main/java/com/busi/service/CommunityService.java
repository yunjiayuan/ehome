package com.busi.service;

import com.busi.dao.CommunityDao;
import com.busi.entity.Community;
import com.busi.entity.CommunityResident;
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
 * @description: 居委会
 * @author: ZHaoJiaJie
 * @create: 2020-03-18 11:32:23
 */
@Service
public class CommunityService {

    @Autowired
    private CommunityDao epidemicSituationDao;

    /***
     * 更新居委会
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunity(Community selectionVote) {
        return epidemicSituationDao.changeCommunity(selectionVote);
    }

    /***
     * 新增居委会
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addCommunity(Community selectionActivities) {
        return epidemicSituationDao.addCommunity(selectionActivities);
    }

    /***
     * 根据ID查询居委会
     * @param id
     * @return
     */
    public Community findCommunity(long id) {
        return epidemicSituationDao.findCommunity(id);
    }

    /***
     * 查询居委会列表
     * @param lon     经度
     * @param lat     纬度
     * @param string    模糊搜索
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<Community> findCommunityList(double lon, double lat, String string, int province, int city, int district, int page, int count) {
        List<Community> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findCommunityList(lon, lat, string, province, city, district);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询居民
     * @param userId
     * @return
     */
    public CommunityResident findResident(long communityId, long userId) {
        return epidemicSituationDao.findResident(communityId, userId);
    }

    /***
     * 查询是否已加入居委会
     * @param userId
     * @return
     */
    public CommunityResident findJoin(long userId) {
        return epidemicSituationDao.findJoin(userId);
    }

    /***
     * 删除居民
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delResident(String[] ids) {
        return epidemicSituationDao.delResident(ids);
    }

    /***
     * 更新居民
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeResident(CommunityResident selectionVote) {
        return epidemicSituationDao.changeResident(selectionVote);
    }

    /***
     * 加入居委会
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addResident(CommunityResident selectionActivities) {
        return epidemicSituationDao.addResident(selectionActivities);
    }

    /***
     * 查询居民列表
     * @param communityId    居委会
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<CommunityResident> findResidentList(long communityId, int page, int count) {
        List<CommunityResident> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findResidentList(communityId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询指定居民列表
     * @param userIds    用户
     * @param communityId    居委会
     * @return
     */
    public List<CommunityResident> findIsList(long communityId, String userIds) {
        List<CommunityResident> list = null;
        list = epidemicSituationDao.findIsList(communityId, userIds.split(","));
        return list;
    }
}
