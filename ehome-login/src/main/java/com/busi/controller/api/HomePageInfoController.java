package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.UserInfoService;
import com.busi.service.UserJurisdictionService;
import com.busi.service.UserRelationShipService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取家主页信息接口
 * author：SunTianJie
 * create time：2018/7/16 13:51
 */
@RestController
public class HomePageInfoController extends BaseController implements HomePageInfoApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRelationShipService userRelationShipService;

    @Autowired
    UserJurisdictionService userJurisdictionService;

    /***
     * 获取指定用户ID的家主页信息
     * @param userId
     * @return
     */
    @Override
    public ReturnData findHomePageInfo(@PathVariable long userId) {
        //获取当前登录者的用户ID
        long myId = CommonUtils.getMyId();
        Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId );
        if(userMap==null||userMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            UserInfo userInfo = userInfoService.findUserById((Long)userId);
            if(userInfo==null){
                return returnData(StatusCode.CODE_ACCOUNT_NOT_EXIST.CODE_VALUE,"您访问的用户不存在",new JSONObject());
            }
            //将用户信息存入缓存中
            userMap = CommonUtils.objectToMap(userInfo);
            redisUtils.hmset(Constants.REDIS_KEY_USER+userId,userMap,Constants.USER_TIME_OUT);
        }
        HomePageInfo homePageInfo =  new HomePageInfo();
        homePageInfo.setUserId(userId);
        //获取用户被访问权限和房间锁信息
        Map<String,Object> userJurisdictionMap = redisUtils.hmget(Constants.REDIS_KEY_USER_JURISDICTION+userId);
        if(userJurisdictionMap==null||userJurisdictionMap.size()<=0){
            UserJurisdiction userJurisdiction = userJurisdictionService.findUserJurisdiction(userId);
            if(userJurisdiction==null){
                //之前该用户未设置过权限信息
                userJurisdiction = new UserJurisdiction();
                userJurisdiction.setUserId(userId);
            }else{
                userJurisdiction.setRedisStatus(1);//数据库中已有记录
            }
            userJurisdictionMap = CommonUtils.objectToMap(userJurisdiction);
            //放到缓存中
            redisUtils.hmset(Constants.REDIS_KEY_USER_JURISDICTION+userId,userJurisdictionMap);
        }
        homePageInfo.setGarden(Integer.parseInt(userJurisdictionMap.get("garden").toString()));//花园锁状态 1未上锁 2已上锁
        homePageInfo.setLivingRoom(Integer.parseInt(userJurisdictionMap.get("livingRoom").toString()));//客厅锁状态 1未上锁 2已上锁
        homePageInfo.setHomeStore(Integer.parseInt(userJurisdictionMap.get("homeStore").toString()));//家店锁状态 1未上锁 2已上锁
        homePageInfo.setStorageRoom(Integer.parseInt(userJurisdictionMap.get("storageRoom").toString()));//储存室锁状态 1未上锁 2已上锁
        int accessRights = Integer.parseInt(userJurisdictionMap.get("accessRights").toString());//访问权限 0允许任何人  1禁止任何人  2 仅好友
        //判断被访问者是不是自己
        int isFriend = 0;//是否为好友 0不是好友  1是好友
        //判断自己是否与该用户是好友关系
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0,-1);
        if(list==null||list.size()<=0){
            if(userRelationShipService.checkFriend(CommonUtils.getMyId(),userId)){
                isFriend = 1;
                list = new ArrayList();
            }else{
                isFriend = 0;
                list = new ArrayList();
            }
        }
        boolean forFlag = false;
        for(int i=0;i<list.size();i++){
            if(forFlag){
                break;
            }
            Map map = (Map) list.get(i);
            if(map!=null&&map.size()>0){
                List userList = (List) map.get("userList");
                if(userList==null||userList.size()<=0){
                    continue;
                }
                for(int j=0;j <userList.size();j++){
                    UserRelationShip userRelationShip = (UserRelationShip) userList.get(j);
                    if(userRelationShip != null&&userRelationShip.getFriendId()==userId){
                        isFriend = 1;
                        forFlag = true;
                        break;
                    }
                }
            }
        }
        if(userId!=myId){
            //判断当前用户是否有访问权限  1允许任何人  2禁止任何人  3 仅好友
            if(accessRights==1){
                homePageInfo.setAccessRights(0);//访问权限 0允许任何人  1禁止任何人  2 已是好友可以访问   3不是好友禁止访问
            }
            if(accessRights==2){//禁止访问
                homePageInfo.setAccessRights(1);//访问权限 0允许任何人  1禁止任何人  2 已是好友可以访问   3不是好友禁止访问
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"您当前串门的用户禁止任何人访问串门",homePageInfo);
            }
            if(accessRights==3){//仅好友可以访问
                if(isFriend==0){
                    homePageInfo.setAccessRights(3);//访问权限 0允许任何人  1禁止任何人  2 已是好友可以访问   3不是好友禁止访问
                    return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"您当前串门的用户仅好友可以串门",homePageInfo);
                }else{
                    homePageInfo.setAccessRights(2);//访问权限 0允许任何人  1禁止任何人  2 已是好友可以访问   3不是好友禁止访问
                }
            }
        }
        homePageInfo.setIsFriend(isFriend);
        //获取要访问用户的会员信息
        homePageInfo.setMembershipLevel(0);//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
        homePageInfo.setMemberLevel(0);//会员等级  1：一级  2：二级 3...
        //获取要访问用户的是否有被涂鸦过
        if(userMap.get("graffitiHead")==null||CommonUtils.checkFull(userMap.get("graffitiHead").toString())){
            homePageInfo.setHead(userMap.get("head").toString());//头像
        }else{
            homePageInfo.setHead(userMap.get("graffitiHead").toString());//头像
            homePageInfo.setGraffitiStatus(1);//涂鸦状态 0当前用户未被涂鸦过 1当前用户已被涂鸦
        }
        //设置其他信息
        homePageInfo.setProType(Integer.parseInt(userMap.get("proType").toString()));//省简称ID
        homePageInfo.setHouseNumber(Long.parseLong(userMap.get("houseNumber").toString()));//门牌号
        homePageInfo.setName(userMap.get("name").toString());//昵称
        homePageInfo.setSex(Integer.parseInt(userMap.get("sex").toString()));//性别
        homePageInfo.setIsNewUser(Integer.parseInt(userMap.get("isNewUser").toString()));//是否为新用户  0新用户 1已领新人红包(老用户)
        homePageInfo.setWelcomeInfoStatus(Integer.parseInt(userMap.get("welcomeInfoStatus").toString()));//系统欢迎消息状态 0表示未发送  1表示已发送
        homePageInfo.setFlag(0);//临时参数 1禁止查看会员中心 方便IOS平台审核
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",homePageInfo);
    }
}
