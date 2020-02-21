package com.busi.service;

import com.busi.dao.EpidemicSituationDao;
import com.busi.entity.*;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 疫情
 * @author: ZHaoJiaJie
 * @create: 2020-02-15 11:26:52
 */
@Service
public class EpidemicSituationService {
    @Autowired
    private EpidemicSituationDao epidemicSituationDao;

    /***
     * 新建
     * @param epidemicSituation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(EpidemicSituation epidemicSituation) {
        return epidemicSituationDao.add(epidemicSituation);
    }

    /***
     * 查询列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<EpidemicSituation> findList(int page, int count) {

        List<EpidemicSituation> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findList();

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询疫情(最新一条)
     * @return
     */
    public EpidemicSituation findNew() {
        return epidemicSituationDao.findNew();
    }


    /***
     * 查询疫情(天气平台)
     * @return
     */
    public EpidemicSituationTianqi findNewEStianQi() {
        return epidemicSituationDao.findNewEStianQi();
    }


    /***
     * 查询列表(天气平台)
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<EpidemicSituationTianqi> findTQlist(int page, int count) {

        List<EpidemicSituationTianqi> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findTQlist();

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新建我和疫情
     * @param epidemicSituation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addESabout(EpidemicSituationAbout epidemicSituation) {
        return epidemicSituationDao.addESabout(epidemicSituation);
    }

    /***
     * 更新我和疫情
     * @param kitchenDishes
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int changeESabout(EpidemicSituationAbout kitchenDishes) {
        return epidemicSituationDao.changeESabout(kitchenDishes);
    }

    /***
     * 根据ID查询我和疫情
     * @param id
     * @return
     */
    public EpidemicSituationAbout findESabout(long id) {
        return epidemicSituationDao.findESabout(id);
    }

    /***
     * 更新评选作品删除状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(CampaignAwardActivity kitchen) {
        return epidemicSituationDao.updateDel(kitchen);
    }

    /***
     * 更新评选作品审核状态
     * @param kitchen
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateExamine(CampaignAwardActivity kitchen) {
        return epidemicSituationDao.updateExamine(kitchen);
    }

    /***
     * 新增评选作品信息
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addSelection(CampaignAwardActivity selectionActivities) {
        return epidemicSituationDao.addSelection(selectionActivities);
    }

    /***
     * 新增投票
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addVote(CampaignAwardVote selectionVote) {
        return epidemicSituationDao.addVote(selectionVote);
    }

    /***
     * 更新评选作品信息
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateSelection(CampaignAwardActivity selectionActivities) {
        return epidemicSituationDao.updateSelection(selectionActivities);
    }

    /***
     * 更新投票数
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateNumber(CampaignAwardActivity selectionActivities) {
        return epidemicSituationDao.updateNumber(selectionActivities);
    }

    /***
     * 根据ID查询评选作品的详细信息
     * @param id
     * @return
     */
    public CampaignAwardActivity findById(long id) {
        return epidemicSituationDao.findById(id);
    }

    /***
     * 根据ID查询评选作品的详细信息
     * @param id
     * @return
     */
    public CampaignAwardActivity findId(long id, long myId) {
        return epidemicSituationDao.findId(id, myId);
    }

    /***
     * 分页查询审核作品列表
     * @param findType   查询类型： 0待审核（时间倒叙&票数最高），1已审核的  2我审核的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CampaignAwardActivity> findExamineList(long userId, int findType, int page, int count) {
        List<CampaignAwardActivity> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findExamineList(userId, findType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 查询是否给该作品投过票
     * @return
     */
    public CampaignAwardVote findTicket(long myId, long campaignAwardId) {
        return epidemicSituationDao.findTicket(myId, campaignAwardId);
    }

    /***
     * 分页查询评选作品列表
     * @param findType   查询类型： 0综合排序，1票数最高 2时间最新
     * @param userId   用戶ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<CampaignAwardActivity> findsSelectionActivitiesList(long userId, int findType, int page, int count) {
        List<CampaignAwardActivity> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findsSelectionList(userId, findType);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 删除轨迹
     * @param ids
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delTrajectory(String[] ids, long userId) {
        return epidemicSituationDao.delTrajectory(ids, userId);
    }

    /***
     * 更新轨迹
     * @param selectionVote
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int editTrajectory(MyTrajectory selectionVote) {
        return epidemicSituationDao.editTrajectory(selectionVote);
    }

    /***
     * 新增轨迹
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addTrajectory(MyTrajectory selectionActivities) {
        return epidemicSituationDao.addTrajectory(selectionActivities);
    }

    /***
     * 根据ID查询轨迹
     * @param id
     * @return
     */
    public MyTrajectory findTrajectory(long id) {
        return epidemicSituationDao.findTrajectory(id);
    }

    /***
     * 分页查询轨迹列表
     * @param userId   用戶ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<MyTrajectory> findTrajectoryList(long userId, int page, int count) {
        List<MyTrajectory> list = null;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = epidemicSituationDao.findTrajectoryList(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增轨迹
     * @param selectionActivities
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addHtrajectory(HomeTrajectory selectionActivities) {
        return epidemicSituationDao.addHtrajectory(selectionActivities);
    }

    /***
     * 根据ID查询轨迹
     * @param id
     * @return
     */
    public HomeTrajectory findHtrajectory(long id) {
        return epidemicSituationDao.findHtrajectory(id);
    }

    /***
     * 删除轨迹
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delHtrajectory(long id) {
        return epidemicSituationDao.delHtrajectory(id);
    }
}
