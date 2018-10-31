package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.FollowCountsControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.FollowCounts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 粉丝统计service
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class FollowCountsService implements MessageAdapter {

    @Autowired
    private FollowCountsControllerFegin followCountsControllerFegin;

    /***
     * fegin 调用login服务中的 更新用户粉丝数接口
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            int followCounts = Integer.parseInt(body.getString("followCounts"));
            FollowCounts fc = new FollowCounts();
            fc.setUserId(userId);
            fc.setCounts(followCounts);
            followCountsControllerFegin.updateFollowCounts(fc);//fegin调用更新操作
            log.info("消息服务平台处理用户更新粉丝数操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户更新粉丝数操作失败，参数有误："+body.toJSONString());
        }
    }
}
