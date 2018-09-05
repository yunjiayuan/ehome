package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.BirdJournalService;
import com.busi.service.TaskService;
import com.busi.service.UserMembershipService;
import com.busi.utils.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * @program: 喂鸟
 * @author: ZHaoJiaJie
 * @create: 2018-09-04 14:25
 */
@RestController
public class BirdJournalController extends BaseController implements BirdJournalApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TaskService taskService;

    @Autowired
    MqUtils mqUtils;

    @Autowired
    UserMembershipService userMembershipService;

    @Autowired
    BirdJournalService birdJournalService;

    /***
     * 新增喂鸟记录
     * @param visitId
     * @return
     */
    @Override
    public ReturnData addBirdLog(@PathVariable long visitId) {
        //验证参数
        if (visitId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        //验证是不是自己
        if (myId == visitId) {//自己不做增加
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        Map<String, Object> memberMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP + myId);
        if (memberMap == null || memberMap.size() <= 0) {
            //缓存中没有用户对象 查询数据库
            UserMembership membership = userMembershipService.findUserMembership(myId);
            if (membership == null) {
                membership = new UserMembership();
                membership.setUserId(CommonUtils.getMyId());
            } else {
                //数据库中已有对应记录
                membership.setRedisStatus(1);
            }
            memberMap = CommonUtils.objectToMap(membership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP + CommonUtils.getMyId(), memberMap, Constants.USER_TIME_OUT);
        }
        int memberShipStatus = 0;
        int numLimit = Constants.FEEDBIRDTOTALCOUNT;
        if (memberMap.get("memberShipStatus") != null && !CommonUtils.checkFull(memberMap.get("memberShipStatus").toString())) {
            memberShipStatus = Integer.parseInt(memberMap.get("memberShipStatus").toString());
            if (memberShipStatus == 1) {//普通会员
                numLimit = 50;
            } else if (memberShipStatus > 1) {//高级以上
                numLimit = 10000;
            }
        }
        //当前日期
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int todaylastfeedbirddate = Integer.valueOf(format.format(new Date()));
        //计算当前时间 到 今天晚上12点的秒数差
        long second = CommonUtils.getCurrentTimeTo_12();

        //判断当前用户今日是否有喂鸟行为
        long my_coverTodaystimes = 0;
        long my_lastFeedBirdDate = 0;
        Map<String, Object> birdTodayMap = redisUtils.hmget(Constants.REDIS_KEY_BIRD_EXISTENCE + myId);
        if (birdTodayMap != null || birdTodayMap.size() > 0) {
            //获取缓存中今天是否喂过此鸟
            Map<String, Object> birdUserIdMap = redisUtils.hmget(Constants.REDIS_KEY_BIRD_FEEDING_TODAY + myId + "_" + visitId);
            if (birdUserIdMap != null || birdUserIdMap.size() > 0) {
                return returnData(StatusCode.CODE_BIRD_FEED_TREE.CODE_VALUE, "今日已喂过此鸟，明天再来吧！", new JSONObject());
            }
            //获取缓存中当前用户今日喂鸟次数(判断是否还有次数)
            my_coverTodaystimes = (long) redisUtils.hget(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + myId);
            if (my_coverTodaystimes >= numLimit) {
                return returnData(StatusCode.CODE_BIRD_FEED_FULL.CODE_VALUE, "今日喂鸟次数已用尽，明天再来吧！", new JSONObject());
            }
        }
        //更新当天喂鸟次数
        long my_todaycurFeedBird = redisUtils.hashIncr(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + myId, 1);//原子操作 递增1
        redisUtils.expire(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, second);//更新当天喂鸟次数的生命周期 到今天晚上12点失效
        //更新缓存（互动双方）
        BirdFeedingData myAttr = birdJournalService.findUserById(myId);
        Map<String, Object> myAttrMap = CommonUtils.objectToMap(myAttr);
        redisUtils.hmset(Constants.REDIS_KEY_BIRD_FEEDING_TODAY + myId + "_" + visitId, myAttrMap, second);
        //查询数据库
        if (myAttr != null) {
            // 玩家最后喂鹦鹉日 (判断是否同一天)
            my_lastFeedBirdDate = myAttr.getLastFeedBirdDate();
            if (todaylastfeedbirddate == my_lastFeedBirdDate) {
                //玩家喂鹦鹉数总次数
                long my_feedBirdTotalCount = myAttr.getFeedBirdTotalCount();
                //被喂者是否满足被喂条件
                BirdFeedingData userAttr = birdJournalService.findUserById(visitId);
                int user_eggState = 0;
                if (userAttr != null) {//被喂者鸟蛋记录
                    user_eggState = userAttr.getEggState();
                }
                //被喂玩家蛋状态
                if (user_eggState == 0) {//只有没蛋时才可喂
                    myAttr.setCurFeedBirdTimes(my_todaycurFeedBird);
                    //累计喂鹦鹉总次数++
                    myAttr.setFeedBirdTotalCount(my_feedBirdTotalCount + 1);
                    //记录玩家最后喂鹦鹉日
                    myAttr.setLastFeedBirdDate(todaylastfeedbirddate);
                    //记录玩家当天喂过的鹦鹉
                    if (myAttr.getFeedBirdIds() != null) {
                        myAttr.setFeedBirdIds(myAttr.getFeedBirdIds() + "," + visitId);
                    } else {
                        myAttr.setFeedBirdIds("," + visitId);
                    }
                    birdJournalService.updateMya(myAttr);
                } else if (user_eggState == 1) {
                    return returnData(StatusCode.CODE_BIRD_FEED_PRODUCING.CODE_VALUE, "产蛋中不能喂食！", new JSONObject());
                } else {
                    return returnData(StatusCode.CODE_BIRD_FEED_UNCLAIMED.CODE_VALUE, "未领取蛋不能喂食！", new JSONObject());
                }
            } else {
                myAttr.setCurFeedBirdTimes(my_todaycurFeedBird);
                //累计当天喂鹦鹉次数++
                myAttr.setFeedBirdTotalCount(1);
                //记录玩家最后喂鹦鹉日
                myAttr.setLastFeedBirdDate(todaylastfeedbirddate);
                //累计记录玩家当天向谁喂过鹦鹉
                myAttr.setFeedBirdIds("," + visitId);
                birdJournalService.updateMya(myAttr);
            }
        } else {
            myAttr = new BirdFeedingData();
            myAttr.setUserId(myId);
            myAttr.setCurFeedBirdTimes(my_todaycurFeedBird);
            //累计当天喂鹦鹉次数++
            myAttr.setFeedBirdTotalCount(1);
            //记录玩家最后喂鹦鹉日
            myAttr.setLastFeedBirdDate(todaylastfeedbirddate);
            //记录玩家当天向谁喂过鹦鹉
            myAttr.setFeedBirdIds("," + visitId);
        }
        BirdFeedingData userAttr = birdJournalService.findUserById(visitId);
        if (userAttr != null) {
            //玩家被喂鹦鹉次数
            long user_birdBeFeedTotalCount = userAttr.getBirdBeFeedTotalCount();
            //玩家蛋状态
            int user_eggState = userAttr.getEggState();
            //判断被喂者被喂次数与鸟蛋状态
            if (user_birdBeFeedTotalCount < Constants.FEEDBIRDFULL && user_eggState == 0) {
                //累计当天被喂鹦鹉次数
                userAttr.setBirdBeFeedTotalCount(user_birdBeFeedTotalCount + 1);
                //记录玩家最后被喂鹦鹉日
                userAttr.setBeenLastFeedBirdDate(todaylastfeedbirddate);
                //累计玩家被被喂鹦鹉总数
                userAttr.setBeenFeedBirdTotalCount(userAttr.getBeenFeedBirdTotalCount() + 1);
                birdJournalService.updateUsa(userAttr);

                //判断当前是否满足产蛋条件并设置产蛋时间
                if (userAttr.getBeenFeedBirdTotalCount() >= Constants.FEEDBIRDFULL && user_eggState == 0) {
                    userAttr.setStartLayingTime(new Date());
                    userAttr.setEggState(1);    //产蛋中状态
                    birdJournalService.updateUsb(userAttr);
                }
            } else {
                //判断蛋是否成熟
                long time = userAttr.getStartLayingTime().getTime();//产蛋时间
                long today = new Date().getTime();//当前时间
                long num = today - time;

                if (num >= Constants.EGGCOUNTDOWN) {
                    return returnData(StatusCode.CODE_BIRD_FEED_UNCLAIMED.CODE_VALUE, "未领取蛋不能喂食！", new JSONObject());
                }
                if (user_eggState == 1) {
                    return returnData(StatusCode.CODE_BIRD_FEED_PRODUCING.CODE_VALUE, "产蛋中不能喂食！", new JSONObject());
                }
                if (user_eggState == 2) {
                    return returnData(StatusCode.CODE_BIRD_FEED_UNCLAIMED.CODE_VALUE, "未领取蛋不能喂食！", new JSONObject());
                }
            }
        } else {
            //新增被喂者记录
            userAttr = new BirdFeedingData();
            userAttr.setUserId(visitId);
            //累计被喂鹦鹉次数++
            userAttr.setBirdBeFeedTotalCount(1);
            //记录玩家最后被喂鹦鹉日
            userAttr.setBeenLastFeedBirdDate(todaylastfeedbirddate);
            //累计玩家被被喂鹦鹉总数
            userAttr.setBeenFeedBirdTotalCount(1);
        }
        //双方均是第一次喂鸟
        if (myAttr == null && userAttr == null) {
            myAttr = new BirdFeedingData();
            myAttr.setUserId(myId);
            myAttr.setCurFeedBirdTimes(my_todaycurFeedBird);
            //累计喂鹦鹉总次数++
            myAttr.setFeedBirdTotalCount(1);
            //记录玩家最后喂出鹦鹉日
            myAttr.setLastFeedBirdDate(todaylastfeedbirddate);
            myAttr.setFeedBirdIds("," + myId);    //记录玩家当天向谁喂过鹦鹉

            userAttr = new BirdFeedingData();
            userAttr.setUserId(visitId);
            //累计被喂鹦鹉次数++
            userAttr.setBirdBeFeedTotalCount(1);
            //记录玩家最后被喂鹦鹉日
            userAttr.setBeenLastFeedBirdDate(todaylastfeedbirddate);
            //累计玩家被被喂鹦鹉总数
            userAttr.setBeenFeedBirdTotalCount(1);
        }
        //更新双方互动次数
        redisUtils.hashIncr(Constants.REDIS_KEY_BIRD_DYNAMIC_COUNT, myId + "_" + visitId, 1);//原子操作 递增1
        redisUtils.expire(Constants.REDIS_KEY_BIRD_DYNAMIC_COUNT, 0);//永不失效

        //新增
        if (myAttr.getId() <= 0) {
            birdJournalService.addData(myAttr);
        }
        if (userAttr.getId() <= 0) {
            birdJournalService.addData(userAttr);
        }
        if (birdTodayMap == null || birdTodayMap.size() <= 0) {
            //更新缓存（今日是否喂过鸟）
            Map<String, Object> todayMap = CommonUtils.objectToMap(myAttr);
            redisUtils.hmset(Constants.REDIS_KEY_BIRD_EXISTENCE + myId, todayMap, second);
        }
        return returnData(StatusCode.CODE_BIRD_FEED_TREE.CODE_VALUE, "今日已喂过此鸟，明天再来吧！", new JSONObject());
    }

    @Override
    public ReturnData delBird(long id, long userId) {
        return null;
    }

    @Override
    public ReturnData updateBird(@Valid BirdFeedingData birdFeedingData, BindingResult bindingResult) {
        return null;
    }

    @Override
    public ReturnData getBird(long id) {
        return null;
    }

    /***
     * 查询喂鸟记录
     * @param userId  用户ID
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findBirdList(long userId, int page, int count) {
        return null;
    }
}
