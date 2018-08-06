package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 此处编写本类功能说明
 * author：SunTianJie
 * create time：2018/8/1 11:07
 */
@RestController
public class InitiateGroupController extends BaseController implements InitiateGroupApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    /***
     * 查询指定群成员的用户信息
     * @param houseNumbers 将要查询的用户门牌号组合 格式0_1001518,0_1001519
     * @return
     */
    @Override
    public ReturnData findInitiateGroupMemberInfo(@PathVariable String houseNumbers) {
        //验证参数
        if(CommonUtils.checkFull(houseNumbers)||houseNumbers.indexOf("_")==-1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"houseNumbers参数有误",new JSONObject());
        }
        List list = new ArrayList();
        String[] key = houseNumbers.split(",");
        UserInfo userInfo = null;
        Map<String,Object> userMap = null;
        for(int k=0;k<key.length;k++){
            String hn = key[k];
            if(CommonUtils.checkFull(hn))continue;
            String[] houseNumberArray = hn.split("_");
            if(houseNumberArray==null||houseNumberArray.length!=2)continue;
            Object object = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER,hn);
            if(object==null){//缓存中不存在 门牌号与用户ID的对应关系
                //查询数据库
                userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseNumberArray[0]),houseNumberArray[1]);
                if(userInfo==null)continue;
                userMap = CommonUtils.objectToMap(userInfo);
                userInfo.setPassword("");
                userInfo.setIm_password("");
                list.add(userInfo);
                //更新缓存中 门牌号与用户ID 的对应关系表
                redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,userInfo.getProType()+"_"+userInfo.getHouseNumber(),userInfo.getUserId());
                //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
                continue;
            }
            //缓存中存在对应关系 根据用户ID去用户信息
            userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+object.toString() );
            if(userMap==null||userMap.size()<=0){
                //缓存中没有用户对象信息 查询数据库
                userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseNumberArray[0]),houseNumberArray[1]);
                if(userInfo==null)continue;
                //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                userMap = CommonUtils.objectToMap(userInfo);
                userInfo.setPassword("");
                userInfo.setIm_password("");
                list.add(userInfo);
                //更新缓存中 门牌号与用户ID 的对应关系表
                redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,userInfo.getProType()+"_"+userInfo.getHouseNumber(),userInfo.getUserId());
                //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
            }
            //缓存中存在用户实体 放入集合中
            list.add(userMap);

        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",list);
    }
}
