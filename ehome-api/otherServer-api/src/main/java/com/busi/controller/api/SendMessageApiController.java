package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 短信邀请抗议评选活动
 * author：suntj
 * create time：2020-2-21 21:10:32
 */
interface SendMessageApiController {

    /**
     * 短信邀请
     * @param phone      将要发送短信的手机
     * @param phoneType  类型 默认0 可扩展
     * @param param      自定义参数
     * @return
     */
    @GetMapping("sendMessageByEpidemicSituation/{phone}/{phoneType}/{param}")
    ReturnData sendMessageByEpidemicSituation(@PathVariable String phone,@PathVariable int phoneType,@PathVariable String param);

}
