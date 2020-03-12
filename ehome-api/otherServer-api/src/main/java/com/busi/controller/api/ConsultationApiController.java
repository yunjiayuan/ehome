package com.busi.controller.api;

import com.busi.entity.ConsultationOrders;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 律师医生咨询相关
 * author：ZJJ
 * create time：2020-03-12 14:48:35
 */
public interface ConsultationApiController {

    /***
     * 新增订单
     * @param consultationOrders
     * @param bindingResult
     * @return
     */
    @PostMapping("addConsultOrder")
    ReturnData addConsultOrder(@Valid @RequestBody ConsultationOrders consultationOrders, BindingResult bindingResult);

    /***
     * 查询收费信息
     * @param occupation 职业：0医生  1律师
     * @param userId   咨询对象ID
     * @param type     咨询类型：0语音、视频  1图文
     * @return
     */
    @GetMapping("findConsultList/{occupation}/{type}/{userId}")
    ReturnData findConsultList(@PathVariable int occupation, @PathVariable int type, @PathVariable long userId);
}
