package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserInfo;
import com.busi.mq.MqProducer;
import com.busi.service.UserInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录和退出登录接口
 * author：SunTianJie
 * create time：2018/7/4 14:57
 */
@RestController //此处必须继承BaseController和实现项目对应的接口LoginApiController
public class LoginController extends BaseController implements LoginApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    MqProducer mqProducer;

    /***
     * 门牌号登录接口
     * @param account   省简称ID_门牌号、手机号、或第三方平台登录类型   格式 0_1001518 或15901213694 或 1
     * @param password  登录密码(一遍32位MD5加密后的密码)或第三方平台登录key
     * @param loginType 登录类型 0门牌号登录(默认) 1手机号登录 2第三方平台账号登录
     * @return
     */
    @Override
    public ReturnData login(@PathVariable int loginType,@PathVariable String account , @PathVariable String password) {
        //验证参数
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String clientId = request.getHeader("clientId");
        if(CommonUtils.checkFull(clientId)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"clientId参数有误",new JSONObject());
        }
        if(loginType<0||loginType>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"loginType参数有误",new JSONObject());
        }
        Object userId = null;
        UserInfo userInfo = null;
        if(loginType==1){//手机
            if(CommonUtils.checkFull(account)||!CommonUtils.checkPhone(account)||CommonUtils.checkFull(password)||password.length()!=32){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
            }
            //验证缓存中手机号和账号的对应关系是否存在
            userId = redisUtils.hget(Constants.REDIS_KEY_PHONENUMBER,account);
            if(userId==null||Long.parseLong(userId.toString())<=0){
                //缓存中不存在 查询数据库
                userInfo = userInfoService.findUserByPhone(account);
                if(userInfo==null){
                    return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"账号不存在",new JSONObject());
                }
                //将手机号和账号的对应关系存入缓存
                redisUtils.hset(Constants.REDIS_KEY_PHONENUMBER,userInfo.getPhone(),userInfo.getUserId());
                //更新缓存中 门牌号与用户ID 的对应关系表
                if(userInfo.getHouseNumber()>0){
                    redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,userInfo.getProType()+"_"+userInfo.getHouseNumber(),userInfo.getUserId());
                }
                //更新缓存中 第三方平台账号与用户ID 的对应关系表
                if(!CommonUtils.checkFull(userInfo.getOtherPlatformKey())){
                    redisUtils.hset(Constants.REDIS_KEY_OTHERNUMBER,userInfo.getOtherPlatformType()+"_"+userInfo.getOtherPlatformKey(),userInfo.getUserId());
                }
                userId = userInfo.getUserId()+"";
            }
        }else if(loginType==2){//第三方平台账号登录
            if(CommonUtils.checkFull(account)||CommonUtils.checkFull(password)){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
            }
            int otherPlatformType = Integer.parseInt(account);
            if(otherPlatformType<1||otherPlatformType>3){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误OtherPlatformType="+otherPlatformType,new JSONObject());
            }
            //判断第三方账号与账号之间的对应关系在缓存中是否存在
            userId = redisUtils.hget(Constants.REDIS_KEY_OTHERNUMBER,account+"_"+password);
            if(userId==null||Long.parseLong(userId.toString())<=0){
                //第三方账号与账号之间的对应关系在缓存中不存在 查询数据库
                userInfo = userInfoService.findUserByOtherPlatform(otherPlatformType,password);
                if(userInfo==null){
                    //数据库不存在 新增注册
                    userInfo = new UserInfo();
                    userInfo.setOtherPlatformType(otherPlatformType);
                    userInfo.setOtherPlatformKey(password);//此处password充当第三方平台key
                    userInfo.setTime(new Date());
                    userInfo.setAccountStatus(1);//未激活
                    userInfoService.add(userInfo);
                }
                //更新缓存中 第三方平台账号与用户ID 的对应关系表
                redisUtils.hset(Constants.REDIS_KEY_OTHERNUMBER,userInfo.getOtherPlatformType()+"_"+userInfo.getOtherPlatformKey(),userInfo.getUserId());
                //将手机号和账号的对应关系存入缓存
                if(!CommonUtils.checkFull(userInfo.getPhone())){
                    redisUtils.hset(Constants.REDIS_KEY_PHONENUMBER,userInfo.getPhone(),userInfo.getUserId());
                }
                //更新缓存中 门牌号与用户ID 的对应关系表
                if(userInfo.getHouseNumber()>0){
                    redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,userInfo.getProType()+"_"+userInfo.getHouseNumber(),userInfo.getUserId());
                }
                userId = userInfo.getUserId();
            }
        }else{//门牌号登录
            if(CommonUtils.checkFull(account)||account.indexOf("_")<0||CommonUtils.checkFull(password)||password.length()!=32){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
            }
            userId = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER,account);
            //判断门牌号与账号之间的对应关系在缓存中是否存在
            if(userId==null||Long.parseLong(userId.toString())<=0){
                //门牌号与账号之间的对应关系再缓存中不存在  查询数据库
                String houseArray[] = account.split("_");
                userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseArray[0]),houseArray[1]);
                if(userInfo==null){
                    return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"账号不存在",new JSONObject());
                }
                //更新缓存中 门牌号与用户ID 的对应关系表
                redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,userInfo.getProType()+"_"+userInfo.getHouseNumber(),userInfo.getUserId());
                //更新缓存中 第三方平台账号与用户ID 的对应关系表
                if(!CommonUtils.checkFull(userInfo.getOtherPlatformKey())){
                    redisUtils.hset(Constants.REDIS_KEY_OTHERNUMBER,userInfo.getOtherPlatformType()+"_"+userInfo.getOtherPlatformKey(),userInfo.getUserId());
                }
                //将手机号和账号的对应关系存入缓存
                if(!CommonUtils.checkFull(userInfo.getPhone())){
                    redisUtils.hset(Constants.REDIS_KEY_PHONENUMBER,userInfo.getPhone(),userInfo.getUserId());
                }
                userId = userInfo.getUserId()+"";
            }
        }
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
        if(userMap==null||userMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            if(userInfo==null){
                userInfo = userInfoService.findUserById(Long.parseLong(userId.toString()));
            }
            if(userInfo==null){//数据库也没有
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"账号不存在",new JSONObject());
            }
            userMap = CommonUtils.objectToMap(userInfo);
        }
        //添加暴力密码限制
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,userId+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的登录密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服","{}");
        }
        //验证密码是否正确  第三方平台登录不用验证密码
        if(loginType!=2&&!password.equals(userMap.get("password"))){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,userId+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,userId+"",1);
            }
            return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE,"密码错误",new JSONObject());
        }
        //验证账号状态
        int accountStatus = (Integer)userMap.get("accountStatus");
        if(accountStatus>1){//用户账户状态：0：已激活，1：未激活，2：禁用
            return returnData(StatusCode.CODE_ACCOUNT_IS_CLOSE.CODE_VALUE,"该账号已被停用，请联系云家园客服",new JSONObject());
        }
        //存储登录信息
        String loginKey = userId + clientId + System.currentTimeMillis() + CommonUtils.getRandom(6,0);//生成token
        String token = CommonUtils.strToMD5(loginKey,16);
        userMap.put("clientId",clientId);
        userMap.put("token",token);
        redisUtils.hmset(Constants.REDIS_KEY_USER+userId,userMap,Constants.USER_TIME_OUT);

        //调用MQ同步用户登录信息
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "3");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发  3表示用户登录时同步登录信息 4表示新增访问量
        JSONObject content = new JSONObject();
        content.put("userId",userId);//用户ID
        content.put("ip",CommonUtils.getIpAddr(request));//当前的IP地址
        content.put("clientInfo",request.getHeader("user-agent"));//当前设备信息
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);

        //响应客户端
        Map<String,Object> map = new HashMap<>();
        map.put("myId",userId);
        map.put("token",token);
        //获取七牛云存储token
        Object obj = redisUtils.getKey(Constants.REDIS_KEY_QINIU_TOKEN);
        String qiuniu_token = "";
        if(obj==null||CommonUtils.checkFull(obj.toString())){//缓存中不存在 重新生成
            qiuniu_token = CommonUtils.getQiniuToken();
            //更新缓存
            redisUtils.set(Constants.REDIS_KEY_QINIU_TOKEN,qiuniu_token,Constants.USER_TIME_OUT);//7天有效期
        }else{
            qiuniu_token = obj.toString();
        }
        map.put("qiniu_token",qiuniu_token);
        map.put("proType",userMap.get("proType"));
        map.put(Constants.REDIS_KEY_HOUSENUMBER,userMap.get(Constants.REDIS_KEY_HOUSENUMBER));
        map.put("im_password",userMap.get("im_password"));//环信的登录密码
        if(accountStatus==1){//未激活
            return returnData(StatusCode.CODE_ACCOUNT_NOT_ACTIVATED.CODE_VALUE,"该账号未激活",map);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 退出登录接口
     * @param myId 当前登录者的用户ID
     * @return
     */
    @Override
    public ReturnData loginOut(@PathVariable long myId) {
        redisUtils.hset(Constants.REDIS_KEY_USER+myId,"clientId","");
        redisUtils.hset(Constants.REDIS_KEY_USER+myId,"token","");
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
