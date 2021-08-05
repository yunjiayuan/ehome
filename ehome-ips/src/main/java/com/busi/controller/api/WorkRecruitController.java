package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.WorkRecruitService;
import com.busi.service.WorkResumeService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 招聘相关接口
 * @author: ZHaoJiaJie
 * @create: 2019-01-02 14:34
 */
@RestController
public class WorkRecruitController extends BaseController implements WorkRecruitApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    WorkRecruitService workRecruitService;

    @Autowired
    WorkResumeService workResumeService;

    @Autowired
    UserMembershipUtils userMembershipUtils;

    @Autowired
    UserAccountSecurityUtils userAccountSecurityUtils;

    /***
     * 新增企业
     * @param workEnterprise
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addEnterprise(@Valid @RequestBody WorkEnterprise workEnterprise, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workEnterprise.getJobProvince(), workEnterprise.getJobCity(), workEnterprise.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        WorkEnterprise enterprise = workRecruitService.getEnterprise(workEnterprise.getUserId());
        if (enterprise != null) {
            return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE, "新增企业失败，企业已存在！", new JSONObject());
        }
        workEnterprise.setAddTime(new Date());
        workRecruitService.addEnterprise(workEnterprise);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workEnterprise.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * @Description: 更新企业
     * @Param: workEnterprise
     * @return:
     */
    @Override
    public ReturnData updateEnterprise(@Valid @RequestBody WorkEnterprise workEnterprise, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workEnterprise.getJobProvince(), workEnterprise.getJobCity(), workEnterprise.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        workRecruitService.updateEnterprise(workEnterprise);

        if (!CommonUtils.checkFull(workEnterprise.getDelImgUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(workEnterprise.getUserId(), workEnterprise.getDelImgUrls());
        }
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workEnterprise.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询企业详情
     * @param id  企业ID
     * @return
     */
    @Override
    public ReturnData getEnterprise(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        WorkEnterprise enterprise = workRecruitService.getEnter(id);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", enterprise);
    }

    /***
     * 查询是否创建企业
     * @param userId   用户
     * @return
     */
    @Override
    public ReturnData getFoundEnterprise(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        int state = 0;//企业信息状态  0没有  1已有
        String corporateName = "";        //公司名称
        long companyId = 0;        //公司ID
        WorkEnterprise enterprise = workRecruitService.getEnterprise(userId);
        if (enterprise != null) {
            state = 1;
            corporateName = enterprise.getCorporateName();
            companyId = enterprise.getId();
        }
        Map<String, Object> map = new HashMap<>();
        map.put("state", state);
        map.put("companyId", companyId);
        map.put("corporateName", corporateName);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增职位申请
     * @param workApplyRecord
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData newApply(@Valid @RequestBody WorkApplyRecord workApplyRecord, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Date date = new Date();
        long days7 = 1000 * 60 * 60 * 24 * 7;
        long days90 = 1000 * 60 * 60 * 24 * 90;
        WorkRecruit is = workRecruitService.findRecruit(workApplyRecord.getRecruitId());//招聘信息[不能是自己]
        if (is == null)
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您申请的招聘信息已过期或已被删除，再看看其他职位吧!", new JSONObject());
        WorkResume is2 = workResumeService.findById(workApplyRecord.getResumeId());//投递简历
        if (is2 == null)
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "您将要投递的简历不存在!", new JSONObject());
        if (CommonUtils.getMyId() == is.getUserId())
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "很抱歉，您不能申请自己名下企业的职位!", new JSONObject());
        long currentTime = new Date().getTime();//当前时间
        WorkApplyRecord record = workRecruitService.findApply(workApplyRecord.getUserId(), workApplyRecord.getResumeId(), workApplyRecord.getRecruitId());
        if (record != null) {
            long refreshTime = record.getRefreshTime().getTime();//刷新时间
            if ((currentTime - days7) < refreshTime) {//合格状态下判断当前时间与刷新时间，刷新时间是不是在7天以内
                return returnData(StatusCode.CODE_POSITION_REPEAT.CODE_VALUE, "职位申请失败，您本周已经申请过该职位了，下周再来试试吧！", new JSONObject());
            } else if ((currentTime - days90) < refreshTime) {//合格状态下判断当前时间与刷新时间，刷新时间是不是在7天以上90天以内，是则更新刷新时间，反之重新申请
                if (record.getEmploymentStatus() > 1) {// 录用状态:0无状态 1通知面试 2录用  3不合格
                    return returnData(StatusCode.CODE_MATCHING_REPEAT.CODE_VALUE, "新增职位申请失败！您与要申请的职位不匹配，再看看其他的职位吧!", new JSONObject());
                }
            }
        }
        workApplyRecord.setAddTime(date);
        workApplyRecord.setRefreshTime(date);
        workApplyRecord.setCompanyId(is.getUserId());

        workRecruitService.addApplyRecord(workApplyRecord);

        //更新招聘信息投递数
        is.setDeliveryNumber(is.getDeliveryNumber() + 1);
        workRecruitService.updateDeliveryNumber(is);

        //更新简历主动投递数
        is2.setDelivery(is2.getDelivery() + 1);
        workResumeService.updateDelivery(is2);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workApplyRecord.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
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
    @Override
    public ReturnData findApplyList(@PathVariable int identity, @PathVariable long userId, @PathVariable int queryType, @PathVariable long recruitId, @PathVariable int employmentStatus, @PathVariable int page, @PathVariable int count) {
        String ids = "";
        JSONObject obb = null;
        List recruitList = null;
        PageBean<WorkApplyRecord> pageBean = null;
        JSONArray jsonArray = new JSONArray();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        pageBean = workRecruitService.findApplyList(userId, identity, queryType, recruitId, employmentStatus, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                WorkApplyRecord record = (WorkApplyRecord) list.get(i);
                if (record != null) {
                    if (identity == 0) {//身份区分：0求职者查 1企业查
                        if (i >= list.size() - 1) {
                            ids += record.getRecruitId();//招聘ID
                        } else {
                            ids += record.getRecruitId() + ",";
                        }
                    } else {
                        if (i >= list.size() - 1) {
                            ids += record.getResumeId();//简历ID
                        } else {
                            ids += record.getResumeId() + ",";
                        }
                    }
                }
            }
            if (identity == 0) {//0求职者查
                recruitList = workRecruitService.findRecruitList(ids.split(","));
                if (recruitList != null && recruitList.size() > 0) {//招聘
                    for (int i = 0; i < recruitList.size(); i++) {
                        WorkRecruit recruit = (WorkRecruit) recruitList.get(i);
                        if (recruit != null) {
                            for (int j = 0; j < list.size(); j++) {//申请记录
                                WorkApplyRecord record = (WorkApplyRecord) list.get(j);
                                if (record != null && record.getRecruitId() == recruit.getId()) {
                                    obb = new JSONObject();
                                    obb.put("id", record.getId());//记录ID
                                    obb.put("recruitId", recruit.getId());//招聘信息ID
                                    obb.put("positionName", recruit.getPositionName());//职位名称
                                    obb.put("corporateName", recruit.getCorporateName());//公司名称
                                    obb.put("refreshTime", sdf.format(record.getRefreshTime()));//刷新时间
                                    obb.put("enterpriseFeedback", record.getEnterpriseFeedback());// 企业反馈: 1企业已查看 2感兴趣  3待反馈
                                    obb.put("employmentStatus", record.getEmploymentStatus());// 录用状态:1通知面试 2录用  3不合格
                                    jsonArray.add(obb);
                                }
                            }
                        }
                    }
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", jsonArray);
                }
            } else {// 1企业查
                List resumeList = null;
                resumeList = workResumeService.findResumeList(ids.split(","));
                if (resumeList != null && resumeList.size() > 0) {//简历
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", resumeList);
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
    }

    /***
     * 新增面试通知
     * @param workInterview
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addInterview(@Valid @RequestBody WorkInterview workInterview, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        Date date = new Date();
        workInterview.setAddTime(date);
        workRecruitService.addInterview(workInterview);
        //更新职位申请状态
        WorkApplyRecord record = workRecruitService.findApply2(workInterview.getNotifiedUserId(), workInterview.getResumeId(), workInterview.getUserId());
        if (record != null) {
            record.setRefreshTime(date);
            record.setEmploymentStatus(1);
            record.setEnterpriseFeedback(1);
            workRecruitService.updateApplyRecord(record);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
    }

    /***
     * 更新面试通知
     * @param workInterview
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateInterview(@Valid @RequestBody WorkInterview workInterview, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        workRecruitService.updateInterview(workInterview);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
    }

    /***
     * 删除面试通知
     * @param id
     * @return
     */
    @Override
    public ReturnData delInterview(@PathVariable long id) {
        WorkInterview work = workRecruitService.findInterview(id);
        if (work != null) {
            work.setDeleteState(1);
            workRecruitService.delInterview(work);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONArray());
    }

    /***
     * 查询面试通知列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity    身份区分：0求职者查 1企业查
     * @return
     */
    @Override
    public ReturnData findInterviewList(@PathVariable int identity, @PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (identity < 0 || identity > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "identity参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkInterview> pageBean;
        pageBean = workRecruitService.findInterviewList(identity, userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 根据ID查询面试通知详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findInterview(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        WorkInterview work = workRecruitService.findInterview(id);
        if (work == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", work);
    }

    /***
     * 更新面试标签
     * @param workApplyRecord
     * @param bindingResult
     * @return
     */

    @Override
    public ReturnData updateInterviewLabel(@Valid @RequestBody WorkApplyRecord workApplyRecord, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        WorkApplyRecord record = workRecruitService.findApply2(workApplyRecord.getUserId(), workApplyRecord.getResumeId(), workApplyRecord.getCompanyId());
        if (record != null) {
            workApplyRecord.setRefreshTime(new Date());
            workRecruitService.updateInterviewLabel(workApplyRecord);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 新增招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workRecruit.getJobProvince(), workRecruit.getJobCity(), workRecruit.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        //判断该用户招聘信息数量 最多10条
        int num = workRecruitService.findRecruitNum(workRecruit.getUserId());
        if (num >= 10) {
            return returnData(StatusCode.CODE_RECRUIT_TOPLIMIT.CODE_VALUE, "新增招聘信息数量超过上限,拒绝新增！", new JSONObject());
        }
        workRecruit.setAddTime(new Date());
        workRecruit.setRefreshTime(new Date());
        workRecruitService.addRecruit(workRecruit);
        //新增任务
        mqUtils.sendTaskMQ(workRecruit.getUserId(), 1, 3);
        //新增足迹
        mqUtils.sendFootmarkMQ(workRecruit.getUserId(), workRecruit.getPositionName(), null, null, null, workRecruit.getId() + "," + 8, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workRecruit.getId());

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证地区
        if (!CommonUtils.checkProvince_city_district(0, workRecruit.getJobProvince(), workRecruit.getJobCity(), workRecruit.getJobDistrict())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "省、市、区参数不匹配", new JSONObject());
        }
        workRecruit.setRefreshTime(new Date());
        workRecruitService.updateRecruit(workRecruit);

        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_WORKRECRUIT + workRecruit.getId(), 0);
        Map<String, Object> map = new HashMap<>();
        map.put("infoId", workRecruit.getId());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 更新招聘信息上下架状态
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData updateRecruitState(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        workRecruit.setRefreshTime(new Date());
        workRecruitService.updateRecruitState(workRecruit);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除招聘信息
     * @param id
     * @return
     */
    @Override
    public ReturnData delRecruit(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        // 查询数据库
        WorkRecruit lf = workRecruitService.findRecruit(id);
        if (lf == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        lf.setState(1);
        workRecruitService.delRecruit(lf);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_WORKRECRUIT + id, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 根据ID查询招聘详情
     * @param id
     * @return
     */
    @Override
    public ReturnData findRecruit(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        WorkRecruit lf = workRecruitService.findRecruit(id);
        if (lf == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", lf);
    }

    /***
     * 查询招聘列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findRecruitList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkRecruit> pageBean = null;
        pageBean = workRecruitService.findRecruitLists(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 条件查询简历信息
     * @param jobProvince  工作区域:省 默认-1
     * @param jobCity  工作区域:城市 默认-1
     * @param jobDistrict  工作区域:地区或县 默认-1
     * @param positionType1     一级分类:职位名称
     * @param positionType2     二级分类:职位类型
     * @param updateTime  更新时间  0不限  1一天以内  2三天以内  3七天以内
     * @param userId  用户
     * @param workingLife  工作年限
     * @param sex  性别
     * @param education  学历要求(仅在条件查询简历时可用,空时为不限)
     * @param photo  照片  仅在条件查询简历时有效  0不限  1有照片的
     * @param startSalary  薪资水平:始
     * @param endSalary  薪资水平:终
     * @param positionName  职位名称
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData queryResumeList(@PathVariable long userId, @PathVariable int jobProvince, @PathVariable int jobCity, @PathVariable int jobDistrict, @PathVariable int updateTime, @PathVariable String positionName, @PathVariable int positionType1, @PathVariable int positionType2,
                                      @PathVariable int startSalary, @PathVariable int endSalary, @PathVariable int workingLife, @PathVariable int sex, @PathVariable String education, @PathVariable int photo, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<WorkResume> pageBean = null;
        pageBean = workRecruitService.queryResumeList(userId, jobProvince, jobCity, jobDistrict,
                positionType1, positionType2, workingLife, sex, education, photo, updateTime, startSalary, endSalary, positionName, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        list = pageBean.getList();
        String ids = "";
        UserInfo userCache = null;
        WorkResume u = null;
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                u = (WorkResume) list.get(i);
                userCache = userInfoUtils.getUserInfo(u.getUserId());
                if (userCache != null) {
                    u.setProTypeId(userCache.getProType());
                    u.setHouseNumber(userCache.getHouseNumber());
                    if (!CommonUtils.checkFull(u.getBirthDay())) {
                        u.setAge(CommonUtils.getAge(u.getBirthDay()));//年龄
                    }
                }
                if (i == list.size() - 1) {
                    ids += u.getId();
                }
                ids += u.getId() + ",";
            }
            //用于简历列表显示下载状态
            List dowList = null;
            dowList = workResumeService.findDowRecords(userId, ids.split(","));
            if (dowList != null && dowList.size() > 0) {
                for (int j = 0; j < dowList.size(); j++) {
                    WorkDowRecord record = (WorkDowRecord) dowList.get(j);
                    for (int i = 0; i < list.size(); i++) {
                        u = (WorkResume) list.get(i);
                        if (u.getId() == record.getResumeId()) {
                            u.setDownState(1);
                        }
                    }
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 刷新招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData refreshRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        workRecruit.setRefreshTime(new Date());
        workRecruitService.refreshRecruit(workRecruit);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询招聘管理
     * @param userId
     * @return
     */
    @Override
    public ReturnData findSupervise(@PathVariable long userId) {
        int cont0 = 0;
        int cont1 = 0;
        int cont2 = 0;
        cont0 = workRecruitService.findSupervise(userId, 0);
        cont1 = workResumeService.findJobWanted(userId, 4);
        cont2 = workRecruitService.findSupervise(userId, 1);

        Map<String, Integer> map = new HashMap<>();
        map.put("cont0", cont0);//应聘简历
        map.put("cont1", cont1);//已下载简历
        map.put("cont2", cont2);//招聘信息
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 查询应聘简历列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @Override
    public ReturnData findApplicationList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //开始查询
        PageBean<WorkApplyRecord> pageBean;
        pageBean = workRecruitService.findApplicationList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
