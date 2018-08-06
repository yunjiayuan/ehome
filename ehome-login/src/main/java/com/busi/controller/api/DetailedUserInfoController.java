package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.DetailedUserInfo;
import com.busi.entity.ReturnData;
import com.busi.service.DetailedUserInfoService;
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
import java.util.Date;
import java.util.Map;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/7/19 14:35
 */
@RestController
public class DetailedUserInfoController extends BaseController implements DetailedUserInfoApiController{

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DetailedUserInfoService detailedUserInfoService;

    /***
     * 查询用户详细信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findDetailedUserInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER_DETAILED+userId );
        if(userMap==null||userMap.size()<=0){
            DetailedUserInfo detailedUserInfo = null;
            //缓存中没有用户对象信息 查询数据库
            detailedUserInfo = detailedUserInfoService.findUserDetailedById(userId);
            if(detailedUserInfo==null){
                detailedUserInfo = new DetailedUserInfo();
                detailedUserInfo.setUserId(userId);
            }else{
                detailedUserInfo.setRedisStatus(1);//数据库中已有对应记录
            }
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USER_DETAILED+userId,CommonUtils.objectToMap(detailedUserInfo),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",detailedUserInfo);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userMap);
    }

    /***
     * 新增和修改用户详细资料接口
     * @param detailedUserInfo
     * @return
     */
    @Override
    public ReturnData updateDetailedUserInfo(@Valid @RequestBody DetailedUserInfo detailedUserInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=detailedUserInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+detailedUserInfo.getUserId()+"]的详细资料信息",new JSONObject());
        }
        //判断缓存中和数据库中是否存在 以判断是新增还是更新
        Map<String,Object> detailedUserMap = redisUtils.hmget(Constants.REDIS_KEY_USER_DETAILED+detailedUserInfo.getUserId() );
        if(detailedUserMap==null||detailedUserMap.size()<=0){
            DetailedUserInfo du = detailedUserInfoService.findUserDetailedById(detailedUserInfo.getUserId());
            if(du==null){//数据库中不存在
                detailedUserInfo.setTime(new Date());
                detailedUserInfoService.add(detailedUserInfo);
                //使缓存中的用户详细信息失效  查询时会重新加载
                redisUtils.expire(Constants.REDIS_KEY_USER_DETAILED+detailedUserInfo.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }else{//缓存中存在 判断是否为空对象
            if(Integer.parseInt(detailedUserMap.get("redisStatus").toString())==0){//redisStatus==0 说明数据中无此记录
                detailedUserInfo.setTime(new Date());
                detailedUserInfoService.add(detailedUserInfo);
                //使缓存中的用户详细信息失效  查询时会重新加载
                redisUtils.expire(Constants.REDIS_KEY_USER_DETAILED+detailedUserInfo.getUserId(),0);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
            }
        }
        //缓存中存在 则更新
        detailedUserInfo.setTime(new Date());
        detailedUserInfoService.update(detailedUserInfo);
        //使缓存中的用户详细信息失效  查询时会重新加载
        redisUtils.expire(Constants.REDIS_KEY_USER_DETAILED+detailedUserInfo.getUserId(),0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
