package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.BirdJournalService;
import com.busi.service.TaskService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.*;

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
    BirdJournalService birdJournalService;

    @Autowired
    UserInfoUtils userInfoUtils;

    @Autowired
    UserMembershipUtils userMembershipUtils;

    /***
     * 新增喂鸟记录
     * @param userId
     * @return
     */
    @Override
    public ReturnData addBirdLog(@PathVariable long userId) {
        //验证参数
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        //验证是不是自己
        if (myId == userId) {//自己不做增加
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "抱歉自己不可以喂食自己的鹦鹉！", new JSONObject());
        }
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(myId);
        int memberShipStatus = 0;
        int numLimit = Constants.FEEDBIRDTOTALCOUNT;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 50;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 10000;
        }
        //当前日期
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int todaylastfeedbirddate = Integer.valueOf(format.format(new Date()));
        //计算当前时间 到 今天晚上12点的秒数差
        long second = CommonUtils.getCurrentTimeTo_12();

        long my_lastFeedBirdDate = 0;
        //获取缓存中今天是否喂过此鸟
        Map<String, Object> birdUserIdMap = redisUtils.hmget(Constants.REDIS_KEY_BIRD_FEEDING_TODAY + myId + "_" + userId);
        if (birdUserIdMap != null && birdUserIdMap.size() > 0) {
            return returnData(StatusCode.CODE_BIRD_FEED_TREE.CODE_VALUE, "今日已喂过此鸟，明天再来吧！", new JSONObject());
        }
        //获取缓存中当前用户今日喂鸟次数(判断是否还有次数)
        Object object = new Object();
        long my_coverTodaystimes = 0;
        object = redisUtils.hget(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + myId);
        if (object != null) {
            my_coverTodaystimes = Long.valueOf(String.valueOf(object));
            if (my_coverTodaystimes >= numLimit) {
                return returnData(StatusCode.CODE_BIRD_FEED_FULL.CODE_VALUE, "今日喂鸟次数已用尽，明天再来吧！", new JSONObject());
            }
        }
        //更新当天喂鸟次数
        long my_todaycurFeedBird = redisUtils.hashIncr(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + myId, 1);//原子操作 递增1
        redisUtils.expire(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, second);//更新当天喂鸟次数的生命周期 到今天晚上12点失效
        //更新缓存（互动双方）
        BirdFeedingData myAttr = birdJournalService.findUserById(myId);
        if (myAttr == null) {//防止空的时候缓存不会新增
            myAttr = new BirdFeedingData();
            myAttr.setUserId(myId);
        }
        Map<String, Object> myAttrMap = CommonUtils.objectToMap(myAttr);
        redisUtils.hmset(Constants.REDIS_KEY_BIRD_FEEDING_TODAY + myId + "_" + userId, myAttrMap, second);
        //查询数据库
        if (myAttr != null) {
            // 玩家最后喂鹦鹉日 (判断是否同一天)
            my_lastFeedBirdDate = myAttr.getLastFeedBirdDate();
            if (todaylastfeedbirddate == my_lastFeedBirdDate) {
                //玩家喂鹦鹉数总次数
                long my_feedBirdTotalCount = myAttr.getFeedBirdTotalCount();
                //被喂者是否满足被喂条件
                BirdFeedingData userAttr = birdJournalService.findUserById(userId);
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
                        myAttr.setFeedBirdIds(myAttr.getFeedBirdIds() + "," + userId);
                    } else {
                        myAttr.setFeedBirdIds("," + userId);
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
                myAttr.setFeedBirdIds("," + userId);
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
            myAttr.setFeedBirdIds("," + userId);
        }
        BirdFeedingData userAttr = birdJournalService.findUserById(userId);
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
            userAttr.setUserId(userId);
            //累计被喂鹦鹉次数++
            userAttr.setBirdBeFeedTotalCount(1);
            //记录玩家最后被喂鹦鹉日
            userAttr.setBeenLastFeedBirdDate(todaylastfeedbirddate);
            //累计玩家被被喂鹦鹉总数
            userAttr.setBeenFeedBirdTotalCount(1);
        }
        //更新双方互动次数
        BirdInteraction birdInteraction = birdJournalService.findInterac(myId, userId);
        if (birdInteraction != null) {
            birdInteraction.setFeedBirdTotalCount(birdInteraction.getFeedBirdTotalCount() + 1);
            birdJournalService.updateMyb(birdInteraction);
        } else {
            birdInteraction = new BirdInteraction();
            birdInteraction.setUserId(myId);
            birdInteraction.setVisitId(userId);
            birdInteraction.setFeedBirdTotalCount(1);
            birdJournalService.addInteraction(birdInteraction);
        }
        //新增喂鸟历史记录
        BirdFeedingRecord birdFeed = new BirdFeedingRecord();
        birdFeed.setUserId(myId);
        birdFeed.setVisitId(userId);
        birdFeed.setTime(new Date());
        birdJournalService.addJourna(birdFeed);

        //新增
        if (myAttr.getId() <= 0) {
            birdJournalService.addData(myAttr);
        }
        if (userAttr.getId() <= 0) {
            birdJournalService.addData(userAttr);
        }
        //更新任务系统
        mqUtils.sendTaskMQ(myId, 1, 7);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除喂鸟记录
     * @param id
     * @return
     */
    @Override
    public ReturnData delBirdRecord(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "id参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的喂鸟记录信息", new JSONObject());
        }
        //查询数据库
        int look = birdJournalService.del(id);
        if (look <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "喂鸟记录[" + id + "]不存在", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询剩余次数与今日是否喂过此鸟
     * @param userId
     * @return
     */
    @Override
    public ReturnData getRemainder(@PathVariable long userId) {

        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(CommonUtils.getMyId());
        int memberShipStatus = 0;
        int numLimit = Constants.FEEDBIRDTOTALCOUNT;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 50;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 10000;
        }
        int is = 0;// 0未喂过 1已喂过
        Map<String, Integer> isMap = new HashMap<>();
        Map<String, Object> birdUserIdMap = redisUtils.hmget(Constants.REDIS_KEY_BIRD_FEEDING_TODAY + CommonUtils.getMyId() + "_" + userId);
        if (birdUserIdMap != null && birdUserIdMap.size() > 0) {
            is = 1;//1已喂过
            isMap.put("is", is);
            return returnData(StatusCode.CODE_BIRD_FEED_TREE.CODE_VALUE, "今日已喂过此鸟，明天再来吧！", isMap);
        }
        //获取缓存中当前用户今日喂鸟次数(判断是否还有次数)
        long number = 0;
        Object object = new Object();
        long my_coverTodaystimes = 0;
        object = redisUtils.hget(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + CommonUtils.getMyId());
        if (object != null) {
            my_coverTodaystimes = Long.valueOf(String.valueOf(object));
            if (my_coverTodaystimes >= numLimit) {
                return returnData(StatusCode.CODE_BIRD_FEED_FULL.CODE_VALUE, "今日喂鸟次数已用尽，明天再来吧！", new JSONObject());
            } else {
                number = numLimit - my_coverTodaystimes;
            }
        } else {
            number = numLimit;
        }
        isMap.put("is", is);
        isMap.put("num", (int) number);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "查询剩余次数与今日是否喂过此鸟成功！", isMap);
    }

    /***
     * 查询喂鸟记录
     * @param userId  用户ID
     * @param state 0喂我的  1我喂的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findBirdList(@PathVariable long userId, @PathVariable int state, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //开始查询
        PageBean<BirdFeedingRecord> pageBean;
        pageBean = birdJournalService.findList(userId, state, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        String users = "";
        List listInterac = null;
        List list = pageBean.getList();
        BirdFeedingRecord birdfeed = null;
        Map<Long, Long> dyMap = new HashMap<Long, Long>();
        for (int i = 0; i < list.size(); i++) {
            birdfeed = (BirdFeedingRecord) list.get(i);
            if (birdfeed != null) {
                //state 0喂我的  1我喂的
                if (state == 0) {
                    users += i == list.size() - 1 ? birdfeed.getUserId() : birdfeed.getUserId() + ",";
                } else {
                    users += i == list.size() - 1 ? birdfeed.getVisitId() : birdfeed.getVisitId() + ",";
                }
            }
        }
        listInterac = birdJournalService.findUserList(userId, users.split(","), state);
        BirdInteraction udc = null;
        if (listInterac.size() > 0 && listInterac != null) {
            for (int i = 0; i < listInterac.size(); i++) {
                udc = (BirdInteraction) listInterac.get(i);
                if (state == 0) {
                    dyMap.put(udc.getUserId(), udc.getFeedBirdTotalCount());
                } else {
                    dyMap.put(udc.getVisitId(), udc.getFeedBirdTotalCount());
                }
            }
        }
        for (int j = 0; j < list.size(); j++) {
            birdfeed = (BirdFeedingRecord) list.get(j);
            UserInfo userInfo = null;
            //state0 谁向我 1我向谁
            if (state == 0) {
                userInfo = userInfoUtils.getUserInfo(birdfeed.getUserId());
                birdfeed.setFeedBirdTotalCount(dyMap.get(birdfeed.getUserId()) == null ? 0 : dyMap.get(birdfeed.getUserId()));
            } else {
                userInfo = userInfoUtils.getUserInfo(birdfeed.getVisitId());
                birdfeed.setFeedBirdTotalCount(dyMap.get(birdfeed.getVisitId()) == null ? 0 : dyMap.get(birdfeed.getVisitId()));
            }
            if (userInfo != null) {
                birdfeed.setUserHead(userInfo.getHead());    //获取头像
                birdfeed.setUserName(userInfo.getName());    //获取 名称
                birdfeed.setBirthday(userInfo.getBirthday());    //生日 计算年龄
                birdfeed.setSex(userInfo.getSex());//性别
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    /***
     * 查询砸蛋记录
     * @param userId
     * @return
     */
    @Override
    public ReturnData findSmashList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //开始查询
        PageBean<BirdEggSmash> pageBean;
        pageBean = birdJournalService.findEggList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        UserInfo userInfo = null;
        List list = pageBean.getList();
        for (int j = 0; j < list.size(); j++) {
            BirdEggSmash birdfeed = null;
            birdfeed = (BirdEggSmash) list.get(j);
            userInfo = userInfoUtils.getUserInfo(birdfeed.getUserId());
            if (userInfo != null) {
                birdfeed.setUserHead(userInfo.getHead());    //获取头像
                birdfeed.setUserName(userInfo.getName());    //获取名称
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, list);
    }

    /***
     * 查询鸟蛋状态
     * @param userId
     * @return
     */
    @Override
    public ReturnData findBirdEgg(@PathVariable long userId) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //查询数据库
        int eggState = 0;
        String time = null;
        Date startLayingTime = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        BirdFeedingData egg = birdJournalService.findUserById(userId);
        if (egg != null) {
            eggState = egg.getEggState();
            startLayingTime = egg.getStartLayingTime();
            //判断蛋 倒计时
            if (eggState == 1) {//0 没蛋  1产蛋中 2已产
                long _time = startLayingTime.getTime();
                long _today = new Date().getTime();
                long num = _today - _time;
                if (num >= Constants.EGGCOUNTDOWN) {
                    egg.setEggState(2);
                    eggState = 2;
                    egg.setLayingTotalCount(egg.getLayingTotalCount() + 1);    //产蛋总量+1
                    birdJournalService.updateUsc(egg);
                }
            }
            if (egg.getStartLayingTime() != null) {
                time = format.format(egg.getStartLayingTime());
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("eggState", String.valueOf(eggState));
        map.put("startLayingTime", time);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 砸鸟蛋
     * @param userId  被砸者
     * @param hitEggType 砸蛋类型   1金蛋2 银蛋
     * @param issue  期号
     * @return
     */
    @Override
    public ReturnData hitEgg(@PathVariable long userId, @PathVariable int hitEggType, @PathVariable int issue) {
        //验证参数
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        //验证参数
        if (hitEggType < 1 || hitEggType > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "hitEggType参数有误", new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        //获取会员等级 根据用户会员等级 获取砸蛋次数
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(myId);
        int memberShipStatus = 0;
        int numLimit = Constants.FEEDBIRDTOTALCOUNT;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        int[] array = new int[10];
        array = new int[]{0, 1, 0, 1, 0, 1, 0, 1, 2, 2};//普通人
        if (memberShipStatus == 1) {//普通会员
            numLimit = 50;
            array = new int[]{0, 1, 0, 1, 0, 1, 2, 1, 2, 2};//普通会员
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 10000;
            array = new int[]{0, 2, 1, 2, 1, 2, 1, 2, 1, 2};//高级会员
        }
        //获取缓存中当前用户今日喂鸟次数(判断是否还有次数)
        Object object = new Object();
        long my_coverTodaystimes = 0;
        object = redisUtils.hget(Constants.REDIS_KEY_BIRD_FEEDING_TOTAL_COUNT, "today_" + myId);
        if (object != null) {
            my_coverTodaystimes = Long.valueOf(String.valueOf(object));
            if (my_coverTodaystimes > numLimit) {
                return returnData(StatusCode.CODE_BIRD_FEED_FULL.CODE_VALUE, "今日喂鸟次数已用尽，明天再来吧！", new JSONObject());
            }
        }
        if (hitEggType == 2) {//砸银蛋    生成奖品
            int redNum = 0;
            Random rand = new Random();
            int awardsId = 0;//奖品ID,0悲催蛋  1艳遇蛋  2家点红包
            awardsId = rand.nextInt(9);
            if (array[awardsId] == 2) {//红包随机
                redNum = rand.nextInt(50) + 1;
                //更新钱包余额和钱包明细
                mqUtils.sendPurseMQ(myId, 4, 2, redNum);
            }
            //添加 砸蛋记录
            BirdEggSmash sEgg = new BirdEggSmash();
            sEgg.setMyId(myId);
            sEgg.setUserId(userId);
            sEgg.setTime(new Date());
            sEgg.setEggType(2);//银蛋
            birdJournalService.addEgg(sEgg);
            //添加中奖记录
            BirdTheWinners theWinners = new BirdTheWinners();
            theWinners.setUserId(myId);
            theWinners.setGrade(array[awardsId]);
            theWinners.setEggType(2);
            theWinners.setTime(new Date());
            theWinners.setCost(redNum);
            theWinners.setIssue(issue);
            birdJournalService.addWinners(theWinners);

            Map<String, Integer> map = new HashMap<>();
            map.put("awardsId", array[awardsId]);
            map.put("cost", redNum);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
        } else {//砸金蛋
            BirdFeedingData egg = null;
            egg = birdJournalService.findUserById(userId);
            if (egg != null) {
                if (egg.getEggState() != 2) {
                    return returnData(StatusCode.CODE_BIRD_FEED_ROB.CODE_VALUE, "砸蛋失败！金蛋不存在或者已被别人取走！", new JSONObject());
                }
                //开始砸蛋
                egg.setEggState(0);// 领后蛋设为空
                egg.setStartLayingTime(null); //上次产蛋时间清空
                egg.setBirdBeFeedTotalCount(0); //清空今日自家鸟被喂次数
                egg.setBeenFeedBirdTotalCount(0); //清空自家鸟被喂总次数
                birdJournalService.updateUserEgg(egg);
                //生成奖品 目前暂定金蛋 只能砸出红包以上奖品
                int awardsId = 0;//奖品ID,0悲催蛋  1艳遇蛋  2家点红包3家币红包4现金红包
                int redNum = 0;//奖品具体数值，当为红包时有效
                int currencyType = 0;//交易支付类型 0钱(真实人民币),1家币,2家点
                Random rand = new Random();
                awardsId = rand.nextInt(3) + 2;//2——4之间随机
                if (awardsId == 2) {//家点红包
                    currencyType = 2;
                    redNum = rand.nextInt(50) + 51;
                } else if (awardsId == 3) {//家币红包
                    Random r = new Random();
                    int romAwardsId = r.nextInt(10) + 1;
                    if (romAwardsId % 2 == 0) {//偶数返家币
                        redNum = r.nextInt(3) + 1;
                        currencyType = 1;
                    } else {
                        redNum = rand.nextInt(50) + 51;
                        currencyType = 2;
                        awardsId = 2;//家点
                    }
                } else {//现金红包
                    Random r = new Random();
                    int romAwardsId = r.nextInt(10) + 1;
                    if (romAwardsId % 2 == 0) {//偶数返家币
                        romAwardsId = r.nextInt(10) + 1;
                        if (romAwardsId % 2 != 0) {//奇数返现金
                            currencyType = 0;
                            redNum = rand.nextInt(300) + 1;
                            double spareMoney = redNum / 100.0;
                        } else {
                            redNum = rand.nextInt(50) + 51;
                            currencyType = 2;
                            awardsId = 2;
                        }
                    } else {
                        redNum = rand.nextInt(50) + 51;
                        currencyType = 2;
                        awardsId = 2;
                    }
                }
                double spareMoney = 0.0;
                if (currencyType == 0) {
                    spareMoney = redNum / 100.0;
                    //更新钱包余额和钱包明细
                    mqUtils.sendPurseMQ(myId, 4, currencyType, spareMoney);
                } else {
                    //更新钱包余额和钱包明细
                    mqUtils.sendPurseMQ(myId, 4, currencyType, redNum);
                }
                //添加 领蛋记录
                BirdEggSmash sEgg = new BirdEggSmash();
                sEgg.setMyId(myId);
                sEgg.setUserId(userId);
                sEgg.setTime(new Date());
                sEgg.setEggType(1);//金蛋
                birdJournalService.addEgg(sEgg);
                //添加中奖记录
                BirdTheWinners theWinners = new BirdTheWinners();
                theWinners.setUserId(myId);
                theWinners.setGrade(awardsId);
                theWinners.setEggType(1);
                theWinners.setTime(new Date());
                theWinners.setIssue(issue);
                if (currencyType == 0) {
                    theWinners.setCost(spareMoney);
                } else {
                    theWinners.setCost(redNum);
                }
                birdJournalService.addWinners(theWinners);
                Map<String, Integer> map = new HashMap<>();
                map.put("awardsId", awardsId);
                map.put("cost", redNum);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询最新一期奖品
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @return
     */
    @Override
    public ReturnData findNewPrize(@PathVariable int eggType) {
        //验证参数
        if (eggType < 0 || eggType > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "eggType参数有误", new JSONObject());
        }
        List<BirdPrize> list = null;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int time = Integer.valueOf(format.format(new Date()));//当前系统时间(Date转int)
        list = birdJournalService.findNewList(eggType, time);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", list);
    }

    /***
     * 分页查询自己奖品
     * @param userId  用户ID
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findOwnList(@PathVariable long userId, @PathVariable int eggType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        if (eggType < 0 || eggType > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "eggType参数有误", new JSONObject());
        }
        //开始查询
        PageBean<BirdTheWinners> pageBean;
        pageBean = birdJournalService.findWinnersList(userId, eggType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

    /***
     * 分页查询中奖名单
     * @param eggType 蛋类型 0不限 1金蛋2 银蛋
     * @param issue 期号
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findWinningList(@PathVariable int eggType, @PathVariable int issue, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (issue <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "issue参数有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (eggType < 0 || eggType > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "eggType参数有误", new JSONObject());
        }
        List<BirdPrize> list = null;
        List<BirdTheWinners> listWinners = null;
        //开始查询
        list = birdJournalService.findAppointList(eggType, issue);
        PageBean<BirdTheWinners> pageBean;
        pageBean = birdJournalService.findPrizeList(eggType, issue, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        UserInfo userInfo = null;
        listWinners = pageBean.getList();
        if (listWinners != null && listWinners.size() > 0) {
            for (int j = 0; j < listWinners.size(); j++) {
                BirdTheWinners birdfeed = null;
                birdfeed = (BirdTheWinners) listWinners.get(j);
                userInfo = userInfoUtils.getUserInfo(birdfeed.getUserId());
                if (userInfo != null) {
                    birdfeed.setHead(userInfo.getHead());    //获取头像
                    birdfeed.setName(userInfo.getName());    //获取名称
                    birdfeed.setProTypeId(userInfo.getProType());  //	省简称ID
                    birdfeed.setHouseNumber(userInfo.getHouseNumber());  // 门牌号
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("prizeData", list);
        map.put("data", listWinners);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
