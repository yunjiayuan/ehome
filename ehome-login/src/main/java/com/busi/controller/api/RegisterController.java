package com.busi.controller.api;

import com.busi.entity.GraffitiChartLog;
import com.busi.entity.UserInfo;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.VisitView;
import com.busi.mq.MqProducer;
import com.busi.service.GraffitiChartLogService;
import com.busi.service.UserInfoService;
import com.busi.service.VisitViewService;
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
    VisitViewService visitViewService;

    @Autowired
    GraffitiChartLogService graffitiChartLogService;

    @Autowired
    MqProducer mqProducer;

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
            JSONObject root = new JSONObject();
            JSONObject header = new JSONObject();
            header.put("interfaceType", "0");//interfaceType 0 表示发送手机短信  1表示发送邮件  2表示新用户注册转发
            JSONObject content = new JSONObject();
            content.put("phone",phone);//将要发送短信的手机号
            content.put("phoneType",0);//短信类型 0表示注册时发送短信
            content.put("phoneCode",code);//短信验证码
            root.put("header", header);
            root.put("content", content);
            String sendMsg = root.toJSONString();
            ActiveMQQueue activeMQQueue = new ActiveMQQueue(Constants.MSG_REGISTER_MQ);
            mqProducer.sendMsg(activeMQQueue,sendMsg);
            //将验证码存入缓存 后边注册时使用
            redisUtils.set(Constants.REDIS_KEY_REG_TOKEN+regToken,phone+"_"+code,60*10);//验证码10分钟内有效
            code = "";//手机验证码不返回客户端
        }else{
            //将验证码存入缓存 后边注册时使用
            redisUtils.set(Constants.REDIS_KEY_REG_TOKEN+regToken,code,60*10);//验证码10分钟内有效
        }
        //添加限流机制 每个设备每天最多只能调用50次
        String requestCount = String.valueOf(redisUtils.hget(Constants.REDIS_KEY_REGISTER_CREATECODE_COUNT,clientId+""));
        if(!CommonUtils.checkFull(requestCount)&&Integer.parseInt(requestCount)>50){//大于100次 今天该账号禁止访问
            return returnData(StatusCode.CODE_REQUEST_ERROR_COUNT.CODE_VALUE,"您今天获取的验证码次数过多，系统已自动禁止该设备使用一天，如有疑问请联系官方客服",new JSONObject());
        }
        if(CommonUtils.checkFull(requestCount)){//第一次
            redisUtils.hset(Constants.REDIS_KEY_REGISTER_CREATECODE_COUNT,clientId+"",1,24*60*60);//设置1天后失效
        }else{
            redisUtils.hashIncr(Constants.REDIS_KEY_REGISTER_CREATECODE_COUNT,clientId+"",1);//自增1
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
        //更新缓存中 手机号与用户ID 的对应关系表
//        redisUtils.hset(Constants.REDIS_KEY_PHONENUMBER,newUserInfo.getPhone(),newUserInfo.getUserId());//重新登录时 会重新加载到缓存中 此处不再同步
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
            if(userInfo==null){
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"将要访问串门的用户不存在",new JSONObject());
            }
            //设置访问量信息
            Object obj = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+userId);
            VisitView visitView = null;
            if(obj==null){//缓存中不存在 查询数据库
                visitView = visitViewService.findVisitView(userId);
                if(visitView==null){
                    visitView = new VisitView();
                    visitView.setUserId(userId);
                }
                userInfo.setTodayVisitCount(visitView.getTodayVisitCount());//设置今日访问量
                userInfo.setTotalVisitCount(visitView.getTotalVisitCount());//设置总访问量
                //更新缓存
                redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(visitView),Constants.USER_TIME_OUT);//7天 此对象只是用来做过期处理的判断 里面参数内容不是准确的
                redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+visitView.getUserId(),visitView.getTotalVisitCount(),CommonUtils.getCurrentTimeTo_12());//更新今日访问量的生命周期 到今天晚上12点失效
                redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+visitView.getUserId(),visitView.getTodayVisitCount(),Constants.USER_TIME_OUT);
            }else{//缓存中存在
                Object todayObj = redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+userId);
                if(todayObj==null){//处理当天访问量失效的问题
                    userInfo.setTodayVisitCount(0);//设置今日访问量
                    //更新缓存 重置今天访问量0
                    redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+visitView.getUserId(),0,CommonUtils.getCurrentTimeTo_12());//更新今日访问量的生命周期 到今天晚上12点失效
                }else{
                    userInfo.setTodayVisitCount(Long.parseLong(redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+userId).toString()));//设置今日访问量
                }
                userInfo.setTotalVisitCount(Long.parseLong(redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+userId).toString()));//设置总访问量
            }
            userInfo.setPassword("");//过滤登录密码
            userInfo.setIm_password("");//过滤环信密码
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userInfo);
        }
        //设置访问量信息
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_VISIT+userId);
        VisitView visitView = null;
        if(map==null||map.size()<=0){//缓存中不存在 查询数据库
            visitView = visitViewService.findVisitView(userId);
            if(visitView==null){
                visitView = new VisitView();
                visitView.setUserId(userId);
            }
            userMap.put("todayVisitCount",visitView.getTodayVisitCount());//设置今日访问量
            userMap.put("totalVisitCount",visitView.getTotalVisitCount());//设置总访问量
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(visitView),Constants.USER_TIME_OUT);//7天 此对象只是用来做过期处理的判断 里面参数内容不是准确的
            redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+visitView.getUserId(),visitView.getTotalVisitCount(),Constants.USER_TIME_OUT);//更新今日访问量的生命周期 到今天晚上12点失效
            redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+visitView.getUserId(),visitView.getTodayVisitCount(),CommonUtils.getCurrentTimeTo_12());
        }else{//缓存中存在
            Object todayObj = redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+userId);
            if(todayObj==null){//处理当天访问量失效的问题
                userMap.put("todayVisitCount",0);//设置今日访问量
                //更新缓存 重置今天访问量0
                redisUtils.hset(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+userId,0,CommonUtils.getCurrentTimeTo_12());//更新今日访问量的生命周期 到今天晚上12点失效
            }else{
                userMap.put("todayVisitCount",Long.parseLong(redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TODAY_COUNT,"today_"+userId).toString()));//设置今日访问量
            }
            userMap.put("totalVisitCount",Long.parseLong(redisUtils.hget(Constants.REDIS_KEY_USER_VISIT_TOTAL_COUNT,"total_"+userId).toString()));//设置总访问量
        }
        userMap.put("password","");//过滤登录密码
        userMap.put("im_password","");//过滤环信密码
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userMap);
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
        }
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
        }
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
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

//    @Override
//    public ReturnData testFegin(@PathVariable Integer id) {
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"nihao",new JSONObject());
//    }
}
