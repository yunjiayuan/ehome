package com.busi.controller.api;

import com.busi.entity.HomeHospital;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 家医馆相关接口 如：创建医馆 修改医馆信息 更改医馆状态等
 * author：ZJJ
 * create time：2020-1-7 13:29:26
 */
public interface HomeHospitalApiController {

    /***
     * 新增
     * @param homeHospital
     * @return
     */
    @PostMapping("addHospital")
    ReturnData addHospital(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult);

    /***
     * 更新
     * @param homeHospital
     * @return
     */
    @PutMapping("changeHospital")
    ReturnData changeHospital(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult);

    /***
     * 更新营业状态
     * @param homeHospital
     * @return
     */
    @PutMapping("updHospitalStatus")
    ReturnData updHospitalStatus(@Valid @RequestBody HomeHospital homeHospital, BindingResult bindingResult);

    /***
     * 查询详情
     * @param userId
     * @return
     */
    @GetMapping("findHospital/{userId}")
    ReturnData findHospital(@PathVariable long userId);

    /***
     * 查询列表
     * @param cityId     默认-1 百度地图中的城市ID，用于同城搜索
     * @param department      科室
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findHospitalList/{cityId}/{watchVideos}/{department}/{search}/{province}/{city}/{district}/{page}/{count}")
    ReturnData findHospitalList(@PathVariable int cityId, @PathVariable int watchVideos, @PathVariable int department, @PathVariable String search, @PathVariable int province, @PathVariable int city, @PathVariable int district, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delHospital/{userId}/{id}")
    ReturnData delHospital(@PathVariable long userId, @PathVariable long id);

}