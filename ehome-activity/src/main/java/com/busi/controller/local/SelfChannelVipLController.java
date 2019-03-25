package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.SelfChannelVip;
import com.busi.service.SelfChannelVipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * @program: ehome
 * @description: 自频道会员（内部调用）
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 13:53
 */
public class SelfChannelVipLController extends BaseController implements SelfChannelVipLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private SelfChannelVipService selfChannelVipService;

    /***
     * 新增会员信息
     * @param selfChannelVip
     * @return
     */
    @Override
    public ReturnData addSelfMember(@RequestBody SelfChannelVip selfChannelVip) {
        Calendar rightNow = Calendar.getInstance();
        rightNow.setTime(selfChannelVip.getStartTime());
        rightNow.add(Calendar.YEAR, 1);// 日期加1年
        Date dt = rightNow.getTime();
        selfChannelVip.setExpiretTime(dt);
        int count = selfChannelVipService.add(selfChannelVip);
        if (count <= 0) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "新增会员信息失败", new JSONObject());
        }
        //放入缓存
        Map<String, Object> map = CommonUtils.objectToMap(selfChannelVip);
        redisUtils.hmset(Constants.REDIS_KEY_KITCHEN + selfChannelVip.getUserId(), map, Constants.TIME_OUT_MINUTE_60_24_1 * 365);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

}