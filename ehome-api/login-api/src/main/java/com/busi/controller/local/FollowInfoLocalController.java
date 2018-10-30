package com.busi.controller.local;


import com.busi.entity.FollowInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 查询关注信息接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface FollowInfoLocalController {

    /***
     * 查询指定用户的关注列表
     * @param userId
     * @return
     */
    @GetMapping("getFollowInfo/{userId}")
    String getFollowInfo(@PathVariable(value = "userId") long userId);

}
