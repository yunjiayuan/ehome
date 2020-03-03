package com.busi.controller.api;

import com.busi.entity.LawyerCircle;
import com.busi.entity.LawyerCircleRecord;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 律师圈相关接口 如：创建 修改信息 更改状态等
 * author：ZJJ
 * create time：2020-03-03 17:12:02
 */
public interface LawyerCircleApiController {
    /***
     * 新增律师
     * @param homeHospital
     * @return
     */
    @PostMapping("addLvshi")
    ReturnData addLvshi(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult);

    /***
     * 更新律师
     * @param homeHospital
     * @return
     */
    @PutMapping("changeLvshi")
    ReturnData changeLvshi(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult);

    /***
     * 更新律师营业状态
     * @param homeHospital
     * @return
     */
    @PutMapping("updLvshiStatus")
    ReturnData updLvshiStatus(@Valid @RequestBody LawyerCircle homeHospital, BindingResult bindingResult);

    /***
     * 查询律师详情
     * @param userId
     * @return
     */
    @GetMapping("findLvshi/{userId}")
    ReturnData findLvshi(@PathVariable long userId);

    /***
     * 查询律师列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param department  律师类型
     * @param search    模糊搜索（可以是：律所、律师类型、律师名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findLvshiList/{cityId}/{watchVideos}/{department}/{search}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findLvshiList(@PathVariable int cityId, @PathVariable int watchVideos, @PathVariable int department, @PathVariable String search, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除律师
     * @return:
     */
    @DeleteMapping("delLvshi/{userId}/{id}")
    ReturnData delLvshi(@PathVariable long userId, @PathVariable long id);

    /***
     * 新增咨询记录
     * @param homeHospital
     * @return
     */
    @PostMapping("addLRecord")
    ReturnData addLRecord(@Valid @RequestBody LawyerCircleRecord homeHospital, BindingResult bindingResult);

    /***
     * 更新咨询记录
     * @param homeHospital
     * @return
     */
    @PutMapping("changeLRecord")
    ReturnData changeLRecord(@Valid @RequestBody LawyerCircleRecord homeHospital, BindingResult bindingResult);

    /***
     * 查询咨询记录列表
     * @param haveDoctor  有无建议：0全部 1没有
     * @param identity   身份区分：0用户查 1律师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findLRecordList/{haveDoctor}/{identity}/{page}/{count}")
    ReturnData findLRecordList(@PathVariable int haveDoctor, @PathVariable int identity, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除咨询记录
     * @return:
     */
    @DeleteMapping("delLRecord/{id}")
    ReturnData delLRecord(@PathVariable long id);
}
