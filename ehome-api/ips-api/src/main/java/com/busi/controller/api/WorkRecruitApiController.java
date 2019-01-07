package com.busi.controller.api;

import com.busi.entity.*;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 招聘相关接口
 * author：zhaojiajie
 * create time：2018-12-14 14:13:41
 */
public interface WorkRecruitApiController {

    /***
     * 新增企业
     * @param workEnterprise
     * @param bindingResult
     * @return
     */
    @PostMapping("addEnterprise")
    ReturnData addEnterprise(@Valid @RequestBody WorkEnterprise workEnterprise, BindingResult bindingResult);

    /**
     * @Description: 更新企业
     * @Param: workEnterprise
     * @return:
     */
    @PutMapping("updateEnterprise")
    ReturnData updateEnterprise(@Valid @RequestBody WorkEnterprise workEnterprise, BindingResult bindingResult);

    /***
     * 查询企业详情
     * @param id  企业ID
     * @return
     */
    @GetMapping("getEnterprise/{id}")
    ReturnData getEnterprise(@PathVariable long id);

    /***
     * 查询是否创建企业
     * @param userId   用户
     * @return
     */
    @GetMapping("getFoundEnterprise/{userId}")
    ReturnData getFoundEnterprise(@PathVariable long userId);

    /***
     * 新增职位申请
     * @param workApplyRecord
     * @param bindingResult
     * @return
     */
    @PostMapping("newApply")
    ReturnData newApply(@Valid @RequestBody WorkApplyRecord workApplyRecord, BindingResult bindingResult);

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
    @GetMapping("findDowResume/{identity}/{userId}/{queryType}/{recruitId}/{employmentStatus}/{page}/{count}")
    ReturnData findDowResume(@PathVariable int identity, @PathVariable long userId, @PathVariable int queryType, @PathVariable long recruitId, @PathVariable int employmentStatus, @PathVariable int page, @PathVariable int count);

    /***
     * 新增面试通知
     * @param workInterview
     * @param bindingResult
     * @return
     */
    @PostMapping("addInterview")
    ReturnData addInterview(@Valid @RequestBody WorkInterview workInterview, BindingResult bindingResult);

    /***
     * 更新面试通知
     * @param workInterview
     * @param bindingResult
     * @return
     */
    @PutMapping("updateInterview")
    ReturnData updateInterview(@Valid @RequestBody WorkInterview workInterview, BindingResult bindingResult);

    /***
     * 删除面试通知
     * @param id
     * @return
     */
    @DeleteMapping("delInterview/{id}")
    ReturnData delInterview(@PathVariable long id);

    /***
     * 查询面试通知列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @param identity    身份区分：0求职者查 1企业查
     * @return
     */
    @GetMapping("findInterviewList/{identity}/{userId}/{page}/{count}")
    ReturnData findInterviewList(@PathVariable int identity, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 根据ID查询面试通知详情
     * @param id
     * @return
     */
    @GetMapping("findInterview/{id}")
    ReturnData findInterview(@PathVariable long id);

    /***
     * 更新面试标签
     * @param workApplyRecord
     * @param bindingResult
     * @return
     */
    @PutMapping("updateInterviewLabel")
    ReturnData updateInterviewLabel(@Valid @RequestBody WorkApplyRecord workApplyRecord, BindingResult bindingResult);

    /***
     * 新增招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @PostMapping("addRecruit")
    ReturnData addRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult);

    /***
     * 更新招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @PutMapping("updateRecruit")
    ReturnData updateRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult);

    /***
     * 删除招聘信息
     * @param id
     * @return
     */
    @DeleteMapping("delRecruit/{id}")
    ReturnData delRecruit(@PathVariable long id);

    /***
     * 根据ID查询招聘详情
     * @param id
     * @return
     */
    @GetMapping("findRecruit/{id}")
    ReturnData findRecruit(@PathVariable long id);

    /***
     * 查询招聘列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findRecruitList/{userId}/{page}/{count}")
    ReturnData findRecruitList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

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
    @GetMapping("queryResumeList/{userId}/{jobProvince}/{jobCity}/{jobDistrict}/{updateTime}/{positionName}/{positionType1}/{positionType2}/{startSalary}/{endSalary}/{workingLife}/{sex}/{education}/{photo}/{page}/{count}")
    ReturnData queryResumeList(@PathVariable long userId, @PathVariable int jobProvince, @PathVariable int jobCity, @PathVariable int jobDistrict, @PathVariable int updateTime, @PathVariable String positionName, @PathVariable int positionType1, @PathVariable int positionType2,
                               @PathVariable int startSalary, @PathVariable int endSalary, @PathVariable int workingLife, @PathVariable int sex, @PathVariable String education, @PathVariable int photo, @PathVariable int page, @PathVariable int count);

    /***
     * 刷新招聘信息
     * @param workRecruit
     * @param bindingResult
     * @return
     */
    @PutMapping("refreshRecruit")
    ReturnData refreshRecruit(@Valid @RequestBody WorkRecruit workRecruit, BindingResult bindingResult);

    /***
     * 查询招聘管理
     * @param userId
     * @return
     */
    @GetMapping("findSupervise/{userId}")
    ReturnData findSupervise(@PathVariable long userId);

    /***
     * 查询应聘简历列表
     * @param userId
     * @param page     页码 第几页 起始值1
     * @param count    每页条数
     * @return
     */
    @GetMapping("findApplicationList/{userId}/{page}/{count}")
    ReturnData findApplicationList(@PathVariable long userId, @PathVariable int page, @PathVariable int count);

}
