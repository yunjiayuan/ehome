package com.busi.service;

import com.busi.dao.CommunityDao;
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
        if (CommonUtils.checkFull(string)) {
            list = epidemicSituationDao.findCommunityList2(lon, lat, province, city, district);
        } else {
            list = epidemicSituationDao.findCommunityList(string, province, city, district);
        }
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
     * 查询已加入的居委会
     * @param userId
     * @return
     */
    public List<CommunityResident> findJoin(long userId) {
        List<CommunityResident> list = null;
        list = epidemicSituationDao.findJoin(userId);
        return list;
    }

    /***
     * 查询指定居委会
     * @param ids
     * @return
     */
    public List<Community> findCommunityList2(String ids) {
        List<Community> list = null;
        list = epidemicSituationDao.findCommunityList3(ids.split(","));
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
    public PageBean<CommunityResident> findResidentList(int type, long communityId, int page, int count) {
        List<CommunityResident> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findResidentList(type, communityId);
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

    /***
     * 查询管理员列表
     * @param communityId    居委会
     * @return
     */
    public List<CommunityResident> findWardenList(long communityId) {
        List<CommunityResident> list = null;
        list = epidemicSituationDao.findWardenList(communityId);
        return list;
    }

    /***
     * 新增评论
     * @param homeBlogComment
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addComment(CommunityMessageBoard homeBlogComment) {
        return epidemicSituationDao.addComment(homeBlogComment);
    }

    /***
     * 查询指定评论
     * @return
     */
    public CommunityMessageBoard findById(long id) {
        return epidemicSituationDao.find(id);
    }

    /***
     * 更新评论回复数
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateCommentNum(CommunityMessageBoard homeBlogAccess) {
        return epidemicSituationDao.updateCommentNum(homeBlogAccess);
    }

    /***
     * 更新评论数
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBlogCounts(Community homeBlogAccess) {
        return epidemicSituationDao.updateBlogCounts(homeBlogAccess);
    }


    /***
     * 更新评论删除状态
     * @param homeBlogAccess
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(CommunityMessageBoard homeBlogAccess) {
        return epidemicSituationDao.update(homeBlogAccess);
    }

    /***
     * 查询评论列表
     * @param communityId  ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CommunityMessageBoard> findList(int type, long communityId, int page, int count) {

        List<CommunityMessageBoard> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findList(type, communityId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询回复列表
     * @param contentId  评论ID
     * @return
     */
    public PageBean<CommunityMessageBoard> findReplyList(long contentId, int page, int count) {
        List<CommunityMessageBoard> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findReplyList(contentId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询回复列表
     * @param commentId  评论ID
     * @return
     */
    public List<CommunityMessageBoard> findMessList(long commentId) {

        List<CommunityMessageBoard> list;
        list = epidemicSituationDao.findReplyList(commentId);
        return list;
    }

    /***
     * 更新回复删除状态
     * @param ids
     * @return
     */
    public int updateReplyState(String[] ids) {
        return epidemicSituationDao.updateReplyState(ids);
    }

    /***
     * 统计该评论下回复数量
     * @param commentId  评论ID
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long getReplayCount(long commentId) {
        return epidemicSituationDao.getReplayCount(commentId);
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
    public int changeSetUp(CommunitySetUp communityHouse) {
        return epidemicSituationDao.changeSetUp(communityHouse);
    }

    /***
     * 新增居委会人员设置
     * @param communityHouse
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSetUp(CommunitySetUp communityHouse) {
        return epidemicSituationDao.addSetUp(communityHouse);
    }

    /***
     * 查询居委会人员设置列表
     * @param page     页码
     * @param count    条数
     * @return
     */
    public PageBean<CommunitySetUp> findSetUpList(long communityId, int page, int count) {
        List<CommunitySetUp> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findSetUpList(communityId);
        return PageUtils.getPageBean(p, list);
    }
}
