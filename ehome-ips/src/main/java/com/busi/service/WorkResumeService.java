package com.busi.service;

import com.busi.dao.WorkResumeDao;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import freemarker.core.CombinedMarkupOutputFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 简历
 * @author: ZHaoJiaJie
 * @create: 2018-12-21 10:39
 */
@Service
public class WorkResumeService {

    @Autowired
    private WorkResumeDao workResumeDao;

    /***
     * 新增简历
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addResume(WorkResume workResume) {
        return workResumeDao.addResume(workResume);
    }

    /***
     * 统计该用户简历数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId) {
        return workResumeDao.findNum(userId);
    }

    /***
     * 根据ID查询简历
     * @param id
     * @return
     */
    public WorkResume findById(long id) {
        return workResumeDao.findById(id);
    }

    /***
     * 更新简历删除状态
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDel(WorkResume workResume) {
        return workResumeDao.updateDel(workResume);
    }

    /***
     * 查询当前用户的所有简历
     * @param userId
     * @return
     */
    public List<WorkResume> findList(long userId) {
        List<WorkResume> list;
        list = workResumeDao.findList(userId);
        return list;
    }

    /***
     * 更新简历默认状态
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDefault(WorkResume workResume) {
        return workResumeDao.updateDefault(workResume);
    }

    /***
     * 根据ID查询工作经验
     * @param id
     * @return
     */
    public WorkExperience findWork(long userId, long id) {
        return workResumeDao.findWork(userId, id);
    }

    /***
     * 根据ID查询教育经历
     * @param id
     * @return
     */
    public WorkEducation findTeach(long userId, long id) {
        return workResumeDao.findTeach(userId, id);
    }

    /***
     * 更新工作经验删除状态
     * @param workExperience
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateWork(WorkExperience workExperience) {
        return workResumeDao.updateWork(workExperience);
    }

    /***
     * 更新教育经历删除状态
     * @param workEducation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTeach(WorkEducation workEducation) {
        return workResumeDao.updateTeach(workEducation);
    }

    /***
     * 更新简历
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateResume(WorkResume workResume) {
        return workResumeDao.updateResume(workResume);
    }

    /***
     * 根据ID查询简历的完整度
     * @param id
     * @return
     */
    public WorkResumeIntegrity findIntegrity(long id, long userId) {
        return workResumeDao.findIntegrity(userId, id);
    }

    /***
     * 更新简历我的亮点的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIntegrity(WorkResumeIntegrity workResumeIntegrity) {
        return workResumeDao.updateIntegrity(workResumeIntegrity);
    }

    /***
     * 更新简历照片的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIntegrity1(WorkResumeIntegrity workResumeIntegrity) {
        return workResumeDao.updateIntegrity1(workResumeIntegrity);
    }

    /***
     * 更新简历工作经验的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIntegrity2(WorkResumeIntegrity workResumeIntegrity) {
        return workResumeDao.updateIntegrity2(workResumeIntegrity);
    }

    /***
     * 更新简历教育经历的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateIntegrity3(WorkResumeIntegrity workResumeIntegrity) {
        return workResumeDao.updateIntegrity3(workResumeIntegrity);
    }

    /***
     * 新增简历的完整度
     * @param workResumeIntegrity
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addIntegrity(WorkResumeIntegrity workResumeIntegrity) {
        return workResumeDao.addIntegrity(workResumeIntegrity);
    }

    /***
     * 查询简历下载记录
     * @param userId
     * @return
     */
    public WorkDowRecord findDowRecord(long userId, long id) {
        return workResumeDao.findDowRecord(userId, id);
    }

    /***
     * 根据投递简历ID企业注册者ID查询
     * @param id
     * @return
     */
    public WorkApplyRecord findApplyRecord(long userId, long id) {
        return workResumeDao.findApplyRecord(userId, id);
    }

    /***
     * 新增简历浏览记录
     * @param workBrowseRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addBrowseRecord(WorkBrowseRecord workBrowseRecord) {
        return workResumeDao.addBrowseRecord(workBrowseRecord);
    }

    /***
     * 更新简历完整度和浏览量
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateBrowse(WorkResume workResume) {
        return workResumeDao.updateBrowse(workResume);
    }

    /***
     * 分页查询简历列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<WorkResume> findResumeList(long userId, int page, int count) {

        List<WorkResume> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workResumeDao.findList(userId);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 条件查询招聘信息
     * @param userId  用户ID
     * @param jobProvince 求职区域：省
     * @param jobCity  求职区域：城市
     * @param jobDistrict  求职区域：地区或县
     * @param jobType1  一级求职类型
     * @param jobType2  二级求职类型
     * @param workExperience  工作经验
     * @param startSalary  期望薪资:开始
     * @param endSalary 期望薪资:结束
     * @param highestEducation 最高学历
     * @param positionName  职位名称
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<WorkRecruit> findRecruitList(long userId, int jobProvince, int highestEducation, String positionName, int jobCity, int jobDistrict, int jobType1, int jobType2, int workExperience, int startSalary, int endSalary, int page, int count) {

        List<WorkRecruit> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(positionName)) {
            list = workResumeDao.findRecruitList(userId, positionName);
        } else {
            list = workResumeDao.findRecruitList1(userId, jobProvince, jobCity, jobDistrict,
                    jobType1, jobType2, workExperience, startSalary, endSalary, highestEducation);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计我的求职下各类信息数量
     * @param type  0.我的简历 1.面试邀请 2.职位申请记录 3.谁下载了我的简历
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findJobWanted(long userId, int type) {
        return workResumeDao.findJobWanted(userId, type);
    }

    /**
     * @Description: 更改公开度
     * @Param: id  简历ID
     * @Param: openType  公开程度：0公开  1对认证企业公开  2 只投递企业可见
     * @return:
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateOpenType(WorkResume workResume) {
        return workResumeDao.updateOpenType(workResume);
    }

    /**
     * @Description: 刷新简历
     * @Param: id  简历ID
     * @return:
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateTime(WorkResume workResume) {
        return workResumeDao.updateTime(workResume);
    }

    /***
     * 根据用户ID查询默认简历
     * @param userId
     * @return
     */
    public WorkResume findDefault(long userId) {
        return workResumeDao.findDefault(userId);
    }

    /***
     * 更新简历图片
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateImg(WorkResume workResume) {
        return workResumeDao.updateImg(workResume);
    }

    /***
     * 新增工作经验
     * @param workExperience
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addExperience(WorkExperience workExperience) {
        return workResumeDao.addExperience(workExperience);
    }

    /***
     * 更新简历工作经验ID
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateExperience(WorkResume workResume) {
        return workResumeDao.updateExperience(workResume);
    }

    /***
     * 根据ID查询工作经历
     * @param id
     * @return
     */
    public WorkExperience findExperience(long id) {
        return workResumeDao.findExperience(id);
    }

    /***
     * 更新工作经验删除状态
     * @param workExperience
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delExperience(WorkExperience workExperience) {
        return workResumeDao.delExperience(workExperience);
    }

    /***
     * 更新工作经验
     * @param workExperience
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateExp(WorkExperience workExperience) {
        return workResumeDao.updateExp(workExperience);
    }

    /***
     * 查询工作经验列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    public PageBean<WorkExperience> findExperienceList(long id, int page, int count) {

        List<WorkExperience> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workResumeDao.findExperienceList(id);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 新增教育经历
     * @param workEducation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addEducation(WorkEducation workEducation) {
        return workResumeDao.addEducation(workEducation);
    }

    /***
     * 更新简历教育经历ID
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateEducationId(WorkResume workResume) {
        return workResumeDao.updateEducationId(workResume);
    }

    /***
     * 根据ID查询教育经历
     * @param id
     * @return
     */
    public WorkEducation findEducation(long id) {
        return workResumeDao.findEducation(id);
    }

    /***
     * 更新教育经历删除状态
     * @param workEducation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delEducation(WorkEducation workEducation) {
        return workResumeDao.delEducation(workEducation);
    }

    /***
     * 更新教育经历
     * @param workEducation
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateEducation(WorkEducation workEducation) {
        return workResumeDao.updateEducation(workEducation);
    }

    /***
     * 查询教育经历列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    public PageBean<WorkEducation> findEducationList(long id, int page, int count) {

        List<WorkEducation> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workResumeDao.findEducationList(id);

        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据企业ID查询
     * @param id
     * @return
     */
    public WorkEnterprise findApply(long id) {
        return workResumeDao.findApply(id);
    }

    /***
     * 根据下载用户ID查询下载次数记录
     * @param userId
     * @return
     */
    public WorkDowLimit findDowLimit(long userId) {
        return workResumeDao.findDowLimit(userId);
    }

    /***
     * 新增下载次数记录
     * @param workDowLimit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDowLimit(WorkDowLimit workDowLimit) {
        return workResumeDao.addDowLimit(workDowLimit);
    }

    /***
     * 更新下载次数记录
     * @param workDowLimit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDowLimit(WorkDowLimit workDowLimit) {
        return workResumeDao.updateDowLimit(workDowLimit);
    }

    /***
     * 新增下载记录
     * @param workDowRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addDow(WorkDowRecord workDowRecord) {
        return workResumeDao.addDow(workDowRecord);
    }

    /***
     * 更新简历下载量
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDownloads(WorkResume workResume) {
        return workResumeDao.updateDownloads(workResume);
    }

    /***
     * 更新企业简历下载量
     * @param workEnterprise
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateEnDownloads(WorkEnterprise workEnterprise) {
        return workResumeDao.updateEnDownloads(workEnterprise);
    }

    /***
     * 新增职位记录
     * @param workApplyRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addApplyRecord(WorkApplyRecord workApplyRecord) {
        return workResumeDao.addApplyRecord(workApplyRecord);
    }

    /***
     * 查询下载记录列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity 身份区分：0求职者查 1企业查  简历ID
     * @param userId   用户ID
     * @return
     */
    public PageBean<WorkDowRecord> findDowList(int identity, long userId, int page, int count) {

        List<WorkDowRecord> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workResumeDao.findDowList(identity, userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 统计用户简历投递数，下载量，浏览量
     * @param id  被下载简历ID
     * @param type  0 投递数 1下载量 2浏览量
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int countDownloader(long id, int type) {
        return workResumeDao.countDownloader(id, type);
    }

    /***
     * 更新简历主动投递数
     * @param workResume
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDelivery(WorkResume workResume) {
        return workResumeDao.updateDelivery(workResume);
    }

    /***
     * 批量查询指定的简历
     * @param id
     * @return
     */
    public List<WorkResume> findResumeList(String[] id) {
        List<WorkResume> list;
        list = workResumeDao.findResumeList(id);
        return list;
    }

    /***
     * 批量查询指定的简历
     * @param id
     * @return
     */
    public List<WorkDowRecord> findDowRecords(long userId, String[] id) {
        List<WorkDowRecord> list;
        list = workResumeDao.findDowRecords(userId, id);
        return list;
    }

}
