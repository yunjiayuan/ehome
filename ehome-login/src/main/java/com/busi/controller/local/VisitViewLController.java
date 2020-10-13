package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.Footprint;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.service.FootprintService;
import com.busi.service.VisitViewService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * 更新访问量信息到数据 为MQ提供调用接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class VisitViewLController extends BaseController implements VisitViewLocalController {

    @Autowired
    VisitViewService visitViewService;

    @Autowired
    FootprintService footprintService;

    @Autowired
    RedisUtils redisUtils;

    /***
     * 更新访问量信息和脚印记录
     * @return
     */
    @Override
    public ReturnData updateLocalVisit(@RequestBody VisitView visitView) {
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId());
        VisitView v = null;
        int count = 0;
        v = visitViewService.findVisitView(visitView.getUserId());
        if(v==null){
            v = new VisitView();
            v.setTotalVisitCount(1);
            v.setTodayVisitCount(1);
            v.setMyId(visitView.getMyId());
            v.setUserId(visitView.getUserId());
            count = visitViewService.add(v);
        }else{
            v.setTodayVisitCount(v.getTodayVisitCount()+1);
            v.setTotalVisitCount(v.getTotalVisitCount()+1);
            count = visitViewService.update(v);
        }
        //更新缓存
        redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(v),CommonUtils.getCurrentTimeTo_12());//今日访问量的生命周期 到今天晚上12点失效
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新用户["+visitView.getUserId()+"]访问量记录到数据库失败",new JSONObject());
        }
        //新增脚印记录
        Footprint footprint = new Footprint();
        footprint.setMyId(visitView.getMyId());
        footprint.setUserId(visitView.getUserId());
        footprint.setTime(new Date());
        footprintService.add(footprint);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 随机新增访问量，每日给用户新增一些访问量（用于服务间调用）
     * @param visitView
     * @return
     */
    @Override
    public ReturnData updateLocalRandomVisit(@RequestBody VisitView visitView) {
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId());
        VisitView v = null;
        int count = 0;
        v = visitViewService.findVisitView(visitView.getUserId());
        Random random = new Random();
        int r = 0;//随机数
        if(v==null){
            //无访问量时 随机1-10
            r = random.nextInt(10)+1;
            v = new VisitView();
            v.setTotalVisitCount(r);
            v.setTodayVisitCount(r);
            v.setMyId(visitView.getMyId());
            v.setUserId(visitView.getUserId());
            count = visitViewService.add(v);
        }else{
            //已有访问量时 判断目前访问量处于什么级别 并根据级别进行新增
            if(v.getTotalVisitCount()<=15){
                r = random.nextInt(10)+5;
            }else if(v.getTotalVisitCount()>15&&v.getTotalVisitCount()<=100){
                r = random.nextInt(10)+1;
            }else if(v.getTotalVisitCount()>100&&v.getTotalVisitCount()<=150){
                r = random.nextInt(10)+10;
            }else if(v.getTotalVisitCount()>150&&v.getTotalVisitCount()<=200){
                r = random.nextInt(10)+1;
            }else if(v.getTotalVisitCount()>200&&v.getTotalVisitCount()<=300){
                r = random.nextInt(10)+10;
            }else if(v.getTotalVisitCount()>300&&v.getTotalVisitCount()<=400){//大概一个月时200-400左右
                r = random.nextInt(10)+10;
            }else if(v.getTotalVisitCount()>400&&v.getTotalVisitCount()<=500){
                r = random.nextInt(10)+1;
            }else if(v.getTotalVisitCount()>500&&v.getTotalVisitCount()<=800){
                r = random.nextInt(10)+10;
            }else if(v.getTotalVisitCount()>800&&v.getTotalVisitCount()<=1000){//大概两个月时800左右
                r = random.nextInt(30)+20;
            }else if(v.getTotalVisitCount()>1000&&v.getTotalVisitCount()<=5000){//超过1000后平均每天新增30-80个
                r = random.nextInt(50)+30;
            }else{//超过5000后平均每天新增1-30个
                r = random.nextInt(30)+1;
            }
            v.setTodayVisitCount(v.getTodayVisitCount()+r);
            v.setTotalVisitCount(v.getTotalVisitCount()+r);
            count = visitViewService.update(v);
        }
        //更新缓存
        redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(v),CommonUtils.getCurrentTimeTo_12());//今日访问量的生命周期 到今天晚上12点失效
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"定时任务新增用户["+visitView.getUserId()+"]今日访问量和总访问量到数据库失败",new JSONObject());
        }else{
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"定时任务新增用户["+visitView.getUserId()+"]今日访问量和总访问量["+r+"]到数据库失败",new JSONObject());
        }
    }

    /**
     * 批量刷新访问量数据 只刷新机器人 1-10000 13870-53870
     * @return
     */
//    @GetMapping("addVisits")
//    public ReturnData addVisits() {
//        for(int i=1;i<=10000;i++){
//            Random random = new Random();
//            int count = random.nextInt(20000)+1;
//            VisitView v = new VisitView();
//            v.setTotalVisitCount(count);
//            v.setUserId(i);
//            visitViewService.add(v);
//        }
//        for(int i=13870;i<=53870;i++){
//            Random random = new Random();
//            int count = random.nextInt(20000)+1;
//            VisitView v = new VisitView();
//            v.setTotalVisitCount(count);
//            v.setUserId(i);
//            visitViewService.add(v);
//        }
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
//    }
}
