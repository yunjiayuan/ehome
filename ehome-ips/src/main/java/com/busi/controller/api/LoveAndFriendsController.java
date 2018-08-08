package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.LoveAndFriends;
import com.busi.service.LoveAndFriendsService;
import com.busi.utils.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.Date;
import java.util.Map;
import com.busi.mq.MqProducer;


/**
 * @program: 婚恋交友
 * @author: ZHaoJiaJie
 * @create: 2018-08-02 13:39
 */
@RestController
public class LoveAndFriendsController extends BaseController implements LoveAndFriendsApiController{

    @Autowired
    LoveAndFriendsService loveAndFriendsService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqProducer mqProducer;

    /***
     * 新增
     * @param loveAndFriends
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库（是否已发布过）
        LoveAndFriends andFriends;
        Map<String,Object> loveAndFriendsMap =  redisUtils.hmget(Constants.REDIS_KEY_IPS_LOVEANDFRIEND+CommonUtils.getMyId());
        if(loveAndFriendsMap==null||loveAndFriendsMap.size()<=0){
            andFriends = loveAndFriendsService.findUserById(loveAndFriends.getUserId());
            if(andFriends==null){
                loveAndFriends.setAuditType(2);
                loveAndFriends.setDeleteType(1);
                loveAndFriends.setRefreshTime(new Date());
                loveAndFriends.setReleaseTime(new Date());
                loveAndFriendsService.add(loveAndFriends);
                //放入缓存
                loveAndFriendsMap = CommonUtils.objectToMap(loveAndFriends);
                redisUtils.hmset("loveAndFriends"+CommonUtils.getMyId(),loveAndFriendsMap,Constants.USER_TIME_OUT);
            }else{
                return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE,"该类公告已存在",new JSONObject());
            }
        }else{
            return returnData(StatusCode.CODE_IPS_AFFICHE_EXISTING.CODE_VALUE,"该类公告已存在",new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND+loveAndFriends.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 删除
     * @param userId 将要删除的userId
     * @return
     */
    @Override
    public ReturnData delLove(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的用户ID userId",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限删除用户["+userId+"]的婚恋交友信息",new JSONObject());
        }
        loveAndFriendsService.del(userId);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND+userId,0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更新
     * @param loveAndFriends
     * @return
     */
    @Override
    public ReturnData updateLove(@Valid @RequestBody LoveAndFriends loveAndFriends, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=loveAndFriends.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+loveAndFriends.getUserId()+"]的婚恋交友信息",new JSONObject());
        }
        loveAndFriends.setRefreshTime(new Date());
        loveAndFriendsService.update(loveAndFriends);

        if(!CommonUtils.checkFull(loveAndFriends.getDelImgUrls())){
            //调用MQ同步 图片到图片删除记录表
            JSONObject root = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("interfaceType", "5");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量 5删除图片
            JSONObject content = new JSONObject();
            content.put("delImageUrls",loveAndFriends.getDelImgUrls());
            content.put("userId",loveAndFriends.getUserId());
            root.put("header", header);
            root.put("content", content);
            String sendMsg = root.toJSONString();
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
            mqProducer.sendMsg(activeMQQueue,sendMsg);
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_LOVEANDFRIEND+loveAndFriends.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /**
     * 查询
     * @param userId
     * @return
     */
    @Override
    public ReturnData getLove(@PathVariable  long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的用户ID userId",new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String,Object> loveAndFriendsMap =  redisUtils.hmget(Constants.REDIS_KEY_IPS_LOVEANDFRIEND+userId);
        if(loveAndFriendsMap==null||loveAndFriendsMap.size()<=0){
            LoveAndFriends loveAndFriends = loveAndFriendsService.findUserById(userId);
            if(loveAndFriends==null){
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
            //放入缓存
            loveAndFriendsMap = CommonUtils.objectToMap(loveAndFriends);
            redisUtils.hmset("loveAndFriends"+userId,loveAndFriendsMap,Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",loveAndFriendsMap);
    }

    /***
     * 条件查询接口
     * @param sex  性别:0不限，1男，2女
     * @param income  收入:0不限，1（<3000），2（3000-5000），3（5000-7000），4（7000-9000），5（9000-12000），6（12000-15000），7（15000-20000），8（>20000）
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findListLove(@PathVariable int sex,@PathVariable int income,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(page<0||count<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分页参数有误",new JSONObject());
        }
        if(sex<1||sex>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"性别参数有误",new JSONObject());
        }
        if(income<1||income>5){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"收入参数有误",new JSONObject());
        }
        //开始查询
        PageBean<LoveAndFriends> pageBean;
        pageBean  = loveAndFriendsService.findList(sex,income,page, count);;
        if(pageBean==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,pageBean);
    }

}
