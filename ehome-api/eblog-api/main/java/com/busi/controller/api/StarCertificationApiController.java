package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.StarCertification;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 生活圈标签相关接口
 * author：zhaojiajie
 * create time：2020-1-2 13:36:14
 */
public interface StarCertificationApiController {

    /***
     * 新增认证
     * @param starCertification
     * @return
     */
    @PostMapping("addCertification")
    ReturnData addCertification(@Valid @RequestBody StarCertification starCertification, BindingResult bindingResult);

    /**
     * @Description: 更新认证状态
     * @Param: starCertification
     * @return:
     */
    @PutMapping("updateCertification")
    ReturnData updateCertification(@Valid @RequestBody StarCertification starCertification, BindingResult bindingResult);

    /***
     * 查询认证详情
     * @param userId     被查询用户ID
     * @return
     */
    @GetMapping("findCertification/{userId}")
    ReturnData findCertification(@PathVariable long userId);
}
