package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserMembership;
import com.busi.service.UserMembershipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

/**
 * 随便走走 各地串串接口
 * author：SunTianJie
 * create time：2018/7/25 19:01
 */
@RestController
public class WalkLimitController extends BaseController implements WalkLimitApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserMembershipService userMembershipService;

    /***
     * 获取随便走走 各地串串的 目标用户ID
     * @return
     */
    @Override
    public ReturnData walk() {
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        int numLimit =  Constants.WALK_LIMIT_COUNT_USER;
        Map<String,Object> memberMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+CommonUtils.getMyId() );
        if(memberMap==null||memberMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            UserMembership userMembership = userMembershipService.findUserMembership(CommonUtils.getMyId());
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(CommonUtils.getMyId());
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            memberMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+CommonUtils.getMyId(),memberMap,Constants.USER_TIME_OUT);
        }
        int memberShipStatus = 0;
        if(memberMap.get("memberShipStatus")!=null&&!CommonUtils.checkFull(memberMap.get("memberShipStatus").toString())){
            memberShipStatus = Integer.parseInt(memberMap.get("memberShipStatus").toString());
            if(memberShipStatus==1){//普通会员
                numLimit = Constants.WALK_LIMIT_COUNT_MEMBER;
            }else if(memberShipStatus>1){//高级以上
                numLimit = Constants.WALK_LIMIT_COUNT_SENIOR_MEMBER;
            }
        }
        //计算当前时间 到 今天晚上12点的秒数差
        long second = CommonUtils.getCurrentTimeTo_12();
        //和缓存中的记录比较是否到达上线
        Object obj = redisUtils.hget(Constants.REDIS_KEY_USER_WALK_LIMIT,CommonUtils.getMyId()+"");
        if(obj==null||CommonUtils.checkFull(obj.toString())){//今天第一次访问
            redisUtils.hset(Constants.REDIS_KEY_USER_WALK_LIMIT,CommonUtils.getMyId()+"",1,second);
        }else{//已有记录 比较是否达到上限
            int serverCount = Integer.parseInt(obj.toString());
            if(serverCount>=numLimit){
                //此处需要判断会员级别
                if(memberShipStatus==0){//普通用户
                    return returnData(StatusCode.CODE_WALK_FEED_FULL.CODE_VALUE,"很抱歉，您今天的随便走走和各地串串次数已用尽,成为会员可获取更多次数!",new JSONObject());
                }else if(memberShipStatus==1){//普通会员
                    return returnData(StatusCode.CODE_MEMBER_WALK_FEED_FULL.CODE_VALUE,"很抱歉，您今天的随便走走和各地串串次数已用尽,成为高级会员可获得无限次数!",new JSONObject());
                }else{
                    return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"很抱歉，您今天的随便走走和各地串串次数已用尽,明天再来试试吧!",new JSONObject());
                }
            }
            serverCount++;
            redisUtils.hset(Constants.REDIS_KEY_USER_WALK_LIMIT,CommonUtils.getMyId()+"",serverCount,second);
        }
        //开始随机用户
        Map<String,Object> map = new HashMap<>();
        long userId = 0;
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_HOUSENUMBER);
        if(userMap==null||userMap.size()<=0){
            //容错处理 17为测试账号  如果缓存中一个用户也没有  随机机器人13870-53870
            Random random = new Random();
            userId = random.nextInt(40000)+13870;
        }
        int count = new Random().nextInt(userMap.size()) + 1;
        int i=1;
        for(String key:userMap.keySet()){
            if(i==count){
                Object object = userMap.get(key);//随机用户
                userId = Long.parseLong(object.toString());
                break;
            }
            i++;
        }
        map.put("userId",userId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
