package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.RealNameInfo;
import com.busi.entity.ReturnData;
import com.busi.entity.UserAccountSecurity;
import com.busi.entity.UserInfo;
import com.busi.fegin.UserInfoLocalControllerFegin;
import com.busi.service.RealNameInfoService;
import com.busi.service.UserAccountSecurityService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户账户安全接口
 * author：SunTianJie
 * create time：2018/9/17 15:07
 */
@RestController
public class UserAccountSecurityController extends BaseController implements UserAccountSecurityApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserAccountSecurityService userAccountSecurityService;

    @Autowired
    RealNameInfoService realNameInfoService;

    @Autowired
    UserInfoLocalControllerFegin userInfoLocalControllerFegin;

    /***
     * 查询安全中心数据接口
     * @param userId
     * @return
     */
    @Override
    public ReturnData findUserAccountSecurity(@PathVariable long userId) {
        if (userId <= 0) {//参数有误
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (userAccountSecurity == null) {
                //之前该用户未设置过安全中心数据
                userAccountSecurity = new UserAccountSecurity();
                userAccountSecurity.setUserId(userId);
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "账号有误，请重新登录后，再进行此操作", new JSONObject());
        }
        String bindPhone = "";// 绑定的手机号码
        String bindEmail = "";// 绑定的邮箱
        int securityQuestionStatus = 0;// 0密保问题未设置 1已设置
        int realNameStatus = 0;// 0实名未认证 1已认证
        int otherPlatformType = 0;//是否绑定第三方平台账号，0：未绑定, 1：绑定QQ账号，2：绑定微信账号，3：绑定新浪微博账号
        String otherPlatformAccount = "";//第三方平台账号名称
        int grade = 1;//安全等级 1为危险  2为一般 3为比较安全 4为安全 5非常安全
        int score = 0;//安全分数 安全等级根据分数计算获得 0分为危险 30分以下为一般  40-50分为比较安全 60以上为安全 100分为非常安全
        if (userAccountSecurity != null) {
            if (!CommonUtils.checkFull(userAccountSecurity.getPhone())) {
                bindPhone = userAccountSecurity.getPhone();
                score += 30;
            }
            if (!CommonUtils.checkFull(userAccountSecurity.getEmail())) {
                bindEmail = userAccountSecurity.getEmail();
                score += 15;
            }
            if (!CommonUtils.checkFull(userAccountSecurity.getSecurityQuestion())) {
                securityQuestionStatus = 1;//已设置密保问题
                score += 15;
            }
            if (!CommonUtils.checkFull(userAccountSecurity.getIdCard())
                    && !CommonUtils.checkFull(userAccountSecurity.getRealName())) {
                realNameStatus = 1;//已认证
                score += 30;
            }
            if (userAccountSecurity.getOtherPlatformType() > 0) {//已绑定第三方平台账号
                otherPlatformType = userAccountSecurity.getOtherPlatformType();
                otherPlatformAccount = userAccountSecurity.getOtherPlatformAccount();
                score += 10;
            }
        }
        //开始计算安全等级
        if (score <= 0) {
            grade = 1;
        } else if (score > 0 && score <= 30) {
            grade = 2;
        } else if (score > 30 && score < 60) {
            grade = 3;
        } else if (score >= 60 && score < 100) {
            grade = 4;
        } else if (score >= 100) {
            grade = 5;
        }
        Map<String, Object> userAccountSecurityMap = new HashMap<>();
        userAccountSecurityMap.put("grade", grade);
        userAccountSecurityMap.put("bindPhone", bindPhone);
        userAccountSecurityMap.put("bindEmail", bindEmail);
        userAccountSecurityMap.put("securityQuestionStatus", securityQuestionStatus);
        userAccountSecurityMap.put("realNameStatus", realNameStatus);
        userAccountSecurityMap.put("otherPlatformType", otherPlatformType);
        userAccountSecurityMap.put("otherPlatformAccount", otherPlatformAccount);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", userAccountSecurityMap);
    }

    /***
     * 绑定手机前，验证新手机号是否被占用接口
     * @param phone
     * @return
     */
    @Override
    public ReturnData checkNewPhone(@PathVariable String phone) {
        //验证参数
        if (CommonUtils.checkFull(phone) || !CommonUtils.checkPhone(phone)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "phone参数有误", new JSONObject());
        }
        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByPhone(phone);
        int isTrue = 0;//是否正确，0表示占用，1表示可用
        if (userAccountSecurity == null) {
            isTrue = 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("isTrue", isTrue);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 短信验证修改密码前，验证短信验证码是否正确接口
     * @param code
     * @param checkType 0修改密码验证旧手机 1找回密码验证旧手机
     * @return
     */
    @Override
    public ReturnData checkPhoneCode(@PathVariable String code, @PathVariable int checkType) {
        //验证参数
        if (CommonUtils.checkFull(code)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "验证码不能为空", new JSONObject());
        }
        if (checkType < 0 || checkType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "验证码不能为空", new JSONObject());
        }
        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(CommonUtils.getMyId());
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "当前账户并未绑定手机", new JSONObject());
        }
        Object serverCode = null;
        if (checkType == 1) {//找回密码验证旧手机
            serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_FINDPASSWORD_CODE + CommonUtils.getMyId() + "_" + userAccountSecurity.getPhone());
        } else {//修改密码验证旧手机
            serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_CHANGEPASSWORD_CODE + CommonUtils.getMyId() + "_" + userAccountSecurity.getPhone());
        }
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        //判断验证码是否正确
        if (!serverCode.toString().equals(code)) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        String key = CommonUtils.strToMD5(CommonUtils.getMyId() + CommonUtils.getClientId() + System.currentTimeMillis() + CommonUtils.getRandom(6, 0), 16);//临时key 用于修改密码
        map.put("key", key);
        redisUtils.set(Constants.REDIS_KEY_USER_CHANGE_PASSWORD_KEY + CommonUtils.getMyId(), key, Constants.MSG_TIME_OUT_MINUTE_10);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 绑定手机号接口
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData bindNewPhone(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数格式
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的安全中心信息", new JSONObject());
        }
        //验证验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getPhone());
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        if (!serverCode.toString().equals(userAccountSecurity.getCode())) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        //验证该手机是否被绑定过
        UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByPhone(userAccountSecurity.getPhone());
        if (uas != null) {//已存在
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该手机号已被其他账户绑定，请更换其他的手机号再进行绑定", new JSONObject());
        }
        //判断该账户是否未绑定手机号
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            UserAccountSecurity uass = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uass == null) {
                //之前该用户未设置过安全中心数据 新增
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {//更新
                uass.setPhone(userAccountSecurity.getPhone());
                userAccountSecurityService.updateUserAccountSecurity(uass);
            }
        } else {
            if (Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                //之前该用户未设置过权限信息 新增
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {//更新
                UserAccountSecurity uass = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
                if (uass != null) {
                    uass.setPhone(userAccountSecurity.getPhone());
                    userAccountSecurityService.updateUserAccountSecurity(uass);
                }
            }
        }
        //同步用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userAccountSecurity.getUserId());
        userInfo.setPhone(userAccountSecurity.getPhone());
        userInfoLocalControllerFegin.updateBindPhone(userInfo);
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        //清除短信验证码
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getPhone(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 解绑手机号
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData unBindPhone(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数格式
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的安全中心信息", new JSONObject());
        }
        //验证验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getPhone());
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        if (!serverCode.toString().equals(userAccountSecurity.getCode())) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        //判断该账户绑定手机号情况
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            UserAccountSecurity uass = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uass == null) {
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未绑定过手机，无法解绑", new JSONObject());
            } else {
                if (!uass.getPhone().equals(userAccountSecurity.getPhone())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "解绑手机号不正确，解绑失败", new JSONObject());
                }
                userAccountSecurity.setPhone("");
                userAccountSecurityService.updateUserAccountSecurity(userAccountSecurity);
            }
        } else {
            if (Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未绑定过手机，无法解绑", new JSONObject());
            } else {
                if (!userAccountSecurityMap.get("phone").toString().equals(userAccountSecurity.getPhone())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "解绑手机号不正确，解绑失败", new JSONObject());
                }
                UserAccountSecurity uass = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
                if (uass != null) {
                    uass.setPhone("");
                    userAccountSecurityService.updateUserAccountSecurity(uass);
                }
            }
        }
        //同步用户信息 置空phone
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userAccountSecurity.getUserId());
        userInfo.setPhone("");
        userInfoLocalControllerFegin.updateBindPhone(userInfo);
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        //清除短信验证码
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getPhone(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询密保问题信息接口
     * @param userId
     * @return
     */
    @Override
    public ReturnData findQuestion(@PathVariable long userId) {
        if (userId <= 0) {//参数有误
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "userId参数有误", new JSONObject());
        }
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId);
        if (map == null || map.size() <= 0) {
            UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(userId);
            if (userAccountSecurity == null) {
                //之前该用户未设置过安全中心数据
                userAccountSecurity = new UserAccountSecurity();
                userAccountSecurity.setUserId(userId);
            } else {
                userAccountSecurity.setRedisStatus(1);//数据库中已有记录
            }
            //放到缓存中
            map = CommonUtils.objectToMap(userAccountSecurity);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userId, map, Constants.USER_TIME_OUT);
        }
        UserAccountSecurity userAccountSecurity = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "账号有误，请重新登录后，再进行此操作", new JSONObject());
        }
        String sQuestion = userAccountSecurity.getSecurityQuestion();
        String questions = "";//只返回密保问题 不返回答案 格式为 ：1,2,5   问题编号“,”分隔
        if (!CommonUtils.checkFull(sQuestion)) {
            String[] sqArray = sQuestion.split(";");
            if (sqArray != null && sqArray.length > 0) {
                for (int i = 0; i < sqArray.length; i++) {
                    String[] questionArray = sqArray[i].split(",");
                    if (i == sqArray.length - 1) {
                        questions += questionArray[0];
                    } else {
                        questions += questionArray[0] + ",";
                    }
                }
            }
        }
        Map<String, Object> userAccountSecurityMap = new HashMap<>();
        userAccountSecurityMap.put("securityQuestion", questions);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", userAccountSecurityMap);
    }

    /***
     * 验证密保问题信息
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData checkQuestion(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数
        if (CommonUtils.checkFull(userAccountSecurity.getSecurityQuestion())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "securityQuestion参数有误", new JSONObject());
        }
        //判断验证人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的安全中心信息", new JSONObject());
        }
        int isTrue = 0;//是否正确，0表示错误，1表示正确
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uas == null) {
                //之前该用户未设置过安全中心数据
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未设置过密保信息，无法验证", new JSONObject());
            } else {
                if (CommonUtils.checkFull(uas.getSecurityQuestion())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未设置过密保信息，无法验证", new JSONObject());
                }
                if (uas.getSecurityQuestion().equals(userAccountSecurity.getSecurityQuestion())) {
                    isTrue = 1;//验证正确
                }
            }
        } else {
            if (Integer.parseInt(map.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未设置过密保信息，无法验证", new JSONObject());
            } else {
                if (map.get("securityQuestion") == null) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未设置过密保信息，无法验证", new JSONObject());
                }
                if (userAccountSecurity.getSecurityQuestion().equals(map.get("securityQuestion").toString())) {
                    isTrue = 1;//验证正确
                }
            }
        }
        Map<String, Object> userAccountSecurityMap = new HashMap<>();
        userAccountSecurityMap.put("isTrue", isTrue);
        String key = CommonUtils.strToMD5(CommonUtils.getMyId() + CommonUtils.getClientId() + System.currentTimeMillis() + CommonUtils.getRandom(6, 0), 16);//临时key 用于修改密码
        userAccountSecurityMap.put("key", key);
        redisUtils.set(Constants.REDIS_KEY_USER_CHANGE_PASSWORD_KEY + CommonUtils.getMyId(), key, Constants.MSG_TIME_OUT_MINUTE_10);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", userAccountSecurityMap);
    }

    /***
     * 设置和修改密保问题信息
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData addQuestion(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getSecurityQuestion())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "securityQuestion参数有误", new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的密保问题信息", new JSONObject());
        }
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uas == null) {
                //之前该用户未设置过安全中心数据
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {
                uas.setSecurityQuestion(userAccountSecurity.getSecurityQuestion());
                userAccountSecurityService.updateUserAccountSecurity(uas);//数据库中已有记录
            }
        } else {
            if (Integer.parseInt(map.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {
                UserAccountSecurity uas = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
                uas.setSecurityQuestion(userAccountSecurity.getSecurityQuestion());
                userAccountSecurityService.updateUserAccountSecurity(uas);
            }
        }
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 实名认证接口
     * @param realNameInfo
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData checkRealName(@Valid @RequestBody RealNameInfo realNameInfo, BindingResult bindingResult) {
        //验证参数
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //开始验证实名信息
        List<RealNameInfo> list = null;
        //查本地库中是否存在该实名信息
        list = realNameInfoService.findRealNameInfo(realNameInfo.getRealName(), realNameInfo.getCardNo());
        RealNameInfo rni = null;
        if (list != null && list.size() > 0) {//存在
            boolean flag = false;
            for (int i = 0; i < list.size(); i++) {
                rni = list.get(i);
                if (rni != null) {
                    if (rni.getUserId() == CommonUtils.getMyId()) {//过滤重复实名
                        flag = true;
                        break;
                    }
                }
            }
            if (!flag) {
                //新增实名记录
                rni.setId(0);//置空主键
                rni.setUserId(CommonUtils.getMyId());
                rni.setTime(new Date());
                realNameInfoService.addRealNameInfo(rni);
            }
        } else {
            //本地中不存在 远程调用第三方平台认证
            rni = RealNameUtils.checkRealName(CommonUtils.getMyId(), realNameInfo.getRealName(), realNameInfo.getCardNo());
            if (rni != null) {
                realNameInfoService.addRealNameInfo(rni);
            } else {
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "认证失败，请填写您本人正确的身份信息", new JSONObject());
            }
        }
        //更新安全中心记录表
        Map<String, Object> map = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + realNameInfo.getUserId());
        if (map == null || map.size() <= 0) {
            UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByUserId(realNameInfo.getUserId());
            if (uas == null) {
                //之前该用户未设置过安全中心数据
                UserAccountSecurity userAccountSecurity = new UserAccountSecurity();
                userAccountSecurity.setUserId(realNameInfo.getUserId());
                userAccountSecurity.setRealName(realNameInfo.getRealName());
                userAccountSecurity.setIdCard(realNameInfo.getCardNo());
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {
                uas.setRealName(realNameInfo.getRealName());
                uas.setIdCard(realNameInfo.getCardNo());
                userAccountSecurityService.updateUserAccountSecurity(uas);//数据库中已有记录
            }
        } else {
            if (Integer.parseInt(map.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                UserAccountSecurity userAccountSecurity = new UserAccountSecurity();
                userAccountSecurity.setUserId(realNameInfo.getUserId());
                userAccountSecurity.setRealName(realNameInfo.getRealName());
                userAccountSecurity.setIdCard(realNameInfo.getCardNo());
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {
                UserAccountSecurity uas = (UserAccountSecurity) CommonUtils.mapToObject(map, UserAccountSecurity.class);
                uas.setRealName(realNameInfo.getRealName());
                uas.setIdCard(realNameInfo.getCardNo());
                userAccountSecurityService.updateUserAccountSecurity(uas);
            }
        }
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + realNameInfo.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }


    /***
     * 查询当前用户的实名信息
     * @return
     */
    @Override
    public ReturnData findRealName() {
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + CommonUtils.getMyId());
        UserAccountSecurity userAccountSecurity = new UserAccountSecurity();
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            //缓存中没有用户对象信息 查询数据库
            UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByUserId(CommonUtils.getMyId());
            if (uas == null) {//数据库也没有
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", userAccountSecurity);
            }
            uas.setRedisStatus(1);
            userAccountSecurityMap = CommonUtils.objectToMap(uas);
            redisUtils.hmset(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY +  CommonUtils.getMyId(), userAccountSecurityMap, Constants.USER_TIME_OUT);
            userAccountSecurity.setUserId(CommonUtils.getMyId());
            userAccountSecurity.setRealName(uas.getRealName());
            userAccountSecurity.setIdCard(uas.getIdCard());
        }else{
            UserAccountSecurity uas = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
            if(uas!=null){
                userAccountSecurity.setUserId(CommonUtils.getMyId());
                userAccountSecurity.setRealName(uas.getRealName());
                userAccountSecurity.setIdCard(uas.getIdCard());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", userAccountSecurity);
    }

    /***
     * 门牌号绑定微信、QQ、新浪微博等第三方平台账号
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData bindHouseNumber(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getOtherPlatformKey())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，otherPlatformKey不能为空", new JSONObject());
        }
        if (CommonUtils.checkFull(userAccountSecurity.getOtherPlatformAccount())) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，otherPlatformAccount不能为空", new JSONObject());
        }
        //处理特殊字符  20190107  ZHJJ
        String name = userAccountSecurity.getOtherPlatformAccount();
        if (!CommonUtils.checkFull(name)) {
            String filteringTitle = CommonUtils.filteringContent(name);
            if (!CommonUtils.checkFull(filteringTitle)) {
                 userAccountSecurity.setOtherPlatformAccount(filteringTitle);
            }
        }
        //验证当前第三方平台账户是否被其他账户绑定过
        UserAccountSecurity uasy = userAccountSecurityService.findUserAccountSecurityByOther(userAccountSecurity.getOtherPlatformType(), userAccountSecurity.getOtherPlatformKey());
        if (uasy != null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该第三方平台账户已被其他账户绑定过", new JSONObject());
        }
        //验证当前用户是否已绑定过
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            UserAccountSecurity uass = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uass != null) {
                if (!CommonUtils.checkFull(uass.getOtherPlatformKey())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "绑定第三方平台账户失败，您绑定过了", new JSONObject());
                }
                //开始绑定
                uass.setOtherPlatformAccount(userAccountSecurity.getOtherPlatformAccount());
                uass.setOtherPlatformKey(userAccountSecurity.getOtherPlatformKey());
                uass.setOtherPlatformType(userAccountSecurity.getOtherPlatformType());
                userAccountSecurityService.updateUserAccountSecurity(uass);
            } else {
                //开始绑定
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            }
        } else {
            if (Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString()) == 1) {//redisStatus==1 说明数据中已有记录
                //开始绑定
                UserAccountSecurity uas = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
                if (uas == null) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "账号有误，请重新登录后，再进行此操作", new JSONObject());
                }
                if (!CommonUtils.checkFull(uas.getOtherPlatformKey())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "很抱歉,绑定第三方平台账户失败,您已经绑定过了", new JSONObject());
                }
                uas.setOtherPlatformKey(userAccountSecurity.getOtherPlatformKey());
                uas.setOtherPlatformType(userAccountSecurity.getOtherPlatformType());
                uas.setOtherPlatformAccount(userAccountSecurity.getOtherPlatformAccount());
                userAccountSecurityService.updateUserAccountSecurity(uas);
            } else {
                //开始绑定
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            }
        }
        //同步用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userAccountSecurity.getUserId());
        userInfo.setOtherPlatformKey(userAccountSecurity.getOtherPlatformKey());
        userInfo.setOtherPlatformAccount(userAccountSecurity.getOtherPlatformAccount());
        userInfo.setOtherPlatformType(userAccountSecurity.getOtherPlatformType());
        userInfoLocalControllerFegin.updateBindOther(userInfo);
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 解除门牌号与第三方平台账号（微信、QQ、新浪微博）之间的绑定关系
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData unBindHouseNumber(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByOther(userAccountSecurity.getOtherPlatformType(), userAccountSecurity.getOtherPlatformKey());
        if (uas == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该第三方平台账户尚未被其他账户绑定过，无法解绑", new JSONObject());
        }
        uas.setOtherPlatformType(0);
        uas.setOtherPlatformAccount("");
        uas.setOtherPlatformKey("");
        userAccountSecurityService.updateUserAccountSecurity(uas);
        //同步用户信息
        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(userAccountSecurity.getUserId());
        userInfo.setOtherPlatformKey("");
        userInfo.setOtherPlatformAccount("");
        userInfo.setOtherPlatformType(0);
        userInfoLocalControllerFegin.updateBindOther(userInfo);
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 绑定邮箱前，验证新邮箱是否被占用接口
     * @param email
     * @return
     */
    @Override
    public ReturnData checkNewEmail(@PathVariable String email) {
        //验证参数
        if (CommonUtils.checkFull(email) || !CommonUtils.checkEmail(email)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "phone参数有误", new JSONObject());
        }
        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByEmail(email);
        int isTrue = 0;//是否正确，0表示占用，1表示可用
        if (userAccountSecurity == null) {
            isTrue = 1;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("isTrue", isTrue);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 邮箱验证修改密码前，验证邮箱验证码是否正确接口
     * @param code
     * @param checkType
     * @return
     */
    @Override
    public ReturnData checkEmailCode(@PathVariable String code, @PathVariable int checkType) {
        //验证参数
        if (CommonUtils.checkFull(code)) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "验证码不能为空", new JSONObject());
        }
        if (checkType < 0 || checkType > 1) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "验证码不能为空", new JSONObject());
        }
        UserAccountSecurity userAccountSecurity = userAccountSecurityService.findUserAccountSecurityByUserId(CommonUtils.getMyId());
        if (userAccountSecurity == null) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "当前账户并未绑定邮箱", new JSONObject());
        }
        Object serverCode = null;
        if (checkType == 1) {//找回密码验证旧邮箱
            serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_FINDPASSWORD_EMAIL_CODE + CommonUtils.getMyId() + "_" + userAccountSecurity.getEmail());
        } else {//修改密码验证旧邮箱
            serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_CHANGEPASSWORD_EMAIL_CODE + CommonUtils.getMyId() + "_" + userAccountSecurity.getEmail());
        }
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        //判断验证码是否正确
        if (!serverCode.toString().equals(code)) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        Map<String, Object> map = new HashMap<>();
        String key = CommonUtils.strToMD5(CommonUtils.getMyId() + CommonUtils.getClientId() + System.currentTimeMillis() + CommonUtils.getRandom(6, 0), 16);//临时key 用于修改密码
        map.put("key", key);
        redisUtils.set(Constants.REDIS_KEY_USER_CHANGE_PASSWORD_KEY + CommonUtils.getMyId(), key, Constants.MSG_TIME_OUT_MINUTE_10);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 绑定邮箱接口
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData bindNewEmail(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数格式
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的安全中心信息", new JSONObject());
        }
        //验证验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_EMAIL_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getEmail());
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        if (!serverCode.toString().equals(userAccountSecurity.getCode())) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        //验证该邮箱是否被绑定过
        UserAccountSecurity uas = userAccountSecurityService.findUserAccountSecurityByEmail(userAccountSecurity.getEmail());
        if (uas != null) {//已存在
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该邮箱已被其他账户绑定，请更换其他的手机号再进行绑定", new JSONObject());
        }
        //判断该账户是否未绑定邮箱
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            UserAccountSecurity uass = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uass == null) {
                //之前该用户未设置过安全中心数据 新增
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {//更新
                uass.setEmail(userAccountSecurity.getEmail());
                userAccountSecurityService.updateUserAccountSecurity(uass);
            }
        } else {
            if (Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                //之前该用户未设置过权限信息 新增
                userAccountSecurityService.addUserAccountSecurity(userAccountSecurity);
            } else {//更新
                UserAccountSecurity uass = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
                if (uass != null) {
                    uass.setEmail(userAccountSecurity.getEmail());
                    userAccountSecurityService.updateUserAccountSecurity(uass);
                }
            }
        }
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        //清除验证码
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_BIND_EMAIL_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getEmail(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 解绑邮箱
     * @param userAccountSecurity
     * @return
     */
    @Override
    public ReturnData unBindEmail(@Valid @RequestBody UserAccountSecurity userAccountSecurity, BindingResult bindingResult) {
        //验证参数格式
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != userAccountSecurity.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userAccountSecurity.getUserId() + "]的安全中心信息", new JSONObject());
        }
        //验证验证码是否正确
        Object serverCode = redisUtils.getKey(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_EMAIL_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getEmail());
        if (serverCode == null) {
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该验证码已过期,请重新获取", new JSONObject());
        }
        if (!serverCode.toString().equals(userAccountSecurity.getCode())) {//不相等
            return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "您输入的验证码有误,请重新输入", new JSONObject());
        }
        //判断该账户绑定手机号情况
        Map<String, Object> userAccountSecurityMap = redisUtils.hmget(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId());
        if (userAccountSecurityMap == null || userAccountSecurityMap.size() <= 0) {
            UserAccountSecurity uass = userAccountSecurityService.findUserAccountSecurityByUserId(userAccountSecurity.getUserId());
            if (uass == null) {
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未绑定过邮箱，无法解绑", new JSONObject());
            } else {
                if (!uass.getEmail().equals(userAccountSecurity.getEmail())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "解绑邮箱地址不正确，解绑失败", new JSONObject());
                }
                //开始解绑 更新数据库
                uass.setEmail("");
                userAccountSecurityService.updateUserAccountSecurity(uass);
            }
        } else {
            if (Integer.parseInt(userAccountSecurityMap.get("redisStatus").toString()) == 0) {//redisStatus==0 说明数据中无此记录
                return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "该账号未绑定过邮箱，无法解绑", new JSONObject());
            } else {
                if (!userAccountSecurityMap.get("email").toString().equals(userAccountSecurity.getEmail())) {
                    return returnData(StatusCode.CODE_ACCOUNTSECURITY_CHECK_ERROR.CODE_VALUE, "解绑邮箱地址不正确，解绑失败", new JSONObject());
                }
                //开始解绑 更新数据库
                UserAccountSecurity uass = (UserAccountSecurity) CommonUtils.mapToObject(userAccountSecurityMap, UserAccountSecurity.class);
                if (uass != null) {
                    uass.setEmail("");
                    userAccountSecurityService.updateUserAccountSecurity(uass);
                }
            }
        }
        //清除安全中心缓存
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY + userAccountSecurity.getUserId(), 0);
        //清除验证码
        redisUtils.expire(Constants.REDIS_KEY_USER_ACCOUNT_SECURITY_UNBIND_EMAIL_CODE + userAccountSecurity.getUserId() + "_" + userAccountSecurity.getEmail(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
