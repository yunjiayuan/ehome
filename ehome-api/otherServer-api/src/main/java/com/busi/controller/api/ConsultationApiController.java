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

    /***
     * 更新咨询状态
     * @param type   更新类型：1咨询中 2已咨询
     * @param occupation 职业：0医生  1律师
     * @param id   咨询记录ID
     * @return
     */
    @GetMapping("upConsultationStatus/{type}/{occupation}/{id}")
    ReturnData upConsultationStatus(@PathVariable int type, @PathVariable int occupation, @PathVariable String id);

    /***
     * 更新咨询时长
     * @param occupation 职业：0医生  1律师
     * @param id   咨询记录ID
     * @param duration   咨询时长
     * @return
     */
    @GetMapping("upActualDuration/{occupation}/{id}/{duration}")
    ReturnData upActualDuration(@PathVariable int occupation, @PathVariable String id, @PathVariable int duration);

    /***
     * 查询等待人员列表
     * @param occupation 职业：0医生  1律师
     * @param userId   医师或律师ID
     * @param page     页码
     * @param count    条数
     * @return
     */
    @GetMapping("findWaitList/{occupation}/{userId}/{page}/{count}")
    ReturnData findWaitList(@PathVariable int occupation, @PathVariable long userId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询等待咨询人数
     * @param occupation 职业：0医生  1律师
     * @param userId   医师或律师ID
     * @return
     */
    @GetMapping("findWaitNum/{occupation}/{userId}")
    ReturnData findWaitNum(@PathVariable int occupation, @PathVariable long userId);

}
