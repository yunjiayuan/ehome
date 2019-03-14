package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.WorkResumeService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: ehome
 * @description: 简历相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-12-17 14:00
 */
@RestController
public class WorkResumeController extends BaseController implements WorkResumeApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    WorkResumeService workResumeService;

    @Autowired
    UserMembershipUtils userMembershipUtils;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增简历
     * @param workResume
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addResume(@Valid @RequestBody WorkResume workResume, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workResume.getJobProvince(), workResume.getJobCity(), workResume.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //判断该用户是否已绑定手机邮箱
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
        if (CommonUtils.checkFull(userAccountSecurity.getPhone()) || CommonUtils.checkFull(userAccountSecurity.getEmail())) {
            return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机邮箱!", new JSONObject());
        }

        //判断该用户简历数量 最多10条
        int num = workResumeService.findNum(workResume.getUserId());
        if (num >= 10) {
            return returnData(StatusCode.CODE_RESUME_TOPLIMIT.CODE_VALUE, "新增简历数量超过上限,拒绝新增！", new JSONObject());
        }
        workResume.setAddTime(new Date());
        workResume.setRefreshTime(new Date());
        workResume.setIntegrity(30);
        if (num <= 0) {//判断是不是首增简历（默认第一条为默认简历）
            workResume.setDefaultResume(1);
        }
        workResumeService.addResume(workResume);

        //新增任务
        mqUtils.sendTaskMQ(workResume.getUserId(), 1, 13);
        //新增足迹
        mqUtils.sendFootmarkMQ(workResume.getUserId(), workResume.getPositionName(), workResume.getOpusImgUrl(), null, null, workResume.getId() + "," + 7, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workResume.getId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 删除
     * @return:
     */
    @Override
    public ReturnData delResume(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的简历信息", new JSONObject());
        }
        // 查询数据库
        WorkResume lf = workResumeService.findById(id);
        if (lf == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        List list = null;
        if (lf.getDefaultResume() == 1) {//判断要删除的是不是默认简历
            lf.setState(1);
            workResumeService.updateDel(lf);
            list = workResumeService.findList(userId);
            if (list != null && list.size() > 0) {
                WorkResume s = (WorkResume) list.get(0);
                if (s != null) {
                    s.setDefaultResume(1);//设置新的默认
                    workResumeService.updateDefault(s);
                }
            }
        } else {
            lf.setState(1);
            workResumeService.updateDel(lf);
        }
        //同时删除工作经验&教育经历
        WorkExperience we = workResumeService.findWork(lf.getUserId(), lf.getId());
        WorkEducation we1 = workResumeService.findTeach(lf.getUserId(), lf.getId());
        if (we != null) {
            we.setState(1);
            workResumeService.updateWork(we);
        }
        if (we1 != null) {
            we1.setState(1);
            workResumeService.updateTeach(we1);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_WORKRESUME + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新简历
     * @Param: workResume
     * @return:
     */
    @Override
    public ReturnData updateResume(@Valid @RequestBody WorkResume workResume, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workResume.getJobProvince(), workResume.getJobCity(), workResume.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //判断该用户是否已绑定手机邮箱
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(CommonUtils.getMyId());
        if (CommonUtils.checkFull(userAccountSecurity.getPhone()) || CommonUtils.checkFull(userAccountSecurity.getEmail())) {
            return returnData(StatusCode.CODE_NOT_BIND_PHONE_ERROR.CODE_VALUE, "该用户未绑定手机邮箱!", new JSONObject());
        }
        // 根据perfectType判断更新类型
        // perfectType 完善类型 0基本信息 1求职意向 2我的亮点 3职位名称 4作品
        workResume.setRefreshTime(new Date());
        workResumeService.updateResume(workResume);

        int integrity = 0; //0待完善   1完整
        int integrity2 = 0; //0待完善   1完整
        if (!CommonUtils.checkFull(workResume.getHighlights())) {
            integrity = 1;
        }
        if (!CommonUtils.checkFull(workResume.getOpusImgUrl())) {
            integrity2 = 1;
        }
        //更新完整度
        WorkResumeIntegrity wr = workResumeService.findIntegrity(workResume.getId(), workResume.getUserId());
        if (wr != null) {
            wr.setPhoto(integrity2);
            wr.setHighlights(integrity);
            workResumeService.updateIntegrity(wr);
        } else {
            WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
            integrity1.setPhoto(integrity2);
            integrity1.setHighlights(integrity);
            integrity1.setResumeId(workResume.getId());
            integrity1.setUserId(workResume.getUserId());
            workResumeService.addIntegrity(integrity1);
        }
        if (!CommonUtils.checkFull(workResume.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(workResume.getUserId(), workResume.getDelImgUrls());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_WORKRESUME + workResume.getId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workResume.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询详情
     * @param id
     * @return
     */
    @Override
    public ReturnData getResume(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        int realName = 0;  //是否实名  0 未实名  1已实名
        int workEducation = 0;//教育经历完整状态  0待完善   1完整
        int workExperience = 0;//工作经验完整状态  0待完善   1完整
        int employmentStatus = 0;//录用状态:默认0不限 1通知面试 2录用  3不合格
        WorkResume is = null;
        is = workResumeService.findById(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //判断该用户是否实名
        UserAccountSecurity userAccountSecurity = null;
        userAccountSecurity = userAccountSecurityUtils.getUserAccountSecurity(is.getUserId());
        if (userAccountSecurity != null) {
            if (!CommonUtils.checkFull(userAccountSecurity.getRealName()) && !CommonUtils.checkFull(userAccountSecurity.getIdCard())) {
                realName = 1;
            }
        }
        //判断是否是自己的简历（或以企业的身份下载过此份简历，或被投递过）并返回手机邮箱
        WorkDowRecord wd = null;
        wd = workResumeService.findDowRecord(CommonUtils.getMyId(), id);
        //此处会返回多条记录 应聘者可能投递多个职位(待后续完善)
        List record = workResumeService.findApplyRecord(CommonUtils.getMyId(), id);
        if (wd != null || is.getUserId() == CommonUtils.getMyId()) {
            if (userAccountSecurity != null) {
                if (record != null && record.size() > 0) {
                    WorkApplyRecord record2 = (WorkApplyRecord) record.get(0);
                    if (record2 != null) {
//                        employmentStatus = record2.getEmploymentStatus();
                    }
                }
                is.setEmail(userAccountSecurity.getEmail());
                is.setContactsPhone(userAccountSecurity.getPhone());
            }
        }
        UserInfo userInfo = null;
        userInfo = userInfoUtils.getUserInfo(is.getUserId());
        if (userInfo != null) {
            is.setHead(userInfo.getHead());
        }
        //获取简历完整度
        int integrity = 0;
        WorkResumeIntegrity wr = workResumeService.findIntegrity(is.getId(), is.getUserId());
        if (wr != null) {
            integrity = (wr.getHighlights() == 1 ? 15 : 0) + (wr.getWorkEducation() == 1 ? 20 : 0)
                    + (wr.getPhoto() == 1 ? 15 : 0) + (wr.getWorkExperience() == 1 ? 20 : 0);
            is.setIntegrity(integrity + is.getIntegrity());
            workExperience = wr.getWorkExperience();//工作经验完整状态  0待完善   1完整
            workEducation = wr.getWorkEducation();//教育经历完整状态  0待完善   1完整
        }
        //新增浏览记录
//            mqUtils.sendLookMQ(CommonUtils.getMyId(), id, posts.getPositionName(), 7);
        if (is.getUserId() != CommonUtils.getMyId()) {
            //更新简历浏览记录
            WorkBrowseRecord browseRecord = new WorkBrowseRecord();
            browseRecord.setAddTime(new Date());
            browseRecord.setResumeId(is.getId());
            browseRecord.setUserId(CommonUtils.getMyId());
            is.setBrowseAmount(is.getBrowseAmount() + 1);

            workResumeService.addBrowseRecord(browseRecord);
            workResumeService.updateBrowse(is);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("data", is);
        map.put("realName", realName);
        map.put("experience", workExperience);
        map.put("workEducation", workEducation);
        map.put("employmentStatus", employmentStatus);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询简历列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @return
     */
    @Override
    public ReturnData findResumeList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkResume> pageBean = null;
        pageBean = workResumeService.findResumeList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        int integrity = 0;
        List<WorkResume> list = null;
        list = pageBean.getList();
        WorkResume resume = null;
        if (list != null && list.size() > 0) {
            for (WorkResume aList : list) {
                resume = aList;
                WorkResumeIntegrity wr = workResumeService.findIntegrity(resume.getId(), resume.getUserId());
                if (wr != null && resume != null) {
                    integrity = resume.getIntegrity();
                    integrity += (wr.getHighlights() == 1 ? 15 : 0) + (wr.getWorkEducation() == 1 ? 20 : 0)
                            + (wr.getPhoto() == 1 ? 15 : 0) + (wr.getWorkExperience() == 1 ? 20 : 0);
                    resume.setIntegrity(integrity);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 条件查询招聘信息
     * @param jobProvince 求职区域：省
     * @param jobCity  求职区域：城市
     * @param jobDistrict  求职区域：地区或县
     * @param jobType1  一级求职类型
     * @param jobType2  二级求职类型
     * @param workExperience  工作经验 -1：不限   0：应届生   1：1年以下   2：1-3年   3：3-5年   4：5-10年   5：10年以上
     * @param startSalary  期望薪资:开始
     * @param endSalary 期望薪资:结束
     * @param highestEducation 最高学历
     * @param positionName  职位名称
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData queryRecruitList(@PathVariable int jobProvince, @PathVariable int highestEducation, @PathVariable String positionName, @PathVariable int jobCity, @PathVariable int jobDistrict, @PathVariable int jobType1, @PathVariable int jobType2, @PathVariable int workExperience, @PathVariable int startSalary, @PathVariable int endSalary, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //处理工作年限
        int workExperienceStart = -1;
        int workExperienceEnd = -1;
        if (workExperience == 0) {
            workExperienceStart = 0;
            workExperienceEnd = 0;
        }
        if (workExperience == 1) {
            workExperienceStart = 0;
            workExperienceEnd = 1;
        }
        if (workExperience == 2) {
            workExperienceStart = 1;
            workExperienceEnd = 3;
        }
        if (workExperience == 3) {
            workExperienceStart = 3;
            workExperienceEnd = 5;
        }
        if (workExperience == 4) {
            workExperienceStart = 5;
            workExperienceEnd = 10;
        }
        if (workExperience == 5) {
            workExperienceStart = 10;
        }
        //开始查询
        PageBean<WorkRecruit> pageBean;
        pageBean = workResumeService.findRecruitList(CommonUtils.getMyId(), jobProvince, highestEducation, positionName, jobCity, jobDistrict, jobType1, jobType2, workExperienceStart, workExperienceEnd, startSalary, endSalary, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * @param userId
     * @Description: 统计我的求职下各类信息数量
     * @return:
     */
    @Override
    public ReturnData statisticsResume(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        int orderCont0 = 0;
        int orderCont1 = 0;
        int orderCont2 = 0;
        int orderCont3 = 0;

        orderCont0 = workResumeService.findJobWanted(userId, 0);
        orderCont1 = workResumeService.findJobWanted(userId, 1);
        orderCont2 = workResumeService.findJobWanted(userId, 2);
        orderCont3 = workResumeService.findJobWanted(userId, 3);

        Map<String, Integer> map = new HashMap<>();
        map.put("orderCont0", orderCont0);//我的简历
        map.put("orderCont1", orderCont1);//面试邀请
        map.put("orderCont2", orderCont2);//职位申请记录
        map.put("orderCont3", orderCont3);//谁下载了我的简历

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 更改公开度
     * @Param: id  简历ID
     * @Param: openType  公开程度：0公开  1对认证企业公开  2 只投递企业可见
     * @return:
     */
    @Override
    public ReturnData changeOpenType(@PathVariable long id, @PathVariable int openType) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        WorkResume is = null;
        is = workResumeService.findById(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        is.setOpenType(openType);
        workResumeService.updateOpenType(is);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 刷新简历
     * @Param: id  简历ID
     * @return:
     */
    @Override
    public ReturnData refreshResume(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        WorkResume is = null;
        is = workResumeService.findById(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        is.setRefreshTime(new Date());
        workResumeService.updateTime(is);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 设置默认简历
     * @Param: id  简历ID
     * @return:
     */
    @Override
    public ReturnData defaultResumeSet(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        //设置默认
        WorkResume s = null;
        List list = null;
        WorkResume h = workResumeService.findById(id);
        if (h == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        list = workResumeService.findList(h.getUserId());
        //设置前重置默认(清除上次默认设置)
        if (list != null && list.size() > 0) {
            s = (WorkResume) list.get(0);
            if (s != null) {
                s.setDefaultResume(0);
                workResumeService.updateDefault(s);
            }
        }
        h.setDefaultResume(1);
        workResumeService.updateDefault(h);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询默认简历
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findDefaultResume(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        WorkResume resume = null;
        resume = workResumeService.findDefault(userId);
        if (resume != null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resume);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除图片
     * @return:
     */
    @Override
    public ReturnData delResumePic(@PathVariable long id, @PathVariable String delImgUrl) {
        WorkResume wr = workResumeService.findById(id);
        if (wr != null) {
            String path = wr.getOpusImgUrl();// 图片地址组合
            String path22 = path.replace(delImgUrl, "");
            if (!CommonUtils.checkFull(path22)) {
                path22 = path22.replace(",,", ",");
                if (path22.indexOf(",") == 0) {//处理逗号出现在第一位
                    path22 = path22.substring(1);//从下标1开始截取
                }
                if ((path22.length() - 1) == path22.lastIndexOf(",")) {//处理逗号出现在最后一位
                    path22 = path22.substring(0, path22.lastIndexOf(","));//从开始一直截取到最后一个逗号前（不包含逗号）
                }
                wr.setOpusImgUrl(path22);
            }
            if (CommonUtils.checkFull(wr.getOpusImgUrl())) {
                //更新完整度
                WorkResumeIntegrity wri = workResumeService.findIntegrity(wr.getId(), wr.getUserId());
                if (wri != null) {
                    wri.setPhoto(0);
                    workResumeService.updateIntegrity1(wri);
                } else {
                    WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
                    integrity1.setPhoto(0);
                    integrity1.setResumeId(wr.getId());
                    integrity1.setUserId(wr.getUserId());
                    workResumeService.addIntegrity(integrity1);
                }
            }
            workResumeService.updateImg(wr);
        }
        if (!CommonUtils.checkFull(delImgUrl)) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(wr.getUserId(), delImgUrl);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询用户是否存在简历
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findExistence(@PathVariable long userId) {
        List list = null;
        int whether = 0;// 0没有  1有
        list = workResumeService.findList(userId);
        if (list != null && list.size() > 0) {
            whether = 1;
        }
        Map<String, Integer> map = new HashMap<>();
        map.put("whether", whether);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增工作经验
     * @param workExperience
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addExperience(@Valid @RequestBody WorkExperience workExperience, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        WorkResume is = workResumeService.findById(workExperience.getResumeId());
        if (is == null) {
            return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "新增工作经验失败，简历不存在！", new JSONObject());
        }
        workExperience.setAddTime(new Date());
        workResumeService.addExperience(workExperience);
        String experience = is.getExperienceId();
        if (CommonUtils.checkFull(experience)) {
            experience = "";
        }
        //更新简历工作经验ID
        is.setExperienceId(experience + workExperience.getId() + ",");
        workResumeService.updateExperience(is);
        //更新完整度
        WorkResumeIntegrity wri = workResumeService.findIntegrity(is.getId(), is.getUserId());
        if (wri != null) {
            wri.setWorkExperience(1);
            workResumeService.updateIntegrity2(wri);
        } else {
            WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
            integrity1.setWorkExperience(1);
            integrity1.setResumeId(is.getId());
            integrity1.setUserId(is.getUserId());
            workResumeService.addIntegrity(integrity1);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除工作经验
     * @return:
     */
    @Override
    public ReturnData delExperience(@PathVariable long id, @PathVariable long userId) {
        WorkExperience we = workResumeService.findExperience(id);
        if (we != null) {
            WorkResume wr = workResumeService.findById(we.getResumeId());
            if (wr != null) {
                we.setState(1);
                workResumeService.delExperience(we);
                //更新简历中工作经验ID
                String[] experience = null;
                if (!CommonUtils.checkFull(wr.getExperienceId())) {
                    experience = wr.getExperienceId().split(",");
                    for (int i = 0; i < experience.length; i++) {
                        if (Long.parseLong(experience[i]) == id) {
                            String experienceIds = wr.getExperienceId().replace(id + "", "");
                            experienceIds = experienceIds.replace(",,", ",");
                            if (experienceIds.indexOf(",") == 0) {//处理逗号出现在第一位
                                experienceIds = experienceIds.substring(1);//从下标1开始截取“experienceIds”为截取结果
                            }
                            wr.setExperienceId(experienceIds);
                            workResumeService.updateExperience(wr);
                            break;
                        }
                    }
                }
                if (CommonUtils.checkFull(wr.getExperienceId())) {
                    //更新完整度
                    WorkResumeIntegrity wri = workResumeService.findIntegrity(wr.getId(), wr.getUserId());
                    if (wri != null) {
                        wri.setWorkExperience(0);
                        workResumeService.updateIntegrity2(wri);
                    } else {
                        WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
                        integrity1.setWorkExperience(0);
                        integrity1.setResumeId(wr.getId());
                        integrity1.setUserId(wr.getUserId());
                        workResumeService.addIntegrity(integrity1);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新工作经验
     * @Param: workExperience
     * @return:
     */
    @Override
    public ReturnData updateExperience(@Valid @RequestBody WorkExperience workExperience, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        WorkResume is = workResumeService.findById(workExperience.getResumeId());
        if (is == null) {
            return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "编辑工作经验失败，简历不存在！", new JSONObject());
        }
        if (!CommonUtils.checkFull(is.getExperienceId())) {
            String[] experience = is.getExperienceId().split(",");
            for (int i = 0; i < experience.length; i++) {
                if (Long.parseLong(experience[i]) == workExperience.getId()) {
                    workResumeService.updateExp(workExperience);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询工作经验详情
     * @param id  工作经验ID
     * @return
     */
    @Override
    public ReturnData getExperience(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        WorkExperience is = workResumeService.findExperience(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", is);
    }

    /***
     * 查询工作经验列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    @Override
    public ReturnData findExperienceList(@PathVariable long id, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (id < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkExperience> pageBean;
        pageBean = workResumeService.findExperienceList(id, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 新增教育经历
     * @param workEducation
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addEducation(@Valid @RequestBody WorkEducation workEducation, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        WorkResume is = workResumeService.findById(workEducation.getResumeId());
        if (is == null) {
            return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "新增教育经历失败，简历不存在！", new JSONObject());
        }
        workEducation.setAddTime(new Date());
        workResumeService.addEducation(workEducation);
        String education = is.getEducationId();
        if (CommonUtils.checkFull(education)) {
            education = "";
        }
        is.setEducationId(education + workEducation.getId() + ",");
        workResumeService.updateEducationId(is);
        //更新完整度
        WorkResumeIntegrity wri = workResumeService.findIntegrity(is.getId(), is.getUserId());
        if (wri != null) {
            wri.setWorkEducation(1);
            workResumeService.updateIntegrity3(wri);
        } else {
            WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
            integrity1.setWorkEducation(1);
            integrity1.setResumeId(is.getId());
            integrity1.setUserId(is.getUserId());
            workResumeService.addIntegrity(integrity1);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除教育经历
     * @return:
     */
    @Override
    public ReturnData delEducation(@PathVariable long id, @PathVariable long userId) {
        WorkEducation we = workResumeService.findEducation(id);
        if (we != null) {
            WorkResume wr = workResumeService.findById(we.getResumeId());
            if (wr != null) {
                we.setState(1);
                workResumeService.delEducation(we);
                //更新简历中工作经验ID
                if (!CommonUtils.checkFull(wr.getEducationId())) {
                    String[] educationIds = wr.getEducationId().split(",");
                    for (int i = 0; i < educationIds.length; i++) {
                        if (Long.parseLong(educationIds[i]) == id) {
                            String ids = wr.getEducationId().replace(id + "", "");
                            ids = ids.replace(",,", ",");
                            if (ids.indexOf(",") == 0) {//处理逗号出现在第一位
                                ids = ids.substring(1);//从下标1开始截取“experienceIds”为截取结果
                            }
                            wr.setEducationId(ids);
                            workResumeService.updateEducationId(wr);
                            break;
                        }
                    }
                }
                if (CommonUtils.checkFull(wr.getEducationId())) {
                    //更新完整度
                    WorkResumeIntegrity wri = workResumeService.findIntegrity(wr.getId(), wr.getUserId());
                    if (wri != null) {
                        wri.setWorkEducation(0);
                        workResumeService.updateIntegrity3(wri);
                    } else {
                        WorkResumeIntegrity integrity1 = new WorkResumeIntegrity();
                        integrity1.setWorkEducation(0);
                        integrity1.setResumeId(wr.getId());
                        integrity1.setUserId(wr.getUserId());
                        workResumeService.addIntegrity(integrity1);
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新教育经历
     * @Param: workExperience
     * @return:
     */
    @Override
    public ReturnData updateEducation(@Valid @RequestBody WorkEducation workEducation, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        WorkResume is = workResumeService.findById(workEducation.getResumeId());
        if (is == null) {
            return returnData(StatusCode.CODE_IPS_AFFICHE_NOT_EXIST.CODE_VALUE, "编辑教育经历失败，简历不存在！", new JSONObject());
        }
        if (!CommonUtils.checkFull(is.getEducationId())) {
            String[] educationIds = is.getEducationId().split(",");
            for (int i = 0; i < educationIds.length; i++) {
                if (Long.parseLong(educationIds[i]) == workEducation.getId()) {
                    workResumeService.updateEducation(workEducation);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询教育经历详情
     * @param id  教育经历ID
     * @return
     */
    @Override
    public ReturnData getEducation(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        WorkEducation is = workResumeService.findEducation(id);
        if (is == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", is);
    }

    /***
     * 查询教育经历列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    @Override
    public ReturnData findEducationList(@PathVariable long id, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (id < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkEducation> pageBean;
        pageBean = workResumeService.findEducationList(id, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 下载简历
     * @param workDowRecord
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData downloadResume(@Valid @RequestBody WorkDowRecord workDowRecord, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库(是否下载过简历)
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_IPS_WORKDOWNLOAD + workDowRecord.getCompanyId() + "_" + workDowRecord.getResumeId());
        if (map == null || map.size() <= 0) {
            WorkDowRecord posts = workResumeService.findDowRecord(workDowRecord.getUserId(), workDowRecord.getResumeId());
            if (posts != null) {
                //放入缓存
                map = CommonUtils.objectToMap(posts);
                redisUtils.hmset(Constants.REDIS_KEY_IPS_WORKDOWNLOAD + workDowRecord.getCompanyId() + "_" + workDowRecord.getResumeId(), map, Constants.USER_TIME_OUT);
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经下载过该简历了", new JSONObject());
            }
        } else {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您已经下载过该简历了", new JSONObject());
        }
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(CommonUtils.getMyId());
        int numLimit = Constants.DOWRESUME_COUNT;
        int numLimitTotal = Constants.DOWRESUME_COUNTTOTAL;
        int memberShipStatus = 0;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 50;
            numLimitTotal = 300;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 100;
            numLimitTotal = 800;
        }
        WorkResume is = workResumeService.findById(workDowRecord.getResumeId());
        WorkEnterprise we = workResumeService.findApply(workDowRecord.getCompanyId());
        if (is != null && we != null) {
            if (we.getDownloads() >= numLimitTotal) {
                return returnData(StatusCode.CODE_DOWRESUME_TOPLIMIT.CODE_VALUE, "下载简历操作失败，下载简历总次数已达上限！", new JSONObject());
            }
            //判断该用户是否有过简历下载次数记录
            WorkDowLimit walk = workResumeService.findDowLimit(CommonUtils.getMyId());
            if (walk == null) {
                WorkDowLimit dowLimit = new WorkDowLimit();
                dowLimit.setUserId(CommonUtils.getMyId());
                dowLimit.setLastDowDate(new Date());
                dowLimit.setDowResumeTimes(numLimit - 1);
                workResumeService.addDowLimit(dowLimit);
            } else {
                //更新简历下载记录
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String now = format.format(new Date());//今天日期
                String time = format.format(walk.getLastDowDate());//最后一次下载简历日期
                if (now.equals(time)) {//是当天
                    if (walk.getDowResumeTimes() <= 0) {//次数用尽
                        return returnData(StatusCode.CODE_DOWRESUME_TOPLIMIT.CODE_VALUE, "下载简历操作失败，今日下载简历次数已用完！", new JSONObject());
                    }
                    walk.setDowResumeTimes(walk.getDowResumeTimes() - 1);
                } else {//不是同一天
                    walk.setDowResumeTimes(numLimit - 1);
                    walk.setLastDowDate(new Date());
                }
                workResumeService.updateDowLimit(walk);
            }
            workDowRecord.setAddTime(new Date());
            workDowRecord.setCorporateName(we.getCorporateName());
            workDowRecord.setHighestEducation(is.getHighestEducation());
            workDowRecord.setHighlights(is.getHighlights());
            workDowRecord.setJobProvince(is.getJobProvince());
            workDowRecord.setJobType2(is.getJobType2());
            workDowRecord.setSex(is.getSex());
            workDowRecord.setHead(is.getHeadImgUrl());
            workDowRecord.setWorkExperience(is.getWorkExperience());
            workDowRecord.setName(is.getName());
            workResumeService.addDow(workDowRecord);

            //更新简历被下载量
            is.setDownloads(is.getDownloads() + 1);
            workResumeService.updateDownloads(is);
            //更新企业简历下载量
            we.setDownloads(we.getDownloads() + 1);
            workResumeService.updateEnDownloads(we);

            //新增职位记录（企业下载，为后续标签更新做铺垫）
            WorkApplyRecord is1 = new WorkApplyRecord();
            is1.setUserId(is.getUserId());
            is1.setAddTime(new Date());
            is1.setRefreshTime(new Date());
            is1.setResumeId(is.getId());
            is1.setCompanyId(we.getUserId());
            is1.setEnterpriseFeedback(1);
            is1.setDowtype(1);
            workResumeService.addApplyRecord(is1);

            //更缓存中的简历下载记录对照关系
            redisUtils.hmset(Constants.REDIS_KEY_IPS_WORKDOWNLOAD + workDowRecord.getCompanyId() + "_" + workDowRecord.getResumeId(), map, Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询剩余下载次数
     * @param userId  下载用户ID
     * @return
     */
    @Override
    public ReturnData findRemainder(@PathVariable long userId) {
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(CommonUtils.getMyId());
        int numLimit = Constants.DOWRESUME_COUNT;
        int memberShipStatus = 0;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 50;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 100;
        }
        //判断该用户是否有过简历下载次数记录
        WorkDowLimit walk = workResumeService.findDowLimit(userId);
        if (walk != null) {
            if (walk.getDowResumeTimes() <= 0) {//次数用尽
                return returnData(StatusCode.CODE_DOWRESUME_TOPLIMIT.CODE_VALUE, "下载简历操作失败，今日下载简历次数已用完！", new JSONObject());
            }
            numLimit = walk.getDowResumeTimes();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("numLimit", numLimit);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询下载记录列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity  身份区分：0简历用户查 1企业查
     * @return
     */
    @Override
    public ReturnData findDowResume(@PathVariable int identity, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
//        WorkResume resume = null;
        PageBean<WorkDowRecord> pageBean = null;
        pageBean = workResumeService.findDowList(identity, CommonUtils.getMyId(), page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        list = pageBean.getList();
//        if (list != null && list.size() > 0) {
//            if (identity == 1) {//企业查
//                UserInfo userCache = null;
//                for (int i = 0; i < list.size(); i++) {
//                    WorkDowRecord wdr = (WorkDowRecord) list.get(i);
//                    userCache = userInfoUtils.getUserInfo(wdr.getResumeUserId());
//                    if (userCache != null) {
//                        resume = workResumeService.findById(wdr.getResumeId());
//                        if (resume != null && !CommonUtils.checkFull(resume.getBirthDay())) {
//                            wdr.setAge(CommonUtils.getAge(resume.getBirthDay()));//年龄
//                        }
//                        wdr.setHead(userCache.getHead());
//                        wdr.setProTypeId(userCache.getProType());
//                        wdr.setHouseNumber(userCache.getHouseNumber());
//                    }
//                }
//            }
//        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 统计用户简历投递数，下载量，浏览量
     * @param id  被下载简历ID
     * @return
     */
    @Override
    public ReturnData findResumeCount(@PathVariable long id) {
        int cont0 = 0;
        int cont1 = 0;
        int cont2 = 0;
        cont0 = workResumeService.countDownloader(id, 0);
        cont1 = workResumeService.countDownloader(id, 1);
        cont2 = workResumeService.countDownloader(id, 2);

        Map<String, Object> map = new HashMap<>();
        map.put("cont0", cont0);//投递数
        map.put("cont1", cont1);//下载量
        map.put("cont2", cont2);//浏览量
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}