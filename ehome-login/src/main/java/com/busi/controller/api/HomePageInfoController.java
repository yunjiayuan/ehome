package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.*;
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

    @Autowired
    UserMembershipService userMembershipService;

    @Autowired
    FollowInfoService followInfoService;

    @Autowired
    FollowCountsService followCountsService;

    @Autowired
    VisitViewService visitViewService;

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
        homePageInfo.setSwitchLamp(Integer.parseInt(userJurisdictionMap.get("switchLamp").toString()));//开关灯状态值 0 默认开灯  1关灯
        int accessRights = Integer.parseInt(userJurisdictionMap.get("accessRights").toString());//访问权限 0允许任何人  1禁止任何人  2 仅好友
        //判断被访问者是不是自己
        int isFriend = 0;//是否为好友 0不是好友  1是好友
        int isFollow = 0;//是否已关注 0未关注 1已关注
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
            //设置关注关系
            String[] followArray = null;
            Object follow = redisUtils.getKey(Constants.REDIS_KEY_FOLLOW_LIST+CommonUtils.getMyId());
            if(follow!=null&&!CommonUtils.checkFull(follow.toString())){
                followArray = follow.toString().split(",");
            }else{
                PageBean<FollowInfo> pageBean = followInfoService.findFollowList(CommonUtils.getMyId(),0,1,2000);
                if(pageBean!=null&&pageBean.getList()!=null&&pageBean.getList().size()>0){
                    String followUserIds = "";
                    for (int i = 0; i <pageBean.getList().size() ; i++) {
                        FollowInfo followInfo = pageBean.getList().get(i);
                        if(followInfo==null){
                            continue;
                        }
                        if(i==pageBean.getList().size()-1){
                            followUserIds += followInfo.getFollowUserId()+"";
                        }else{
                            followUserIds += followInfo.getFollowUserId()+",";
                        }
                    }
                    //放入缓存
                    redisUtils.set(Constants.REDIS_KEY_FOLLOW_LIST+CommonUtils.getMyId(),followUserIds,Constants.USER_TIME_OUT);
                    followArray = followUserIds.split(",");
                }
            }
            if(followArray!=null){
                for (int i = 0; i < followArray.length; i++) {
                    if(userId==Long.parseLong(followArray[i])){
                        isFollow = 1;
                        break;
                    }
                }
            }
        }
        homePageInfo.setIsFollow(isFollow);
        homePageInfo.setIsFriend(isFriend);
        //获取要访问用户的会员信息
        Map<String,Object> membershipMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userId );
        if(membershipMap==null||membershipMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            UserMembership userMembership = userMembershipService.findUserMembership(userId);
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(userId);
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            membershipMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+userId,membershipMap,Constants.USER_TIME_OUT);
        }
        if(membershipMap.get("memberShipStatus")!=null&&!CommonUtils.checkFull(membershipMap.get("memberShipStatus").toString())){
            int memberShipStatus = Integer.parseInt(membershipMap.get("memberShipStatus").toString());
            int initiatorMembershipLevel = 0;
            if(memberShipStatus==4&&membershipMap.get("initiatorMembershipLevel")!=null&&!CommonUtils.checkFull(membershipMap.get("initiatorMembershipLevel").toString())){//创始元老级会员
                initiatorMembershipLevel = Integer.parseInt(membershipMap.get("initiatorMembershipLevel").toString());
            }
            homePageInfo.setMembershipLevel(memberShipStatus);//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
            homePageInfo.setMemberLevel(initiatorMembershipLevel);//会员等级  1：一级  2：二级 3...
        }else{
            homePageInfo.setMembershipLevel(0);//用户当前会员状态  1：普通会员  2：vip高级会员  3：元老级会员  4：创始元老级会员
            homePageInfo.setMemberLevel(0);//会员等级  1：一级  2：二级 3...
        }

        //获取要访问用户的是否有被涂鸦过
        if(userMap.get("graffitiHead")==null||CommonUtils.checkFull(userMap.get("graffitiHead").toString())){
            if(userMap.get("head")!=null){
                homePageInfo.setHead(userMap.get("head").toString());//头像
            }
        }else{
            homePageInfo.setHead(userMap.get("graffitiHead").toString());//头像
            homePageInfo.setGraffitiStatus(1);//涂鸦状态 0当前用户未被涂鸦过 1当前用户已被涂鸦
        }
        //设置粉丝数
        Object object = redisUtils.getKey(Constants.REDIS_KEY_FOLLOW_COUNTS+userId);
        long followCounts = 0;
        if(object!=null&&!CommonUtils.checkFull(object.toString())){
            followCounts = Long.parseLong(object.toString());
        }else{
            FollowCounts fc = followCountsService.findFollowInfo(userId);
            if(fc!=null){
                followCounts = fc.getCounts();
            }
        }
        homePageInfo.setFollowCounts(followCounts);
        //设置其他信息
        homePageInfo.setProType(Integer.parseInt(userMap.get("proType").toString()));//省简称ID
        homePageInfo.setHouseNumber(Long.parseLong(userMap.get("houseNumber").toString()));//门牌号
        if(userMap.get("name")!=null){
            homePageInfo.setName(userMap.get("name").toString());//昵称
        }
        homePageInfo.setSex(Integer.parseInt(userMap.get("sex").toString()));//性别
        homePageInfo.setIsNewUser(Integer.parseInt(userMap.get("isNewUser").toString()));//是否为新用户  0新用户 1已领新人红包(老用户)
        homePageInfo.setWelcomeInfoStatus(Integer.parseInt(userMap.get("welcomeInfoStatus").toString()));//系统欢迎消息状态 0表示未发送  1表示已发送
        homePageInfo.setIsGoodNumber(Integer.parseInt(userMap.get("isGoodNumber").toString()));//设置靓号情况
        homePageInfo.setUser_ce(Integer.parseInt(userMap.get("user_ce").toString()));//设置大V用户状态
        homePageInfo.setTalkToSomeoneStatus(Integer.parseInt(userMap.get("talkToSomeoneStatus").toString()));//设置找人倾诉状态
        homePageInfo.setChatnteractionStatus(Integer.parseInt(userMap.get("chatnteractionStatus").toString()));//设置找人互动状态
        homePageInfo.setIsSpokesman(Integer.parseInt(userMap.get("isSpokesman").toString()));//设置代言人类型
        if(userMap.get("spokesmanName")!=null){
            homePageInfo.setSpokesmanName(userMap.get("spokesmanName").toString());//设置代言人名称
        }
        int homepageinfoFlag = 0;//苹果“屏蔽主界面部分功能按钮”状态  0默认关闭  1开启
        Object obj = redisUtils.getKey(Constants.REDIS_KEY_ADMINI_HOMEPAGEINFO_FLAG);
        if(obj!=null){
            homepageinfoFlag = Integer.parseInt(obj.toString());
        }
        homePageInfo.setFlag(homepageinfoFlag);//临时参数 1禁止查看苹果部分功能 方便IOS平台审核
        int videoshootType = 0;//“生活圈拍摄视频时的拍摄类型” 0默认使用七牛拍摄 1使用APP自研拍摄 2使用其他平台拍摄
        Object videoshootObj = redisUtils.getKey(Constants.REDIS_KEY_ADMINI_VIDEOSHOOT_TYPE);
        if(videoshootObj!=null){
            videoshootType = Integer.parseInt(videoshootObj.toString());
        }
        int homepageinfoFlagByAndroid = 0;//安卓“屏蔽主界面部分功能按钮”状态  0默认关闭  1开启
        Object obj2 = redisUtils.getKey(Constants.REDIS_KEY_ADMINI_HOMEPAGEINFO_FLAG_ANDROID);
        if(obj2!=null){
            homepageinfoFlagByAndroid = Integer.parseInt(obj2.toString());
        }
        homePageInfo.setFlagByAndroid(homepageinfoFlagByAndroid);//临时参数 1禁止安卓部分功能 方便安卓平台审核
        homePageInfo.setVideoshootType(videoshootType);//临时参数 1禁止查看会员中心 方便IOS平台审核
        int purseCashOutStatus = 0;//0开启钱包提现功能 1禁用钱包提现功能
        Object obj3 = redisUtils.getKey(Constants.REDIS_KEY_ADMINI_PURSE_CASHOUT_STATUS);
        if(obj3!=null){
            purseCashOutStatus = Integer.parseInt(obj3.toString());
        }
        homePageInfo.setPurseCashOutStatus(purseCashOutStatus);//临时参数 与数据库无关字段 0开启钱包提现功能 1禁用钱包提现功能

        int shopFloorStatus = 0;//0开启钱包提现功能 1禁用钱包提现功能
        Object obj4 = redisUtils.getKey(Constants.REDIS_KEY_ADMINI_SHOPFLOOR_STATUS);
        if(obj4!=null){
            shopFloorStatus = Integer.parseInt(obj4.toString());
        }
        homePageInfo.setShopFloorStatus(shopFloorStatus);//临时参数 与数据库无关字段 0表示家门口隐形超市只允许礼品类商品加入购物车  1表示店铺正常并且无任何限制  2表示隐形超市功能暂时停用

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
            homePageInfo.setTodayVisitCount(visitView.getTodayVisitCount());//初始化今日访问量
            homePageInfo.setTotalVisitCount(visitView.getTotalVisitCount());//设置总访问量
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
                homePageInfo.setTodayVisitCount(visitView.getTodayVisitCount());//初始化今日访问量
                homePageInfo.setTotalVisitCount(visitView.getTotalVisitCount());//设置总访问量
                //更新缓存
                redisUtils.hmset(Constants.REDIS_KEY_USER_VISIT+visitView.getUserId(),CommonUtils.objectToMap(visitView),CommonUtils.getCurrentTimeTo_12());//保证今日访问量的生命周期 到今天晚上12点失效
            }else{
                homePageInfo.setTodayVisitCount(vv.getTodayVisitCount()+1);//设置今日访问量
                homePageInfo.setTotalVisitCount(vv.getTotalVisitCount()+1);//设置总访问量
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",homePageInfo);
    }

    /***
     * 更新管理员权限中的相关操作
     * @param type   设置类型 type=0 修改苹果“屏蔽主界面部分功能按钮”状态
     *                         type=1 修改“生活圈拍摄视频时的拍摄类型”
     *                         type=2 修改安卓“屏蔽主界面部分功能按钮”状态
     *                         type=3 修改“家门口隐藏超市使用权限”
     * @param status type=0或者2时 status为状态值 0默认关闭  1开启
     *                type=1时 status：0默认使用七牛拍摄 1使用APP自研拍摄 2使用其他平台拍摄
     *                type=3时 status：0表示家门口隐形超市只允许礼品类商品加入购物车  1表示店铺正常并且无任何限制  2表示隐形超市功能暂时停用
     * @return
     */
    @Override
    public ReturnData adminiSetUp(@PathVariable int type,@PathVariable int status) {
        if(CommonUtils.getAdministrator(CommonUtils.getMyId(),redisUtils)<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您无权限进行此操作，请联系管理员申请权限!",new JSONObject());
        }
        if(type<0||type>3||status<0||status>2){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
        }
        switch (type) {
            case 0://修改“屏蔽主界面部分功能按钮”状态
                redisUtils.set(Constants.REDIS_KEY_ADMINI_HOMEPAGEINFO_FLAG,status+"",0);//永不失效
                break;
            case 1://修改“生活圈拍摄视频时的拍摄类型”
                redisUtils.set(Constants.REDIS_KEY_ADMINI_VIDEOSHOOT_TYPE,status+"",0);//永不失效
                break;
            case 2://修改安卓“屏蔽主界面部分功能按钮”状态
                redisUtils.set(Constants.REDIS_KEY_ADMINI_HOMEPAGEINFO_FLAG_ANDROID,status+"",0);//永不失效
                break;
            case 3://修改“家门口隐藏超市使用权限”状态
                redisUtils.set(Constants.REDIS_KEY_ADMINI_SHOPFLOOR_STATUS,status+"",0);//永不失效
                break;
            default:
                break;
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
