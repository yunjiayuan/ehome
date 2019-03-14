package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.mq.MqProducer;
import com.busi.service.VisitViewService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Map;

/**
 * 访问量信息相关接口
 * author：SunTianJie
 * create time：2018/7/26 14:53
 */
@RestController
public class VisitViewController extends BaseController implements VisitViewApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    VisitViewService visitViewService;

    @Autowired
    MqProducer mqProducer;

    /***
     * 更新访问量信息
     * @param visitView
     * @return
     */
    @Override
    public ReturnData updateVisit(@Valid @RequestBody VisitView visitView, BindingResult bindingResult) {
        //验证参数
        if(visitView.getUserId()<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //验证是不是自己
        if(CommonUtils.getMyId()==visitView.getUserId()){//自己不做增加
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        //计算当前时间 到 今天晚上12点的秒数差
//        long second = CommonUtils.getCurrentTimeTo_12();
//        //检测访问量对象记录在缓存中是否存在
//        Map<String,Object> visitMap = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId());
//        VisitView vv = null;
//        if(visitMap==null||visitMap.size()<=0){
//            //查询数据库
//            vv = visitViewService.findVisitView(visitView.getUserId());
//            if(vv==null){//之前已有过访问量 加载到内存
//                vv = new VisitView();
//                vv.setUserId(visitView.getUserId());
//            }
//        }else{
//            vv = (VisitView) CommonUtils.mapToObject(visitMap,VisitView.class);
//        }
//        if(vv!=null){
//            vv.setTotalVisitCount(vv.getTotalVisitCount()+1);
//            vv.setTodayVisitCount(vv.getTotalVisitCount()+1);
////            redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(vv),second);//今日访问量的生命周期 到今天晚上12点失效
//        }
//        //开始进行更新访问量+1
//        long totalVisitCount =0;//总访问量
//        long todayVisitCount =0;//今日访问量
//        todayVisitCount = redisUtils.hashIncr(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+visitView.getUserId(),1);//原子操作 递增1
//        redisUtils.expire(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,second);//更新今日访问量的生命周期 到今天晚上12点失效
//        //检测总访问量缓存中是否存在
//        totalVisitCount = redisUtils.hashIncr(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+visitView.getUserId(),1);//原子操作 递增1
//        redisUtils.expire(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,Constants.USER_TIME_OUT);//7天
        //调用MQ同步访问量数据库
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "4");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量
        JSONObject content = new JSONObject();
        content.put("myId",CommonUtils.getMyId());
        content.put("userId",visitView.getUserId() );
        content.put("todayVisitCount",0);//无用字段
        content.put("totalVisitCount",0);//无用字段
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
