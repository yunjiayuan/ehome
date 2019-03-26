package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.CloudVideoService;
import com.busi.service.SelfChannelVipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
    CloudVideoService cloudVideoService;

    @Autowired
    SelfChannelVipService selfChannelVipService;

    /***
     * 新增订单
     * @param selfChannelVipOrder
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addGearShiftOrder(@Valid @RequestBody SelfChannelVipOrder selfChannelVipOrder, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        long time = new Date().getTime();
        String noTime = String.valueOf(time);
        String random = CommonUtils.getRandom(6, 1);
        String noRandom = CommonUtils.strToMD5(noTime + CommonUtils.getMyId() + random, 16);
        selfChannelVipOrder.setMoney(298);
        selfChannelVipOrder.setOrderNumber(noRandom);
        selfChannelVipOrder.setTime(new Date());
        selfChannelVipOrder.setUserId(CommonUtils.getMyId());
        //放入缓存 5分钟
        Map<String, Object> ordersMap = CommonUtils.objectToMap(selfChannelVipOrder);
        redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP_ORDER + CommonUtils.getMyId() + "_" + selfChannelVipOrder.getOrderNumber(), ordersMap, Constants.TIME_OUT_MINUTE_5);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询用户是否是自频道会员
     * @param userId
     * @return
     */
    @Override
    public ReturnData rightVip(@PathVariable long userId) {
        int state = 0; //0非会员  1会员
        //验证是否是自频道会员
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_SELFCHANNELVIP + userId);
        if (map == null || map.size() <= 0) {
            SelfChannelVip vip = selfChannelVipService.findDetails(userId);
            if (vip != null) {
                state = 1;
                //放入缓存
                Map<String, Object> ordersMap = CommonUtils.objectToMap(vip);
                redisUtils.hmset(Constants.REDIS_KEY_SELFCHANNELVIP + userId, ordersMap, Constants.USER_TIME_OUT);
            }
        } else {
            SelfChannelVip vip = (SelfChannelVip) CommonUtils.mapToObject(map, SelfChannelVip.class);
            if (vip != null) {
                if (vip.getExpiretTime().getTime() > new Date().getTime()) {
                    state = 1;
                }
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", state);
    }

    /***
     * 新增排挡
     * @param selfChannel
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addGearShift(@Valid @RequestBody SelfChannel selfChannel, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //只能在上午八点至十点之间排挡
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8); // 控制时
        calendar.set(Calendar.MINUTE, 0);       // 控制分
        calendar.set(Calendar.SECOND, 0);       // 控制秒
        long time = calendar.getTimeInMillis(); // 此处为今天的08：00：00
        long curren = 3600000;//一小时毫秒数
        long da = new Date().getTime();//当前时间毫秒数
        int seconds = 86400;//一天秒数
        int timeStamp = Integer.valueOf(simpleDateFormat.format(getNextDay(simpleDateFormat.format(new Date()))).substring(0, 8));//一天后的时间
        if (da >= time && da < time + curren * 2) {//8-10
            SelfChannelDuration selfChannelDuration = selfChannelVipService.findTimeStamp(timeStamp);
            // 查询活动信息
            CloudVideoActivities activities = cloudVideoService.findDetails(selfChannel.getUserId(), selfChannel.getSelectionType());
            if (activities == null) {
                return returnData(StatusCode.CODE_SELF_CHANNEL_VIP_JOIN_ACTIVITIES.CODE_VALUE, "您尚未参加过活动!", new JSONObject());
            }
            //计算视频秒数
            String[] array = activities.getDuration().split(":");//拆分
            int minute = Integer.parseInt(array[0]);//分
            int second = Integer.parseInt(array[1]);//秒
            int duration = minute * 60 + second;
            if (selfChannelDuration != null) {
                if (duration < selfChannelDuration.getSurplusTime()) {
                    Calendar calendar2 = new GregorianCalendar();
                    calendar2.setTime(new Date());
                    calendar2.add(calendar.DATE, 1);//把日期往后增加一天.整数往后推,负数往前移动
                    Date date = calendar2.getTime(); //日期往后推一天的时间
                    String dateString = simpleDateFormat.format(date);
                    int timeStamp2 = Integer.valueOf(dateString);
                    selfChannel.setTime(timeStamp2);
                    selfChannel.setCity(activities.getCity());
                    selfChannel.setDistrict(activities.getDistrict());
                    selfChannel.setDuration(activities.getDuration());
                    selfChannel.setProvince(activities.getProvince());
                    selfChannel.setSex(activities.getSex());
                    selfChannel.setSinger(activities.getSinger());
                    selfChannel.setSongName(activities.getSongName());
                    selfChannel.setBirthday(activities.getBirthday());
                    selfChannel.setVideoCover(activities.getVideoCover());
                    selfChannel.setVideoUrl(activities.getVideoUrl());
                    selfChannelVipService.addSelfChannel(selfChannel);

                    //更新时长表
                    selfChannelDuration.setSurplusTime(seconds - duration);//剩余秒数
                    selfChannelVipService.updateDuration(selfChannelDuration);
                }
            } else {
                //新增档期信息
                SelfChannelDuration selfChannelDuration1 = new SelfChannelDuration();
                selfChannelDuration1.setTimeStamp(timeStamp);//时间戳
                selfChannelDuration1.setSurplusTime(seconds - duration);//剩余秒数
                selfChannelVipService.addDuration(selfChannelDuration1);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询排挡视频列表
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findGearShiftList(@PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        PageBean<SelfChannel> pageBean = null;
        int timeStamp = 0;
        int timeStamp2 = 0;
        String time = simpleDateFormat.format(new Date());
        String time2 = simpleDateFormat.format(getNextDay(simpleDateFormat.format(new Date())));
        try {
            timeStamp = Integer.parseInt(time);//当前时间
            timeStamp2 = Integer.parseInt(time2);//一天后的时间
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        pageBean = selfChannelVipService.findGearShiftList(timeStamp, timeStamp2, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    //当前时间到第二天凌晨的时间
    public Date getNextDay(String dateTime) {
        SimpleDateFormat simpleDateFormat = new
                SimpleDateFormat("yyyyMMddHHmmss");
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = simpleDateFormat.parse(dateTime);
            calendar.setTime(date);
            calendar.add(Calendar.DATE, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return calendar.getTime();
    }
}
