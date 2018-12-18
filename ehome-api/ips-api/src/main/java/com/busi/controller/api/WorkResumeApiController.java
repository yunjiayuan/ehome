package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 简历相关接口
 * author：zhaojiajie
 * create time：2018-12-14 14:13:41
 */
public interface WorkResumeApiController {

    /***
     * 新增简历
     * @param workResume
     * @param bindingResult
     * @return
     */
    @PostMapping("addResume")
    ReturnData addResume(@Valid @RequestBody WorkResume workResume, BindingResult bindingResult);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delResume/{id}/{userId}")
    ReturnData delResume(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新简历
     * @Param: workResume
     * @return:
     */
    @PutMapping("updateResume")
    ReturnData updateResume(@Valid @RequestBody WorkResume workResume, BindingResult bindingResult);

    /***
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping("getResume/{id}")
    ReturnData getResume(@PathVariable long id);

    /***
     * 查询简历列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param userId
     * @return
     */
    @GetMapping("findResumeList/{userId}/{page}/{count}")
    ReturnData findResumeList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

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
    @GetMapping("queryResumeList/{jobProvince}/{userId}/{highestEducation}/{positionName}/{jobCity}/{jobDistrict}/{jobType1}/{jobType2}/{workExperience}/{startSalary}/{endSalary}/{page}/{count}")
    ReturnData queryResumeList(@PathVariable int jobProvince, @PathVariable long userId, @PathVariable int highestEducation, @PathVariable int positionName, @PathVariable int jobCity, @PathVariable int jobDistrict, @PathVariable int jobType1, @PathVariable int jobType2, @PathVariable int workExperience, @PathVariable int startSalary, @PathVariable int endSalary, @PathVariable int page, @PathVariable int count);

    /***
     * @param userId
     * @Description: 统计我的求职下各类信息数量
     * @return:
     */
    @GetMapping("statisticsResume/{userId}")
    ReturnData statisticsResume(@PathVariable long userId);

    /**
     * @Description: 更改公开度
     * @Param: id  简历ID
     * @Param: openType  公开程度：0公开  1对认证企业公开  2 只投递企业可见
     * @return:
     */
    @GetMapping("changeOpenType/{id}/{openType}")
    ReturnData changeOpenType(@PathVariable long id, @PathVariable int openType);

    /**
     * @Description: 刷新简历
     * @Param: id  简历ID
     * @return:
     */
    @GetMapping("refreshResume/{id}")
    ReturnData refreshResume(@PathVariable long id);

    /**
     * @Description: 设置默认简历
     * @Param: id  简历ID
     * @return:
     */
    @GetMapping("defaultResumeSet/{id}")
    ReturnData defaultResumeSet(@PathVariable long id);

    /***
     * 查询默认简历
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findDefaultResume/{userId}")
    ReturnData findDefaultResume(@PathVariable long userId);

    /**
     * @Description: 删除图片
     * @return:
     */
    @DeleteMapping("delResumePic/{id}/{delImgUrl}")
    ReturnData delResumePic(@PathVariable long id, @PathVariable String delImgUrl);

    /***
     * 查询用户是否存在简历
     * @param userId  用户ID
     * @return
     */
    @GetMapping("findExistence/{userId}")
    ReturnData findExistence(@PathVariable long userId);

    /***
     * 新增工作经验
     * @param workExperience
     * @param bindingResult
     * @return
     */
    @PostMapping("addExperience")
    ReturnData addExperience(@Valid @RequestBody WorkExperience workExperience, BindingResult bindingResult);

    /**
     * @Description: 删除工作经验
     * @return:
     */
    @DeleteMapping("delExperience/{id}/{userId}")
    ReturnData delExperience(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新工作经验
     * @Param: workExperience
     * @return:
     */
    @PutMapping("updateExperience")
    ReturnData updateExperience(@Valid @RequestBody WorkExperience workExperience, BindingResult bindingResult);

    /***
     * 查询工作经验详情
     * @param id  工作经验ID
     * @return
     */
    @GetMapping("getExperience/{id}")
    ReturnData getExperience(@PathVariable long id);

    /***
     * 查询工作经验列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    @GetMapping("findExperienceList/{id}/{page}/{count}")
    ReturnData findExperienceList(@PathVariable long id, @PathVariable int page, @PathVariable int count);

    /***
     * 新增教育经历
     * @param workEducation
     * @param bindingResult
     * @return
     */
    @PostMapping("addEducation")
    ReturnData addEducation(@Valid @RequestBody WorkEducation workEducation, BindingResult bindingResult);

    /**
     * @Description: 删除教育经历
     * @return:
     */
    @DeleteMapping("delEducation/{id}/{userId}")
    ReturnData delEducation(@PathVariable long id, @PathVariable long userId);

    /**
     * @Description: 更新教育经历
     * @Param: workExperience
     * @return:
     */
    @PutMapping("updateEducation")
    ReturnData updateEducation(@Valid @RequestBody WorkEducation workEducation, BindingResult bindingResult);

    /***
     * 查询教育经历详情
     * @param id  工作经验ID
     * @return
     */
    @GetMapping("getEducation/{id}")
    ReturnData getEducation(@PathVariable long id);

    /***
     * 查询教育经历列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param id  简历ID
     * @return
     */
    @GetMapping("findEducationList/{id}/{page}/{count}")
    ReturnData findEducationList(@PathVariable long id, @PathVariable int page, @PathVariable int count);

    /***
     * 下载简历
     * @param workDowRecord
     * @param bindingResult
     * @return
     */
    @PostMapping("downloadResume")
    ReturnData downloadResume(@Valid @RequestBody WorkDowRecord workDowRecord, BindingResult bindingResult);

    /***
     * 查询剩余下载次数
     * @param userId  工作经验ID
     * @return
     */
    @GetMapping("findRemainder/{userId}")
    ReturnData findRemainder(@PathVariable long userId);

    /***
     * 查询下载记录列表
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity  身份区分：0企业查 1简历用户查
     * @return
     */
    @GetMapping("findDowResume/{identity}/{page}/{count}")
    ReturnData findDowResume(@PathVariable int identity, @PathVariable int page, @PathVariable int count);

    /***
     * @param id  被下载简历ID
     * @Description: 统计我的求职下各类信息数量
     * @return:
     */
    @GetMapping("findResumeCount/{id}")
    ReturnData findResumeCount(@PathVariable long id);

}