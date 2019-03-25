package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.SelfChannelVip;
import com.busi.service.SelfChannelVipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * @program: ehome
 * @description: 自频道会员
 * @author: ZHaoJiaJie
 * @create: 2019-03-22 14:44
 */
@RestController
public class SelfChannelVipController extends BaseController implements SelfChannelVipApiController {


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    SelfChannelVipService selfChannelVipService;

    /***
     * 查询用户是否是自频道会员
     * @param userId
     * @return
     */
    @Override
    public ReturnData rightVip(@PathVariable long userId) {
        int state = 1; //0非会员  1会员
        //验证是否是自频道会员
        //查询缓存 缓存中不存在 查询数据库(缓存中存在必定是会员)
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_SELFCHANNELVIP + userId);
        if (map == null || map.size() <= 0) {
            SelfChannelVip vip = selfChannelVipService.findDetails(userId);
            if (vip == null) {
                state = 0;
            } else {
                //放入缓存
                long time = vip.getExpiretTime().getTime() - new Date().getTime();
                Map<String, Object> ordersMap = CommonUtils.objectToMap(vip);
                redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP + userId, ordersMap, time);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", state);
    }
}
