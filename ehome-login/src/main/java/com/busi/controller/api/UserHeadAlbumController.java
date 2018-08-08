package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadAlbum;
import com.busi.mq.MqProducer;
import com.busi.service.UserHeadAlbumService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户个人资料界面的九张头像相册 接口
 * author：SunTianJie
 * create time：2018/7/25 15:36
 */
@RestController
public class UserHeadAlbumController extends BaseController implements UserHeadAlbumApiController {

    @Autowired
    UserHeadAlbumService userHeadAlbumService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqProducer mqProducer;

    /***
     * 查询用户九张头像相册
     * @param userId 被查询者的用户ID
     * @return
     */
    @Override
    public ReturnData findUserHeadAlbum(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //查询缓存中是否存在
        Map<String,Object> userHeadAlbumMap = redisUtils.hmget(Constants.REDIS_KEY_USER_HEADALBUN+userId );
        UserHeadAlbum userHeadAlbum = null;
        if(userHeadAlbumMap==null||userHeadAlbumMap.size()<=0){
            //缓存中没有 查询数据库
            userHeadAlbum = userHeadAlbumService.findUserHeadAlbumById(userId);
            if(userHeadAlbum==null){//数据库中不存在
                userHeadAlbum = new UserHeadAlbum();
                userHeadAlbum.setUserId(userId);
//                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }else{
                userHeadAlbum.setRedisStatus(1);//数据库中已有对应记录
            }
            userHeadAlbumMap = CommonUtils.objectToMap(userHeadAlbum);
            //将用户头像相册信息存入缓存中
            redisUtils.hmset(Constants.REDIS_KEY_USER_HEADALBUN+userId,userHeadAlbumMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userHeadAlbumMap);
    }

    /***
     * 更新接口
     * @param userHeadAlbum
     * @return
     */
    @Override
    public ReturnData updateUserHeadAlbum(@Valid @RequestBody UserHeadAlbum userHeadAlbum, BindingResult bindingResult) {
        //验证修改人权限
        if(CommonUtils.getMyId()!=userHeadAlbum.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userHeadAlbum.getUserId()+"]的九张头像相册",new JSONObject());
        }
        //查询缓存中是否存在
        Map<String,Object> userHeadAlbumMap = redisUtils.hmget(Constants.REDIS_KEY_USER_HEADALBUN+userHeadAlbum.getUserId() );
        if(userHeadAlbumMap==null||userHeadAlbumMap.size()<=0){
            UserHeadAlbum uha = userHeadAlbumService.findUserHeadAlbumById(userHeadAlbum.getUserId());
            if(uha==null){//数据库中不存在
                userHeadAlbumService.add(userHeadAlbum);
                //将缓存中数据 清除
                redisUtils.expire(Constants.REDIS_KEY_USER_HEADALBUN+userHeadAlbum.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }else{//缓存中存在 判断是否为空对象
            if(Integer.parseInt(userHeadAlbumMap.get("redisStatus").toString())==0){//redisStatus==0 说明数据中无此记录
                userHeadAlbumService.add(userHeadAlbum);
                //将缓存中数据 清除
                redisUtils.expire(Constants.REDIS_KEY_USER_HEADALBUN+userHeadAlbum.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }
        //缓存中存在 则更新
        userHeadAlbumService.updateUserHeadAlbum(userHeadAlbum);
        //将缓存中数据 清除
        redisUtils.expire(Constants.REDIS_KEY_USER_HEADALBUN+userHeadAlbum.getUserId(),0);

        if(!CommonUtils.checkFull(userHeadAlbum.getDelImageUrls())){
            //调用MQ同步 图片到图片删除记录表
            JSONObject root = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("interfaceType", "5");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量 5删除图片
            JSONObject content = new JSONObject();
            content.put("delImageUrls",userHeadAlbum.getDelImageUrls());
            content.put("userId",userHeadAlbum.getUserId());
            root.put("header", header);
            root.put("content", content);
            String sendMsg = root.toJSONString();
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
            mqProducer.sendMsg(activeMQQueue,sendMsg);
        }

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
