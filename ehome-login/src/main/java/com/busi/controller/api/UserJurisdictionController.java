package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserJurisdiction;
import com.busi.service.UserJurisdictionService;
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
import java.util.Map;

/**
 * 用户权限相关接口（设置功能中的权限设置 包括房间锁和被访问权限）
 * author：SunTianJie
 * create time：2018/7/30 13:36
 */
@RestController
public class UserJurisdictionController extends BaseController implements UserJurisdictionApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserJurisdictionService userJurisdictionService;

    /***
     * 修改权限
     * @param userJurisdiction
     * @return
     */
    @Override
    public ReturnData updateUserJurisdiction(@Valid @RequestBody UserJurisdiction userJurisdiction, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userJurisdiction.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userJurisdiction.getUserId()+"]的权限设置",new JSONObject());
        }
        //判断缓存中和数据库中是否存在 以判断是新增还是更新
        Map<String,Object> userJurisdictionMap = redisUtils.hmget(Constants.REDIS_KEY_USER_JURISDICTION+userJurisdiction.getUserId() );
        if(userJurisdictionMap==null||userJurisdictionMap.size()<=0){
            UserJurisdiction uj = userJurisdictionService.findUserJurisdiction(userJurisdiction.getUserId());
            if(uj==null){
                //之前该用户未设置过权限信息 新增
                userJurisdictionService.add(userJurisdiction);
                //清除缓存
                redisUtils.expire(Constants.REDIS_KEY_USER_JURISDICTION+userJurisdiction.getUserId(),0);//设置过期0秒
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }else{//缓存中存在 判断是否为空对象
            if(Integer.parseInt(userJurisdictionMap.get("redisStatus").toString())==0){//redisStatus==0 说明数据中无此记录
                //之前该用户未设置过权限信息 新增
                userJurisdictionService.add(userJurisdiction);
                //清除缓存
                redisUtils.expire(Constants.REDIS_KEY_USER_JURISDICTION+userJurisdiction.getUserId(),0);//设置过期0秒
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }
        userJurisdictionService.update(userJurisdiction);
        //清除缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_JURISDICTION+userJurisdiction.getUserId(),0);//设置过期0秒
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询权限信息
     * @return
     */
    @Override
    public ReturnData findUserJurisdiction(@PathVariable long userId) {
        if(userId<=0){//参数有误
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_JURISDICTION+userId);
        if(map==null||map.size()<=0){
            UserJurisdiction userJurisdiction = userJurisdictionService.findUserJurisdiction(userId);
            if(userJurisdiction==null){
                //之前该用户未设置过权限信息
                userJurisdiction = new UserJurisdiction();
                userJurisdiction.setUserId(userId);
            }else{
                userJurisdiction.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            redisUtils.hmset(Constants.REDIS_KEY_USER_JURISDICTION+userId,CommonUtils.objectToMap(userJurisdiction),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userJurisdiction);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }
}
