package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.LoginStatusInfoControllerFegin;
import com.busi.Feign.VisitViewControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.LoginStatusInfo;
import com.busi.entity.VisitView;
import com.busi.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 同步用户访问量信息
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class VisitViewService implements MessageAdapter {

    @Autowired
    private VisitViewControllerFegin visitViewControllerFegin;

    /***
     * fegin 调用login服务中的 新增用户登录信息接口
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            long todayVisitCount = Long.parseLong(body.getString("todayVisitCount"));
            long totalVisitCount = Long.parseLong(body.getString("totalVisitCount"));
            VisitView visitView = new VisitView();
            visitView.setUserId(userId);
            visitView.setTodayVisitCount(todayVisitCount);
            visitView.setTotalVisitCount(totalVisitCount);
            visitViewControllerFegin.updateLocalVisit(visitView);//fegin调用更新操作
            log.info("消息服务平台处理用户新增访问量时，同步访问量信息记录表操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户新增访问量时，同步访问量信息记录表操作失败，参数有误："+body.toJSONString());
        }
    }
}
