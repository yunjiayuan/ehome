package com.busi.service;

import com.busi.dao.WorkRecruitDao;
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
 * @description: 招聘相关
 * @author: ZHaoJiaJie
 * @create: 2019-01-03 15:34
 */
@Service
public class WorkRecruitService {

    @Autowired
    private WorkRecruitDao workRecruitDao;

    /***
     * 新增企业
     * @param workEnterprise
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addEnterprise(WorkEnterprise workEnterprise) {
        return workRecruitDao.addEnterprise(workEnterprise);
    }

    /***
     * 根据用户ID查询企业
     * @param userId
     * @return
     */
    public WorkEnterprise getEnterprise(long userId) {
        return workRecruitDao.getEnterprise(userId);
    }

    /***
     * 更新企业
     * @param workEnterprise
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateEnterprise(WorkEnterprise workEnterprise) {
        return workRecruitDao.updateEnterprise(workEnterprise);
    }

    /***
     * 根据ID查询企业
     * @param id
     * @return
     */
    public WorkEnterprise getEnter(long id) {
        return workRecruitDao.getEnter(id);
    }

    /***
     * 招聘信息
     * @param id
     * @return
     */
    public WorkRecruit findRecruit(long id) {
        return workRecruitDao.findRecruit(id);
    }

    /***
     * 职位申请记录
     * @param userId
     * @return
     */
    public WorkApplyRecord findApply(long userId, long resumeId, long recruitId) {
        return workRecruitDao.findApply(userId, resumeId, recruitId);
    }

    /***
     * 职位申请记录
     * @param userId
     * @return
     */
    public WorkApplyRecord findApply2(long userId, long resumeId, long companyId) {
        return workRecruitDao.findApply2(userId, resumeId, companyId);
    }

    /***
     * 新增职位申请记录
     * @param workApplyRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addApplyRecord(WorkApplyRecord workApplyRecord) {
        return workRecruitDao.addApplyRecord(workApplyRecord);
    }

    /***
     * 更新招聘信息投递数
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateDeliveryNumber(WorkRecruit workRecruit) {
        return workRecruitDao.updateDeliveryNumber(workRecruit);
    }

    /***
     * 查询职位申请记录
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param recruitId     招聘ID
     * @param identity    身份区分：0求职者查 1企业查
     * @param queryType   查询方式：0按职位查 1查询近半年的（仅在identity=1时有效）
     * @param employmentStatus     录用状态:默认0不限 1面试 2录用  （仅在identity=1时有效）
     * @return
     */
    public PageBean<WorkApplyRecord> findApplyList(long userId, int identity, int queryType, long recruitId, int employmentStatus, int page, int count) {

        List<WorkApplyRecord> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (identity == 0) {
            list = workRecruitDao.findApplyList(userId);
        } else {
            list = workRecruitDao.findApplyList2(identity, userId, queryType, recruitId, employmentStatus);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 批量查询指定的招聘信息
     * @param id
     * @return
     */
    public List<WorkRecruit> findRecruitList(String[] id) {
        List<WorkRecruit> list;
        list = workRecruitDao.findRecruitList(id);
        return list;
    }

    /***
     * 新增面试通知
     * @param workInterview
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addInterview(WorkInterview workInterview) {
        return workRecruitDao.addInterview(workInterview);
    }

    /***
     * 更新职位申请状态
     * @param applyRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateApplyRecord(WorkApplyRecord applyRecord) {
        return workRecruitDao.updateApplyRecord(applyRecord);
    }

    /***
     * 更新面试通知
     * @param workInterview
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateInterview(WorkInterview workInterview) {
        return workRecruitDao.updateInterview(workInterview);
    }


    /***
     * 更新面试通知删除状态
     * @param workInterview
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delInterview(WorkInterview workInterview) {
        return workRecruitDao.delInterview(workInterview);
    }

    /***
     * 根据ID查询面试通知
     * @param id
     * @return
     */
    public WorkInterview findInterview(long id) {
        return workRecruitDao.findInterview(id);
    }

    /***
     * 查询面试通知列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity    身份区分：0求职者查 1企业查
     * @return
     */
    public PageBean<WorkInterview> findInterviewList(int identity, long userId, int page, int count) {

        List<WorkInterview> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workRecruitDao.findInterviewList(identity, userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 更新面试标签
     * @param workApplyRecord
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateInterviewLabel(WorkApplyRecord workApplyRecord) {
        return workRecruitDao.updateInterviewLabel(workApplyRecord);
    }

    /***
     * 统计该用户招聘信息数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findRecruitNum(long userId) {
        return workRecruitDao.findRecruitNum(userId);
    }

    /***
     * 新增招聘信息
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addRecruit(WorkRecruit workRecruit) {
        return workRecruitDao.addRecruit(workRecruit);
    }

    /***
     * 更新招聘信息
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateRecruit(WorkRecruit workRecruit) {
        return workRecruitDao.updateRecruit(workRecruit);
    }

    /***
     * 更新招聘信息上下架状态
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateRecruitState(WorkRecruit workRecruit) {
        return workRecruitDao.updateRecruitState(workRecruit);
    }

    /***
     * 更新招聘信息删除状态
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delRecruit(WorkRecruit workRecruit) {
        return workRecruitDao.delRecruit(workRecruit);
    }

    /***
     * 查询招聘列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<WorkRecruit> findRecruitLists(long userId, int page, int count) {

        List<WorkRecruit> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workRecruitDao.findRecruitLists(userId);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 条件查询简历信息
     * @param jobProvince  工作区域:省 默认0
     * @param jobCity  工作区域:城市 默认0
     * @param jobDistrict  工作区域:地区或县 默认0
     * @param positionType1     一级分类:职位名称
     * @param positionType2     二级分类:职位类型
     * @param updateTime  更新时间  0不限  1一天以内  2三天以内  3七天以内
     * @param userId  用户
     * @param workingLife  工作年限
     * @param sex  性别
     * @param education  学历要求(仅在条件查询简历时可用,空时为不限)
     * @param photo  照片  仅在条件查询简历时有效  0不限  1有照片的
     * @param startSalary  薪资水平:起
     * @param endSalary  薪资水平:始
     * @param positionName  职位名称
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<WorkResume> queryResumeList(long userId, int jobProvince, int jobCity, int jobDistrict,
                                                int positionType1, int positionType2, int workingLife, int sex, String education, int photo, int updateTime, int startSalary, int endSalary, String positionName, int page, int count) {

        List<WorkResume> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (!CommonUtils.checkFull(positionName)) {
            list = workRecruitDao.queryResumeList1(userId, positionName);
        } else {
            String[] educationArray = null;
            if (!CommonUtils.checkFull(education)) {
                educationArray = education.split(",");
            }
            list = workRecruitDao.queryResumeList2(userId, jobProvince, jobCity, jobDistrict, positionType1, positionType2,
                    workingLife, sex, educationArray, photo, updateTime, startSalary, endSalary);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 刷新招聘信息
     * @param workRecruit
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int refreshRecruit(WorkRecruit workRecruit) {
        return workRecruitDao.refreshRecruit(workRecruit);
    }

    /***
     * 查询招聘管理
     * @param type  0.应聘简历 1.招聘信息
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findSupervise(long userId, int type) {
        return workRecruitDao.findSupervise(userId, type);
    }

    /***
     * 查询应聘简历列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    public PageBean<WorkApplyRecord> findApplicationList(long userId, int page, int count) {

        List<WorkApplyRecord> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = workRecruitDao.findApplicationList(userId);
        return PageUtils.getPageBean(p, list);
    }

}
