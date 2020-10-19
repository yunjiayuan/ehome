package com.busi.controller.api;

import com.busi.entity.PickNumber;
import com.busi.entity.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.mq.MqProducer;
import com.busi.service.GraffitiChartLogService;
import com.busi.service.PickNumberNewService;
import com.busi.service.UserInfoService;
import com.busi.service.VisitViewService;
import com.busi.utils.*;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 注册接口 Controller
 * author：SunTianJie
 * create time：2018/6/7 16:06
 */
@RestController //此处必须继承BaseController和实现项目对应的接口RegisterApiController
public class RegisterController extends BaseController implements RegisterApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    PickNumberNewService pickNumberNewService;

    @Autowired
    VisitViewService visitViewService;

    @Autowired
    GraffitiChartLogService graffitiChartLogService;

    @Autowired
    MqProducer mqProducer;

    @Autowired
    MqUtils mqUtils;

    /***
     * 门牌号注册接口
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData registerByHouseNumber(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        //验证地区正确性
        if(!CommonUtils.checkProvince_city_district(userInfo.getCountry(),userInfo.getProvince(),userInfo.getCity(),userInfo.getDistrict())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"国家、省、市、区参数不匹配",new JSONObject());
        }
        //验证不能为空的参数
        if(CommonUtils.checkFull(userInfo.getName())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"用户名不能为空",new JSONObject());
        }
        if(CommonUtils.checkFull(userInfo.getPassword())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"密码不能为空",new JSONObject());
        }
        if(CommonUtils.checkFull(userInfo.getCode())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"验证码不能为空",new JSONObject());
        }
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //二次校验验证码
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");//用户注册的临时令牌 用于标识临时用户访问数据的唯一性
        if(CommonUtils.checkFull(token)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"客户端参数token为空",new JSONObject());
        }
        String serverCode = (String) redisUtils.getKey(Constants.REDIS_KEY_REG_TOKEN+token);
        if(CommonUtils.checkFull(serverCode)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码无效或者已过期",new JSONObject());
        }

        if(!serverCode.equals(userInfo.getCode().trim())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码输入有误",new JSONObject());
        }
        //验证成功 清除验证码
        redisUtils.expire(Constants.REDIS_KEY_REG_TOKEN+token,0);//设置过期时间 0秒后失效
        //开始注册
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setName(userInfo.getName());
        newUserInfo.setPassword(userInfo.getPassword());
        newUserInfo.setIm_password(CommonUtils.strToMD5(userInfo.getPassword(),32));//环信密码为两遍MD5
        newUserInfo.setSex(userInfo.getSex());
        newUserInfo.setBirthday(userInfo.getBirthday());
        newUserInfo.setCountry(userInfo.getCountry());
        newUserInfo.setProvince(userInfo.getProvince());
        newUserInfo.setCity(userInfo.getCity());
        newUserInfo.setDistrict(userInfo.getDistrict());
        newUserInfo.setIdCard(userInfo.getIdCard());
        newUserInfo.setTime(new Date());
        newUserInfo.setAccessRights(1);
        newUserInfo.setProType(Constants.PRO_INFO_ARRAY[userInfo.getProvince()]);
        //生成默认头像
        Random random = new Random();
        newUserInfo.setHead("image/head/defaultHead/defaultHead_"+random.nextInt(20)+"_225x225.jpg");
        //生成门牌号
        newUserInfo.setHouseNumber(CommonUtils.getHouseNumber(newUserInfo.getProType(),redisUtils));
        //写入数据库
        userInfoService.add(newUserInfo);
        //更新缓存中 门牌号与用户ID 的对应关系表
        //重新登录时 会重新加载到缓存中 此处不再同步
//        redisUtils.hset("houseNumber",newUserInfo.getProType()+"_"+newUserInfo.getHouseNumber(),newUserInfo.getUserId());
        //调用activeMQ消息系统 同步门牌号记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "2");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量
        JSONObject content = new JSONObject();
        content.put("proType",newUserInfo.getProType() );
        content.put("houseNumber",newUserInfo.getHouseNumber() );
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);
        //同步环信 由于环信服务端接口限流每秒30次 所以此操作改到客户端完成 拼接注册环信需要的参数 返回给客户端 环信账号改成用户ID
        Map<String,String> im_map = new HashMap<>();
        im_map.put("proType",newUserInfo.getProType()+"");
        im_map.put("houseNumber",newUserInfo.getHouseNumber()+"");
        im_map.put("myId",newUserInfo.getUserId()+"");
        im_map.put("password",newUserInfo.getIm_password());//环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",im_map);
    }

    /***
     * 生成验证码
     * @param type 0生成服务端验证码（默认）  1生成短信验证码
     * @param phone 手机号 仅当参数type=1时有效
     * @return
     */
    @Override
    public ReturnData createCode(@PathVariable int type , @PathVariable String phone) {
        //生成临 时token
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String clientId = request.getHeader("clientId");//客户端设备的唯一标识（手机端/PC端）
        if(CommonUtils.checkFull(clientId)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"客户端设备唯一标识clientId不能为空",new JSONObject());
        }
        String regToken = CommonUtils.strToMD5(clientId+System.currentTimeMillis()+CommonUtils.getRandom(6,0), 16);//注册用户的临时key
        String code = CommonUtils.getRandom(4,1);
        //同一手机号每小时限制
        String accountHourTotal = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_ACCOUNT_HOUR_TOTAL,phone+""));
        if(!CommonUtils.checkFull(accountHourTotal)&&Integer.parseInt(accountHourTotal)>Constants.PHONE_HOUR_TOTAL){
            return returnData(StatusCode.CODE_SMS_USEROVER_ERROR.CODE_VALUE,"您当前手机号发送的短信次数过多,请一个小时后再试，如有疑问请联系官方客服",new JSONObject());
        }
        //同一手机号每天时限制
        String accountDayTotal = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_ACCOUNT_DAY_TOTAL,phone+""));
        if(!CommonUtils.checkFull(accountDayTotal)&&Integer.parseInt(accountDayTotal)>Constants.PHONE_DAY_TOTAL){
            return returnData(StatusCode.CODE_SMS_USEROVER_ERROR.CODE_VALUE,"您当前手机号发送的短信次数过多,系统已自动停用当前账号使用短信功能一天，如有疑问请联系官方客服",new JSONObject());
        }
        //同一客户端设备每小时限制
        String clientHourTotal = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_CLIENT_HOUR_TOTAL,clientId+""));
        if(!CommonUtils.checkFull(clientHourTotal)&&Integer.parseInt(clientHourTotal)>Constants.CLIENT_HOUR_TOTAL){
            return returnData(StatusCode.CODE_SMS_PHONEOVER_ERROR.CODE_VALUE,"您当前设备发送的短信次数过多,请一个小时后再试，如有疑问请联系官方客服",new JSONObject());
        }
        //同一客户端设备每天时限制
        String clientDayTotal = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_CLIENT_DAY_TOTAL,clientId+""));
        if(!CommonUtils.checkFull(clientDayTotal)&&Integer.parseInt(clientDayTotal)>Constants.ACCOUNT_DAY_TOTAL){
            return returnData(StatusCode.CODE_SMS_PHONEOVER_ERROR.CODE_VALUE,"您当前设备发送的短信次数过多,系统已自动停用当前账号使用短信功能一天，如有疑问请联系官方客服",new JSONObject());
        }
        if(type==1){//生成短信验证码
            //验证要注册的手机号是否被占用
            if(CommonUtils.checkFull(phone)||!CommonUtils.checkPhone(phone)){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"手机号格式有误",new JSONObject());
            }
            Object userId = redisUtils.hget(Constants.REDIS_KEY_PHONENUMBER,phone);
            if(userId!=null){//存在 则证明该手机号已被注册过
                return returnData(StatusCode.CODE_SMS_PHONEBOUND_ERROR.CODE_VALUE,"该手机号已被注册过",new JSONObject());
            }
            //调用MQ进行发送短信
            mqUtils.sendPhoneMessage(phone,code,0);
//            JSONObject root = new JSONObject();
//            JSONObject header = new JSONObject();
//            header.put("interfaceType", "0");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发
//            JSONObject content = new JSONObject();
//            content.put("phone",phone);//将要发送短信的手机号
//            content.put("phoneType",0);//短信类型 0表示注册时发送短信
//            content.put("phoneCode",code);//短信验证码
//            root.put("header", header);
//            root.put("content", content);
//            String sendMsg = root.toJSONString();
//            ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
//            mqProducer.sendMsg(activeMQQueue,sendMsg);
            //将验证码存入缓存 后边注册时使用
            redisUtils.set(Constants.REDIS_KEY_REG_TOKEN+regToken,phone+"_"+code,60*10);//验证码10分钟内有效
            code = "";//手机验证码不返回客户端
            //更新同一手机号每小时限制
            if(CommonUtils.checkFull(accountHourTotal)){//第一次
                redisUtils.hset(Constants.REDIS_KEY_ACCOUNT_HOUR_TOTAL,phone+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_ACCOUNT_HOUR_TOTAL,phone+"",1);
            }
            //更新同一手机号每天时限制
            if(CommonUtils.checkFull(accountDayTotal)){//第一次
                redisUtils.hset(Constants.REDIS_KEY_ACCOUNT_DAY_TOTAL,phone+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_ACCOUNT_DAY_TOTAL,phone+"",1);
            }
        }else{
            //将验证码存入缓存 后边注册时使用
            redisUtils.set(Constants.REDIS_KEY_REG_TOKEN+regToken,code,60*10);//验证码10分钟内有效
            //更新同一客户端设备每小时限制
            if(CommonUtils.checkFull(clientHourTotal)){//第一次
                redisUtils.hset(Constants.REDIS_KEY_CLIENT_HOUR_TOTAL,clientId+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_CLIENT_HOUR_TOTAL,clientId+"",1);
            }
            //更新同一客户端设备每天时限制
            if(CommonUtils.checkFull(clientDayTotal)){//第一次
                redisUtils.hset(Constants.REDIS_KEY_CLIENT_DAY_TOTAL,clientId+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_CLIENT_DAY_TOTAL,clientId+"",1);
            }
        }
        //响应客户端
        Map<String,String> map = new HashMap();
        map.put("regToken",regToken);
        map.put("code",code);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 校验服务端验证码
     * @return
     */
    @Override
    public ReturnData checkCode(@PathVariable String code) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");//用户注册的临时令牌 用于标识临时用户访问数据的唯一性
        if(CommonUtils.checkFull(token)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"客户端参数token为空，验证失败！",new JSONObject());
        }
        String serverCode = (String) redisUtils.getKey(Constants.REDIS_KEY_REG_TOKEN+token);
        if(CommonUtils.checkFull(serverCode)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码无效或者已过期，验证失败！",new JSONObject());
        }
        if(!serverCode.equals(code.trim())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码输入有误，验证失败！",new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 手机号注册接口
     * @param userInfo
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData registerByPhone(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证手机端注册token是否正确
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");//用户注册的临时令牌 用于标识临时用户访问数据的唯一性
        if(CommonUtils.checkFull(token)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"客户端参数token为空",new JSONObject());
        }
        String serverCode = (String) redisUtils.getKey(Constants.REDIS_KEY_REG_TOKEN+token);
        if(CommonUtils.checkFull(serverCode)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码无效或者已过期",new JSONObject());
        }
        //验证要注册的手机号是否被占用
        Object userId = redisUtils.hget(Constants.REDIS_KEY_PHONENUMBER,userInfo.getPhone());
        if(userId!=null){//存在 则证明该手机号已被注册过
            return returnData(StatusCode.CODE_SMS_PHONEBOUND_ERROR.CODE_VALUE,"该手机号已被注册过",new JSONObject());
        }
        //验证注册手机号是否与发短信手机号相同
        String[] codeArray = serverCode.split("_");
        if(codeArray==null||codeArray.length!=2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码已过期,请重新获取",new JSONObject());
        }
        if(!codeArray[0].equals(userInfo.getPhone())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"手机号输入有误，注册手机号与接收短信的手机号必须相同",new JSONObject());
        }
        //验证手机验证码是否正确
        if(!codeArray[1].equals(userInfo.getCode().trim())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"手机验证码输入有误",new JSONObject());
        }
        //验证成功 清除验证码
        redisUtils.expire(Constants.REDIS_KEY_REG_TOKEN+token,0);//设置过期时间 0秒后失效
        //开始注册
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setPhone(userInfo.getPhone());
        newUserInfo.setPassword(userInfo.getPassword());
        newUserInfo.setIm_password(CommonUtils.strToMD5(userInfo.getPassword(),32));//环信密码为两遍MD5
        newUserInfo.setTime(new Date());
        newUserInfo.setAccountStatus(1);//未激活
        userInfoService.add(newUserInfo);
        //对于手机号和第三方平台注册用户 需要自动绑定安全中心中的相关数据
        mqUtils.sendUserAccountSecurityMQ(newUserInfo.getUserId(),newUserInfo.getPhone(),newUserInfo.getOtherPlatformType(),newUserInfo.getOtherPlatformAccount(),newUserInfo.getOtherPlatformKey());
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 完善资料接口
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData perfectUserInfo(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap==null||userMap.size()<=0){
            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"要完善资料的账号不存在",new JSONObject());
        }
        int accountStatus = Integer.parseInt(userMap.get("accountStatus").toString());
        if(accountStatus!=1){//未激活状态 才能完善资料
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"该账号未处于未激活状态，不能完善资料",new JSONObject());
        }
        //验证完善类型与操作是否相符
        if(userInfo.getType()==1&&CommonUtils.checkFull(String.valueOf(userMap.get("otherPlatformKey")))){//第三方平台完善资料
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，完善资料类型与当前操作不符",new JSONObject());
        }
        if(userInfo.getType()==0&&CommonUtils.checkFull(String.valueOf(userMap.get("phone")))){//手机号完善资料
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，完善资料类型与当前操作不符",new JSONObject());
        }
        //开始完善
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setUserId(userInfo.getUserId());
        newUserInfo.setName(userInfo.getName());
        userMap.put("name",newUserInfo.getName());
        newUserInfo.setSex(userInfo.getSex());
        userMap.put("sex",newUserInfo.getSex());
        newUserInfo.setBirthday(userInfo.getBirthday());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(newUserInfo.getBirthday());
        userMap.put("birthday",date);
        newUserInfo.setCountry(userInfo.getCountry());
        userMap.put("country",newUserInfo.getCountry());
        newUserInfo.setProvince(userInfo.getProvince());
        userMap.put("province",newUserInfo.getProvince());
        newUserInfo.setCity(userInfo.getCity());
        userMap.put("city",newUserInfo.getCity());
        newUserInfo.setDistrict(userInfo.getDistrict());
        userMap.put("district",newUserInfo.getDistrict());
        newUserInfo.setIdCard(userInfo.getIdCard());
        userMap.put("idCard",newUserInfo.getIdCard());
        newUserInfo.setAccessRights(1);
        userMap.put("accessRights",newUserInfo.getAccessRights());
        newUserInfo.setProType(Constants.PRO_INFO_ARRAY[userInfo.getProvince()]);
        userMap.put("proType",newUserInfo.getProType());
        //生成默认头像
        Random random = new Random();
        newUserInfo.setHead("image/head/defaultHead/defaultHead_"+random.nextInt(20)+"_225x225.jpg");
        userMap.put("head",newUserInfo.getHead());
        //生成门牌号
        newUserInfo.setHouseNumber(CommonUtils.getHouseNumber(newUserInfo.getProType(),redisUtils));
        userMap.put("houseNumber",newUserInfo.getHouseNumber());
        //处理密码问题
        if(userInfo.getType()==1){//第三方平台完善资料 设置新密码
            newUserInfo.setPassword(userInfo.getPassword());
            newUserInfo.setIm_password(CommonUtils.strToMD5(userInfo.getPassword(),32));//环信密码为两遍MD5
        }else{//手机号完善资料 无需设置新密码
            newUserInfo.setPassword((String)redisUtils.hget(Constants.REDIS_KEY_USER+userInfo.getUserId(),"password"));
            newUserInfo.setIm_password((String)redisUtils.hget(Constants.REDIS_KEY_USER+userInfo.getUserId(),"im_password"));//环信密码
        }
        userMap.put("password",newUserInfo.getPassword());
        userMap.put("im_password",newUserInfo.getIm_password());
        newUserInfo.setAccountStatus(0);//改成已激活
        userMap.put("accountStatus",newUserInfo.getAccountStatus());
        //写入数据库
        userInfoService.perfectUserInfo(newUserInfo);
        //修改缓存中的用户信息
        redisUtils.hmset(Constants.REDIS_KEY_USER+newUserInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
        //更新缓存中 门牌号与用户ID 的对应关系表
        redisUtils.hset(Constants.REDIS_KEY_HOUSENUMBER,newUserInfo.getProType()+"_"+newUserInfo.getHouseNumber(),newUserInfo.getUserId());

//        //删除缓存中的用户对象和相关对应关系  重新登录时 会重新加载到缓存中
//        redisUtils.delKey(Constants.REDIS_KEY_USER+userInfo.getUserId());
//        if(userInfo.getType()==1){//第三方平台完善资料
//            redisUtils.hdel(Constants.REDIS_KEY_OTHERNUMBER,String.valueOf(userMap.get("otherPlatformType"))+"_"+String.valueOf(userMap.get("otherPlatformKey")));
//        }else{//手机号完善资料
//            redisUtils.hdel(Constants.REDIS_KEY_PHONENUMBER,String.valueOf(userMap.get("phone")));
//        }
        //调用activeMQ消息系统 同步门牌号记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "2");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发
        JSONObject content = new JSONObject();
        content.put("proType",newUserInfo.getProType() );
        content.put("houseNumber",newUserInfo.getHouseNumber() );
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);
        //同步环信 由于环信服务端接口限流每秒30次 所以此操作改到客户端完成 拼接注册环信需要的参数 返回给客户端 环信账号改成用户ID
        Map<String,String> im_map = new HashMap<>();
//        im_map.put("proType",newUserInfo.getProType()+"");
//        im_map.put("houseNumber",newUserInfo.getHouseNumber()+"");
        im_map.put("password",newUserInfo.getIm_password());//环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",im_map);
    }

    /***
     * 查询用户基本信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @Override
    public ReturnData findUserInfo(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
        UserInfo userInfo = null;
        if(userMap==null||userMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            userInfo = userInfoService.findUserById(userId);
        }else{
            userInfo = (UserInfo) CommonUtils.mapToObject(userMap,UserInfo.class);
            if(userInfo==null){
                //缓存中数据异常 查询数据库
                userInfo = userInfoService.findUserById(userId);
            }
        }
        if(userInfo==null){
            return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"将要访问串门的用户不存在",new JSONObject());
        }
        //设置访问量信息
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+userId);
        VisitView visitView = null;
        if(map==null||map.size()<=0){//缓存中不存在 查询数据库
            visitView = visitViewService.findVisitView(userId);
            if(visitView==null){
                visitView = new VisitView();
                visitView.setUserId(userId);
                if(CommonUtils.getMyId()==userId){//自己看自己
                    visitView.setTodayVisitCount(0);//初始化今日访问量
                    visitView.setTotalVisitCount(0);//初始化总访问量
                }else{//别人看自己
                    visitView.setTodayVisitCount(1);//初始化今日访问量
                    visitView.setTotalVisitCount(1);//初始化总访问量
                }
            }
            userInfo.setTodayVisitCount(visitView.getTodayVisitCount());//初始化今日访问量
            userInfo.setTotalVisitCount(visitView.getTotalVisitCount());//设置总访问量
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(visitView),CommonUtils.getCurrentTimeTo_12());//保证今日访问量的生命周期 到今天晚上12点失效
        }else{//缓存中存在
            VisitView vv  = (VisitView) CommonUtils.mapToObject(map,VisitView.class);
            if(vv==null){//防止异常数据情况
                visitView = visitViewService.findVisitView(userId);
                if(visitView==null){
                    visitView = new VisitView();
                    visitView.setUserId(userId);
                    if(CommonUtils.getMyId()==userId){//自己看自己
                        visitView.setTodayVisitCount(0);//初始化今日访问量
                        visitView.setTotalVisitCount(0);//初始化总访问量
                    }else{//别人看自己
                        visitView.setTodayVisitCount(1);//初始化今日访问量
                        visitView.setTotalVisitCount(1);//初始化总访问量
                    }
                }
                userInfo.setTodayVisitCount(visitView.getTodayVisitCount());//初始化今日访问量
                userInfo.setTotalVisitCount(visitView.getTotalVisitCount());//设置总访问量
                //更新缓存
                redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(visitView),CommonUtils.getCurrentTimeTo_12());//保证今日访问量的生命周期 到今天晚上12点失效
            }else{
                userInfo.setTodayVisitCount(vv.getTodayVisitCount());//设置今日访问量
                userInfo.setTotalVisitCount(vv.getTotalVisitCount());//设置总访问量
            }
        }
        userInfo.setPassword("");//过滤登录密码
        userInfo.setIm_password("");//过滤环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",CommonUtils.objectToMap(userInfo));
    }

    /***
     * 修改用户基本资料接口
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateUserInfo(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userInfo.getUserId()+"]的基本信息",new JSONObject());
        }
        //开始修改
        userInfoService.update(userInfo);
        UserInfo newUserInfo = userInfoService.findUserById(userInfo.getUserId());
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+newUserInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            newUserInfo.setClientId(userMap.get("clientId").toString());
            newUserInfo.setToken(userMap.get("token").toString());
            newUserInfo.setRadius(Integer.parseInt(userMap.get("radius").toString()));
            if(userMap.get("positionTime")!=null&&!CommonUtils.checkFull(userMap.get("positionTime").toString())){
                newUserInfo.setPositionTime(userMap.get("positionTime").toString());
            }
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hmset(Constants.REDIS_KEY_USER+newUserInfo.getUserId(),CommonUtils.objectToMap(newUserInfo),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+newUserInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        //添加任务
        mqUtils.sendTaskMQ(userInfo.getUserId(),0,1);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更新用户头像接口
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateUserHead(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(CommonUtils.checkFull(userInfo.getHead())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"头像地址不能为空",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userInfo.getUserId()+"]的基本信息",new JSONObject());
        }
        //开始修改
        userInfoService.updateHead(userInfo);
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"head",userInfo.getHead(),Constants.USER_TIME_OUT);
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"graffitiHead","",Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        //添加任务
        mqUtils.sendTaskMQ(userInfo.getUserId(),0,0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 修改用户访问权限接口  停用
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateUserAccessRights(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userInfo.getUserId()+"]的基本信息",new JSONObject());
        }
        //开始修改
        userInfoService.updateUserAccessRights(userInfo);
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"accessRights",userInfo.getAccessRights(),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 修改账号状态接口 启用、停用
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData updateAccountStatus(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if(CommonUtils.getAdministrator(myId,redisUtils)<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userInfo.getUserId() + "]的账号状态", new JSONObject());
        }
        //开始修改
        userInfoService.updateAccountStatus(userInfo);
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"accountStatus",userInfo.getAccountStatus(),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 修改新用户系统欢迎消息状态接口
     * @return
     */
    @Override
    public ReturnData updateWelcomeInfoStatus() {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(CommonUtils.getMyId());
        userInfo.setWelcomeInfoStatus(1);//修改为已发送
        //开始修改
        userInfoService.updateWelcomeInfoStatus(userInfo);
        //更新缓存数据
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"welcomeInfoStatus",userInfo.getWelcomeInfoStatus(),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 修改登录密码接口
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData changePassWord(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(CommonUtils.checkFull(userInfo.getPassword())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"原密码不能为空",new JSONObject());
        }
        if(CommonUtils.checkFull(userInfo.getNewPassword())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"新密码不能为空",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userInfo.getUserId()+"]的密码信息",new JSONObject());
        }
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            if(userMap.get("password")==null){
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"当前账户登录状态出现异常，建议重新登录后再重试此操作",new JSONObject());
            }
            //验证旧密码是否正确
            if(!userInfo.getPassword().equals(userMap.get("password").toString())){
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"原密码不正确",new JSONObject());
            }
            //开始修改
            userInfoService.changePassWord(userInfo);
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"password",userInfo.getNewPassword(),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 重置密码接口（用于其它方式修改和找回密码操作）
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData resetPassWord(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证参数格式
        if(CommonUtils.checkFull(userInfo.getNewPassword())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"新密码不能为空",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userInfo.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userInfo.getUserId()+"]的密码信息",new JSONObject());
        }
        //验证修改key是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_CHANGE_PASSWORD_KEY+CommonUtils.getMyId());
        if(serverCode==null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"修改秘钥已过期,请重新获取",new JSONObject());
        }
        //判断验证码是否正确
        if(!serverCode.toString().equals(userInfo.getKey())){//不相等
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"秘钥有误,修改失败",new JSONObject());
        }
        //获取缓存中的登录信息
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userInfo.getUserId());
        if(userMap!=null&&userMap.size()>0){//缓存中存在 才更新 不存在不更新
            //开始修改
            userInfoService.changePassWord(userInfo);
            //更新缓存 自己修改自己的用户信息 不考虑并发问题
            redisUtils.hset(Constants.REDIS_KEY_USER+userInfo.getUserId(),"password",userInfo.getNewPassword(),Constants.USER_TIME_OUT);
            redisUtils.expire(Constants.REDIS_KEY_USER+userInfo.getUserId(),Constants.USER_TIME_OUT);
        }else{//缓存中不存在 只更新数据库
            //开始修改
            userInfoService.changePassWord(userInfo);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 找回密码验证账号是否存在
     * @param userAccount 门牌号组合 0_1001518
     * @param code        验证码
     * @return
     */
    @Override
    public ReturnData checkAccount(@PathVariable String userAccount,@PathVariable String code) {
        //验证参数格式
        if(CommonUtils.checkFull(userAccount)||userAccount.indexOf("_")==-1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userAccount参数格式有误",new JSONObject());
        }
        if(CommonUtils.checkFull(code)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"code参数格式有误",new JSONObject());
        }
        //验证验证码
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String token = request.getHeader("token");//用户注册的临时令牌 用于标识临时用户访问数据的唯一性
        if(CommonUtils.checkFull(token)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"客户端参数token为空",new JSONObject());
        }
        String serverCode = (String) redisUtils.getKey(Constants.REDIS_KEY_REG_TOKEN+token);
        if(CommonUtils.checkFull(serverCode)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码无效或者已过期",new JSONObject());
        }

        if(!serverCode.equals(code)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"该验证码输入有误",new JSONObject());
        }
        //验证成功 清除验证码
        redisUtils.expire(Constants.REDIS_KEY_REG_TOKEN+token,0);//设置过期时间 0秒后失效
        Object userId = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER,userAccount);
        if(userId==null||Long.parseLong(userId.toString())<=0){
            //门牌号与账号之间的对应关系再缓存中不存在  查询数据库
            String houseArray[] = userAccount.split("_");
            UserInfo userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseArray[0]),houseArray[1]);
            if(userInfo==null){
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"账号不存在",new JSONObject());
            }
            userId = userInfo.getUserId()+"";
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 完善资料界面中绑定已有门牌号
     * @param homeNumber           将要绑定的门牌号组合格式:0_1001518(目标门票号)
     * @param password             将要绑定的门牌号密码（一遍MD5加密后）
     * @param otherPlatformKey     当bindType=0时，此参数为手机号 ； 当bindType=1时，此参数为第三方平台key
     * @param otherPlatformAccount 第三方平台昵称
     * @param otherPlatformType    第三方平台类型 1：QQ，2：微信
     * @param bindType             绑定类型 0表示手机号绑定门牌号  1表示第三方平台账号绑定门牌号
     * @return
     */
    @Override
    public ReturnData bindHouseNumber(@PathVariable String homeNumber,@PathVariable String password,@PathVariable String otherPlatformKey,
                                      @PathVariable String otherPlatformAccount,@PathVariable int otherPlatformType,@PathVariable int bindType) {
        //验证参数
        if(CommonUtils.checkFull(homeNumber)||homeNumber.indexOf("_")==-1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"homeNumber参数有误",new JSONObject());
        }
        if(CommonUtils.checkFull(password)||password.length()!=32){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"password参数有误",new JSONObject());
        }
        if(CommonUtils.checkFull(otherPlatformKey)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"otherPlatformKey参数有误",new JSONObject());
        }
        if(otherPlatformType<0||otherPlatformType>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"otherPlatformType参数有误",new JSONObject());
        }
        if(bindType<0||bindType>1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"bindType参数有误",new JSONObject());
        }
        //验证当前登录账号是否是未激活状态
        Map<String,Object> myMap = redisUtils.hmget(Constants.REDIS_KEY_USER+CommonUtils.getMyId());
        if(myMap==null||myMap.size()<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"当前账号存在异常，建议重新登录后再试",new JSONObject());
        }
        UserInfo myUserInfo = (UserInfo) CommonUtils.mapToObject(myMap,UserInfo.class);
        if(myUserInfo==null||myUserInfo.getAccountStatus()!=1){//未激活状态才能绑定
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"当前账号无法使用该功能进行绑定",new JSONObject());
        }
        //验证目标门票号账号状态是否正常、密码是否正确
        Object userId = redisUtils.hget(Constants.REDIS_KEY_HOUSENUMBER,homeNumber);
        UserInfo userInfo = null;
        if(userId==null||Long.parseLong(userId.toString())<=0){
            //门牌号与账号之间的对应关系再缓存中不存在  查询数据库
            String houseArray[] = homeNumber.split("_");
            userInfo = userInfoService.findUserByHouseNumber(Integer.parseInt(houseArray[0]),houseArray[1]);
            if(userInfo==null){
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"将要绑定的账号不存在",new JSONObject());
            }
        }else{
            Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId.toString() );
            if(userMap==null||userMap.size()<=0){
                //缓存中没有用户对象信息 查询数据库
                userInfo = userInfoService.findUserById(Long.parseLong(userId.toString()));
                if(userInfo==null){//数据库也没有
                    return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"将要绑定的账号不存在",new JSONObject());
                }
            }else{
                userInfo = (UserInfo) CommonUtils.mapToObject(userMap,UserInfo.class);
            }
        }
        if(userInfo.getAccountStatus()!=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您将要绑定门牌号账号状态不正常，被绑定账号必须是已激活账号",new JSONObject());

        }
        //添加暴力密码限制
        String errorCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,CommonUtils.getMyId()+""));
        if(!CommonUtils.checkFull(errorCount)&&Integer.parseInt(errorCount)>100){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_PASSWORD_ERROR_TOO_MUCH.CODE_VALUE,"您输入的绑定门牌号对应密码错误次数过多，系统已自动封号一天，如有疑问请联系官方客服",new JSONObject());
        }
        //验证密码是否正确
        if(!password.equals(userInfo.getPassword())){
            if(CommonUtils.checkFull(errorCount)){//第一次错误
                redisUtils.hset(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,CommonUtils.getMyId()+"",1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr(Constants.REDIS_KEY_LOGIN_ERROR_COUNT,CommonUtils.getMyId()+"",1);
            }
            return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE,"密码错误",new JSONObject());
        }
        //开始绑定
        if(bindType==1){//第三方平台账号绑定门牌号
            //验证被绑定账号是否已绑定过第三方平台账号或手机号
            if(!CommonUtils.checkFull(userInfo.getOtherPlatformKey())){
                return returnData(StatusCode.CODE_HOUSENUMBER_IS_EXIST_CODE_ERROR.CODE_VALUE,"您将要绑定门牌号账号[\"+homeNumber+\"]已绑定其他平台账号,请更换门牌号或者解绑！",new JSONObject());
            }
            //当前登录账号将会被停用
            redisUtils.hdel(Constants.REDIS_KEY_OTHERNUMBER, myUserInfo.getOtherPlatformType() + "_" + myUserInfo.getOtherPlatformKey());
            redisUtils.expire(Constants.REDIS_KEY_USER+CommonUtils.getMyId(),0);
            userInfoService.delete(myUserInfo);
            //目标账号将会被绑定
            userInfo.setOtherPlatformType(otherPlatformType);
            userInfo.setOtherPlatformKey(otherPlatformKey);
            userInfo.setOtherPlatformAccount(otherPlatformAccount);
            userInfoService.updateBindOther(userInfo);
            //更新安全中心绑定信息
            //修改旧账号绑定信息
            mqUtils.sendUserAccountSecurityMQ(myUserInfo.getUserId(),null,0,null,null);
            //更新目标账号
            mqUtils.sendUserAccountSecurityMQ(userInfo.getUserId(),userInfo.getPhone(),userInfo.getOtherPlatformType(),userInfo.getOtherPlatformAccount(),userInfo.getOtherPlatformKey());
        }else{//手机号绑定门牌号
            //验证被绑定账号是否已绑定过手机号
            if(!CommonUtils.checkFull(userInfo.getPhone())){
                return returnData(StatusCode.CODE_HOUSENUMBER_IS_EXIST_CODE_ERROR.CODE_VALUE,"您将要绑定门牌号账号[\"+homeNumber+\"]已绑定其他手机号,请更换门牌号或者解绑！",new JSONObject());
            }
            //当前登录账号将会被停用
            redisUtils.hdel(Constants.REDIS_KEY_PHONENUMBER,myUserInfo.getPhone());
            redisUtils.expire(Constants.REDIS_KEY_USER+CommonUtils.getMyId(),0);
            userInfoService.delete(myUserInfo);
            //目标账号将会被绑定
            userInfo.setPhone(otherPlatformKey);
            userInfoService.updateBindPhone(userInfo);
            //更新安全中心绑定信息
            //修改旧账号绑定信息
            mqUtils.sendUserAccountSecurityMQ(myUserInfo.getUserId(),null,0,null,null);
            //更新目标账号
            mqUtils.sendUserAccountSecurityMQ(userInfo.getUserId(),userInfo.getPhone(),userInfo.getOtherPlatformType(),userInfo.getOtherPlatformAccount(),userInfo.getOtherPlatformKey());
        }
        redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),CommonUtils.objectToMap(userInfo),Constants.USER_TIME_OUT);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
    /***
     * 创建VIP账号、靓号、普通账号等预选账号接口（仅管理员可用）
     * @param userInfo
     * @return
     */
    @Override
    public ReturnData createVIPHouseNumber(@Valid @RequestBody UserInfo userInfo, BindingResult bindingResult) {
        //验证用户身份
        if(CommonUtils.getMyId()!=10076){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您无权限进行此操作",new JSONObject());
        }
        //验证地区正确性
        if(!CommonUtils.checkProvince_city_district(userInfo.getCountry(),userInfo.getProvince(),userInfo.getCity(),userInfo.getDistrict())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"国家、省、市、区参数不匹配",new JSONObject());
        }
        //验证不能为空的参数
        if(CommonUtils.checkFull(userInfo.getName())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"用户名不能为空",new JSONObject());
        }
        if(CommonUtils.checkFull(userInfo.getPassword())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"密码不能为空",new JSONObject());
        }
//        if(CommonUtils.checkFull(userInfo.getCode())){
//            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"验证码不能为空",new JSONObject());
//        }
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //检测门牌号是否可用
        UserInfo u = userInfoService.findUserByHouseNumber(userInfo.getProType(),userInfo.getHouseNumber()+"");
        if(u!=null){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"账号已存在",new JSONObject());
        }
        //开始注册
        UserInfo newUserInfo = new UserInfo();
        newUserInfo.setName(userInfo.getName());
        newUserInfo.setPassword(userInfo.getPassword());
        newUserInfo.setIm_password(CommonUtils.strToMD5(userInfo.getPassword(),32));//环信密码为两遍MD5
        newUserInfo.setSex(userInfo.getSex());
        newUserInfo.setBirthday(userInfo.getBirthday());
        newUserInfo.setCountry(userInfo.getCountry());
        newUserInfo.setProvince(userInfo.getProvince());
        newUserInfo.setCity(userInfo.getCity());
        newUserInfo.setDistrict(userInfo.getDistrict());
        newUserInfo.setIdCard(userInfo.getIdCard());
        newUserInfo.setUser_ce(userInfo.getUser_ce());//设置是否为VIP
        newUserInfo.setTime(new Date());
        newUserInfo.setAccessRights(1);
        newUserInfo.setProType(Constants.PRO_INFO_ARRAY[userInfo.getProvince()]);
        newUserInfo.setHouseNumber(userInfo.getHouseNumber());
        //生成默认头像
        Random random = new Random();
        newUserInfo.setHead("image/head/defaultHead/defaultHead_"+random.nextInt(20)+"_225x225.jpg");
        //检测是否为靓号
        if(CommonUtils.isPretty(userInfo.getHouseNumber())){
            newUserInfo.setIsGoodNumber(1);//设为靓号
        }
        if(newUserInfo.getIsGoodNumber()==1||newUserInfo.getUser_ce()==1){
            //更新靓号记录表
            PickNumber pickNumber = new PickNumber();
            pickNumber.setHouseNumber(newUserInfo.getHouseNumber());
            pickNumber.setIsGoodNumber(newUserInfo.getIsGoodNumber());
            pickNumber.setIsVipNumber(newUserInfo.getUser_ce());
            pickNumber.setProId(newUserInfo.getProType());
            pickNumber.setTime(new Date());
            pickNumberNewService.add(pickNumber);
            //更新靓号缓存记录
            String pickNumberValue = pickNumber.getProId()+"_"+pickNumber.getHouseNumber();
            redisUtils.hset("pickNumberMap",pickNumberValue,"1");
        }
        //写入数据库
        userInfoService.add(newUserInfo);
        //调用activeMQ消息系统 同步门牌号记录表
        JSONObject root = new JSONObject();
        JSONObject header = new JSONObject();
        header.put("interfaceType", "2");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发 3表示用户登录时同步登录信息 4表示新增访问量
        JSONObject content = new JSONObject();
        content.put("proType",newUserInfo.getProType() );
        content.put("houseNumber",newUserInfo.getHouseNumber() );
        root.put("header", header);
        root.put("content", content);
        String sendMsg = root.toJSONString();
        ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
        mqProducer.sendMsg(activeMQQueue,sendMsg);
        //同步环信 由于环信服务端接口限流每秒30次 所以此操作改到客户端完成 拼接注册环信需要的参数 返回给客户端 环信账号改成用户ID
        Map<String,String> im_map = new HashMap<>();
        im_map.put("proType",newUserInfo.getProType()+"");
        im_map.put("houseNumber",newUserInfo.getHouseNumber()+"");
        im_map.put("myId",newUserInfo.getUserId()+"");
        im_map.put("password",newUserInfo.getIm_password());//环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",im_map);
    }

}
