package com.busi.service;

import com.busi.dao.PropertyDao;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 物业
 * @author: ZHaoJiaJie
 * @create: 2020-04-07 17:10:47
 */
@Service
public class PropertyService {
    @Autowired
    private PropertyDao epidemicSituationDao;

    /***
     * 更新物业
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeProperty(Property selectionVote) {
        return epidemicSituationDao.changeProperty(selectionVote);
    }

    /***
     *  设置所属居委会
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int subordinateProperty(Property selectionVote) {
        return epidemicSituationDao.subordinateProperty(selectionVote);
    }

    /***
     * 刷新物业时间
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeCommunityTime(PropertyResident selectionVote) {
        return epidemicSituationDao.changeCommunityTime(selectionVote);
    }

    /***
     * 新增物业
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addProperty(Property selectionActivities) {
        return epidemicSituationDao.addProperty(selectionActivities);
    }

    /***
     * 查询物业列表
     * @param lon     经度
     * @param lat     纬度
     * @param string    模糊搜索
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<Property> findPropertyList(double lon, double lat, String string, int province, int city, int district, int page, int count) {
        List<Property> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(string)) {
            list = epidemicSituationDao.findPropertyList2(lon, lat, province, city, district);
        } else {
            list = epidemicSituationDao.findPropertyList(string, province, city, district);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询已加入的物业
     * @param userId
     * @return
     */
    public List<PropertyResident> findJoin(long userId) {
        List<PropertyResident> list = null;
        list = epidemicSituationDao.findJoin(userId);
        return list;
    }

    /***
     * 查询指定物业
     * @param ids
     * @return
     */
    public List<Property> findPropertyList2(String ids) {
        List<Property> list = null;
        list = epidemicSituationDao.findPropertyList3(ids.split(","));
        return list;
    }

    /***
     * 查询指定物业居民列表
     * @param ids    居委会ID
     * @return
     */
    public List<PropertyResident> findIsList2(String ids, long userId) {
        List<PropertyResident> list = null;
        list = epidemicSituationDao.findIsList2(ids.split(","), userId);
        return list;
    }

    /***
     * 删除居民
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delResident(int type, String[] ids) {
        return epidemicSituationDao.delResident(type, ids);
    }

    /***
     * 更新居民
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeResident(PropertyResident selectionVote) {
        return epidemicSituationDao.changeResident(selectionVote);
    }

    /***
     * 更新居民标签
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeResidentTag(PropertyResident selectionVote) {
        return epidemicSituationDao.changeResidentTag(selectionVote);
    }

    /***
     * 加入居委会
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addResident(PropertyResident selectionActivities) {
        return epidemicSituationDao.addResident(selectionActivities);
    }

    /***
     * 查询居民列表
     * @param communityId    居委会
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<PropertyResident> findResidentList(int type, long communityId, int page, int count) {
        List<PropertyResident> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findResidentList(type, communityId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询居民
     * @param userId
     * @return
     */
    public PropertyResident findResident(long communityId, long userId) {
        return epidemicSituationDao.findResident(communityId, userId);
    }

    /***
     * 根据ID查询居委会
     * @param id
     * @return
     */
    public Property findProperty(long id) {
        return epidemicSituationDao.findProperty(id);
    }

    /***
     * 查询指定居民列表
     * @param userIds    用户
     * @return
     */
    public List<PropertyResident> findIsList3(String userIds) {
        List<PropertyResident> list = null;
        list = epidemicSituationDao.findIsList3(userIds.split(","));
        return list;
    }

    /***
     * 删除居委会人员设置
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delSetUp(String[] ids) {
        return epidemicSituationDao.delSetUp(ids);
    }

    /***
     * 更新居委会人员设置
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeSetUp(PropertySetUp communityHouse) {
        return epidemicSituationDao.changeSetUp(communityHouse);
    }

    /***
     * 新增居委会人员设置
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSetUp(PropertySetUp communityHouse) {
        return epidemicSituationDao.addSetUp(communityHouse);
    }

    /***
     * 查询居委会人员设置列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<PropertySetUp> findSetUpList(long propertyId, int page, int count) {
        List<PropertySetUp> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findSetUpList(propertyId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新评论数
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBlogCounts(Property homeBlogAccess) {
        return epidemicSituationDao.updateBlogCounts(homeBlogAccess);
    }

    /***
     * 查询管理员列表
     * @param communityId    居委会
     * @return
     */
    public List<PropertyResident> findWardenList(long communityId) {
        List<PropertyResident> list = null;
        list = epidemicSituationDao.findWardenList(communityId);
        return list;
    }
}
