package com.busi.controller.api;

import com.busi.entity.HomeHospitalRecord;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 家医馆咨询相关接口
 * author：ZJJ
 * create time：2020-1-7 13:57:32
 */
public interface HomeHospitalRecordApiController {

    /***
     * 新增
     * @param homeHospital
     * @return
     */
    @PostMapping("addHRecord")
    ReturnData addHRecord(@Valid @RequestBody HomeHospitalRecord homeHospital, BindingResult bindingResult);

    /***
     * 更新
     * @param homeHospital
     * @return
     */
    @PutMapping("changeHRecord")
    ReturnData changeHRecord(@Valid @RequestBody HomeHospitalRecord homeHospital, BindingResult bindingResult);

    /***
     * 查询列表
     * @param haveDoctor  有无医嘱：0全部 1没有
     * @param identity   身份区分：0用户查 1医师查
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findHRecordList/{haveDoctor}/{identity}/{page}/{count}")
    ReturnData findHRecordList(@PathVariable int haveDoctor, @PathVariable int identity, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delHRecord/{id}")
    ReturnData delHRecord(@PathVariable long id);
}
