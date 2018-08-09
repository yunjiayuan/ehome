package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 发起群相关接口
 * author：SunTianJie
 * create time：2018/8/1 11:04
 */
public interface InitiateGroupApiController {
    /***
     * 查询指定群成员的用户信息
     * @param userIds 将要查询的用户ID组合 格式123,456
     * @return
     */
    @GetMapping("findInitiateGroupMemberInfo/{userIds}")
    ReturnData findInitiateGroupMemberInfo(@PathVariable String userIds);
}
