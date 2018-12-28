package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.FollowCounts;
import com.busi.entity.ReturnData;
import com.busi.service.FollowCountsService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class FollowCountsLController extends BaseController implements FollowCountsLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    FollowCountsService followCountsService;

    /***
     * 更新粉丝数
     * @param followCounts
     * @return
     */
    @Override
    public ReturnData updateFollowCounts(@RequestBody FollowCounts followCounts) {
        FollowCounts fc = followCountsService.findFollowInfo(followCounts.getUserId());
        if(fc==null){//新增
            if(followCounts.getCounts()>0){// 正数为新增粉丝的粉丝数 负数为减少的粉丝数
                followCountsService.add(followCounts);
                //放入缓存
                redisUtils.set(Constants.REDIS_KEY_FOLLOW_COUNTS+followCounts.getUserId(),followCounts.getCounts()+"",Constants.USER_TIME_OUT);
            }
        }else{//更新
            if(fc.getCounts()<=0){//处理粉丝数为0时 不能再减的问题
                if(followCounts.getCounts()>0){
                    fc.setCounts(fc.getCounts()+followCounts.getCounts());
                }
            }else{
                fc.setCounts(fc.getCounts()+followCounts.getCounts());
            }
            followCountsService.update(fc);
            //放入缓存
            redisUtils.set(Constants.REDIS_KEY_FOLLOW_COUNTS+fc.getUserId(),fc.getCounts()+"",Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
