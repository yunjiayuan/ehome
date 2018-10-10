package com.busi.controller.api;


import com.busi.entity.Feedback;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 意见反馈相关接口实现
 * @author: ZHaoJiaJie
 * @create: 2018-10-9 14:48:12
 */
public interface FeedbackApiController {

    /***
     * 新增
     * @param feedback
     * @param bindingResult
     * @return
     */
    @PostMapping("addFeedback")
    ReturnData addFeedback(@Valid @RequestBody Feedback feedback, BindingResult bindingResult);

    /***
     * 查询记录
     * @param opinionType  意见类型  0：反馈  1：投诉
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @param sort // 类别    opinionType=0时为反馈类别：0.聊天相关1.访问串门相关2.房间功能相关3.头像设置及私房照上传4.个人资料编辑5.活动参加及投票6.发布公告相关7.发布家博相关8.求助发布相关9.钱包充值相关10.家币家点相关11.红包发送相关12.二手物品相关13.发布视频照片相关14.添加好友及通讯录15.串门送礼物相关16.串门喂鹦鹉砸蛋相关17.每日任务领取家点18.红包分享得红包相关19.家门口功能相关20.设置相关21.其他
     * 						  opinionType=1时为投诉类别：0、聊天互动类1、公共信息类2、家博信息类3、个人资料类4、活动、求助类5、其他类
     * @return
     */
    @GetMapping("findOpinionList/{opinionType}/{sort}/{page}/{count}")
    ReturnData findOpinionList(@PathVariable int opinionType, @PathVariable int sort, @PathVariable int page, @PathVariable int count);

}
