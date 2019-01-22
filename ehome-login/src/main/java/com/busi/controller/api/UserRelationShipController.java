package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.*;
import com.busi.utils.*;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.util.*;

/**
 * 好友关系相关接口
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
@RestController
public class UserRelationShipController extends BaseController implements UserRelationShipApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRelationShipService userRelationShipService;

    @Autowired
    UserFriendGroupService userFriendGroupService;

    @Autowired
    UserMembershipService userMembershipService;

    @Autowired
    MqUtils mqUtils;

    /***
     * 新增好友关系接口
     * @param userRelationShip
     * @return
     */
    @Override
    public ReturnData addFriend(@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证userId
        if(CommonUtils.getMyId()!=userRelationShip.getUserId()){//与当前登录者身份不符
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误，与当前登录者ID不符，无权限操作",new JSONObject());
        }
        //获取当前用户的会员等级 根据用户会员等级 获取好友最大次数
        int userCount =  Constants.USER_FRIEND_COUNT;
        Map<String,Object> userMemberMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userRelationShip.getUserId());
        if(userMemberMap==null||userMemberMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            UserMembership userMembership = userMembershipService.findUserMembership(userRelationShip.getUserId());
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(CommonUtils.getMyId());
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            userMemberMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+userRelationShip.getUserId(),userMemberMap,Constants.USER_TIME_OUT);
        }
        if(userMemberMap.get("memberShipStatus")!=null&&!CommonUtils.checkFull(userMemberMap.get("memberShipStatus").toString())){
            int memberShipStatus = Integer.parseInt(userMemberMap.get("memberShipStatus").toString());
            if(memberShipStatus==1){//普通会员
                userCount = Constants.USER_FRIEND_COUNT_MEMBER;
            }else if(memberShipStatus>1){//高级以上
                userCount = Constants.USER_FRIEND_COUNT_SENIOR_MEMBER;
            }
        }
        if(redisUtils.getListSize(Constants.REDIS_KEY_USERFRIENDLIST+userRelationShip.getUserId())>=userCount){
            return returnData(StatusCode.CODE_MY_FRIENDS_COUNT_FULL.CODE_VALUE,"您当前好友个数已达到上线，开通或升级会员获取添加更多好友权限!",new JSONObject());
        }

        //获取对方用户的会员等级 根据用户会员等级 获取好友最大次数
        int firendCount =  Constants.USER_FRIEND_COUNT;
        Map<String,Object> firendMemberMap = redisUtils.hmget(Constants.REDIS_KEY_USERMEMBERSHIP+userRelationShip.getFriendId());
        if(firendMemberMap==null||firendMemberMap.size()<=0){
            //缓存中没有用户对象信息 查询数据库
            UserMembership userMembership = userMembershipService.findUserMembership(userRelationShip.getFriendId());
            if(userMembership==null){
                userMembership = new UserMembership();
                userMembership.setUserId(CommonUtils.getMyId());
            }else{
                userMembership.setRedisStatus(1);//数据库中已有对应记录
            }
            firendMemberMap = CommonUtils.objectToMap(userMembership);
            //更新缓存
            redisUtils.hmset(Constants.REDIS_KEY_USERMEMBERSHIP+userRelationShip.getFriendId(),firendMemberMap,Constants.USER_TIME_OUT);
        }
        if(firendMemberMap.get("memberShipStatus")!=null&&!CommonUtils.checkFull(firendMemberMap.get("memberShipStatus").toString())){
            int memberShipStatus = Integer.parseInt(firendMemberMap.get("memberShipStatus").toString());
            if(memberShipStatus==1){//普通会员
                firendCount = Constants.USER_FRIEND_COUNT_MEMBER;
            }else if(memberShipStatus>1){//高级以上
                firendCount = Constants.USER_FRIEND_COUNT_SENIOR_MEMBER;
            }
        }
        //查看对方缓存中是否存在好友列表
        List firendList = null;
        firendList = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0,-1);
        if(firendList==null||firendList.size()<=0){//缓存不中存在 证明好友申请 已经超过7天 按过期处理
            return returnData(StatusCode.CODE_FRIENDS_APPLY_NOT_EXIST.CODE_VALUE,"该好友申请已过期，请重新添加!",new JSONObject());
        }
        if(firendList.size()>=firendCount){
            return returnData(StatusCode.CODE_USER_FRIENDS_COUNT_FULL.CODE_VALUE,"对方好友个数已达到上线，无法添加!",new JSONObject());
        }

        //验证是否已存在好友关系
        boolean isFriend = false;//是否为好友 0不是好友  1是好友
        //判断自己是否与该用户是好友关系
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0,-1);
        if(list==null||list.size()<=0){
            if(userRelationShipService.checkFriend(CommonUtils.getMyId(),userRelationShip.getFriendId())){
                isFriend = true;
            }
            list = new ArrayList();
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
                    UserRelationShip userRelationShip2 = (UserRelationShip) userList.get(j);
                    if(userRelationShip2 != null&&userRelationShip2.getFriendId()==userRelationShip.getFriendId()){
                        isFriend = true;
                        forFlag = true;
                        break;
                    }
                }
            }
        }
        if(isFriend){
            return returnData(StatusCode.CODE_FRIENDS_IS_EXIST.CODE_VALUE,"双方已有好友关系，不能重复添加",new JSONObject());
        }
        Date time = new Date();
        UserRelationShip userRelationShip1 = new UserRelationShip();
        userRelationShip1.setUserId(userRelationShip.getUserId());
        userRelationShip1.setFriendId(userRelationShip.getFriendId());
        userRelationShip1.setFriendType(userRelationShip.getFriendType());
        userRelationShip1.setRemarkName(userRelationShip.getRemarkName());
        userRelationShip1.setGroupId(userRelationShip.getGroupId());
        userRelationShip1.setTime(time);
        userRelationShipService.add(userRelationShip1);

        UserRelationShip userRelationShip2 = new UserRelationShip();
        userRelationShip2.setUserId(userRelationShip.getFriendId());
        userRelationShip2.setFriendId(userRelationShip.getUserId());
        userRelationShip2.setFriendType(userRelationShip.getFriendType());
        userRelationShip2.setRemarkName(userRelationShip.getFriendRemarkName());
        userRelationShip2.setGroupId(userRelationShip.getFriendGroupId());
        userRelationShip2.setTime(time);
        userRelationShipService.add(userRelationShip2);

        //将缓存中双方的好友列表清除 查询的时候 会重新加载
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+userRelationShip.getUserId(),0);
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+userRelationShip.getFriendId(),0);

        //添加任务
        mqUtils.sendTaskMQ(userRelationShip.getUserId(),1,11);
        mqUtils.sendTaskMQ(userRelationShip.getFriendId(),1,11);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 删除好友接口
     * @param friendId 将要删除的好友ID
     * @return
     */
    @Override
    public ReturnData delFriend(@PathVariable long friendId) {
        //验证参数
        if(friendId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的好友ID friendId有误",new JSONObject());
        }
        long myId = CommonUtils.getMyId();
        if(myId==friendId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"将要删除的好友ID friendId有误，自己不能删除自己",new JSONObject());
        }
        userRelationShipService.del(myId,friendId);
        //将缓存中的双方的好友列表清除 查询的时候 会重新加载
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+friendId,0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 获取好友列表接口
     * @return
     */
    @Override
    public ReturnData findFriendList(@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(page<0||count<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"分页参数有误",new JSONObject());
        }
        //从缓存中获取好友列表
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0,-1);
        if(list!=null&&list.size()>0){//缓存中存在 直接返回
            //每次查询好友列表时 都延长生命周期
            redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),Constants.USER_TIME_OUT);
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,list);
        }
        //缓存中不存在 查询数据库 并同步到缓存中
        //查询分组数据
        List groupList = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),0,-1);
        PageBean<UserFriendGroup> groupPageBean = null;
        if(groupList==null||groupList.size()<=0){
            //缓存中不存在 查询数据库 并同步到缓存中
            groupPageBean  = userFriendGroupService.findList(CommonUtils.getMyId(),1, 100);
            groupList = groupPageBean.getList();
            if(groupList==null){
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"好友分组数据异常",new JSONObject());
            }
            redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDGROUP+CommonUtils.getMyId(),groupList,Constants.USER_TIME_OUT);
        }
        //查询好友数据
        PageBean<UserRelationShip> pageBean;
        List userFriendList = null;
        pageBean  = userRelationShipService.findList(CommonUtils.getMyId(),page, count);
        userFriendList = pageBean.getList();
        //组合数据
        List<Map<String, Object>> newList = new ArrayList<>();//最终组合后List
        for(int i=0;i<groupList.size();i++){
            UserFriendGroup userFriendGroup = (UserFriendGroup) groupList.get(i);
            List firendList = new ArrayList();
            Map<String,Object> map = new HashMap<>();
            if(userFriendGroup==null){
                continue;
            }
            for(int j=userFriendList.size()-1;j>=0;j--){
                UserRelationShip userRelationShip = (UserRelationShip)userFriendList.get(j);
                if(userRelationShip==null){
                    continue;
                }
                if(userRelationShip.getGroupId()==userFriendGroup.getId()){
                    Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userRelationShip.getFriendId());
                    if(userMap==null||userMap.size()<=0){
                        //缓存中没有用户对象信息 查询数据库
                        UserInfo userInfo = userInfoService.findUserById(userRelationShip.getFriendId());
                        if(userInfo==null){
                            continue;
                        }
                        //将用户信息存入缓存中 无论缓存中是否已有 直接覆盖
                        userMap = CommonUtils.objectToMap(userInfo);
                        redisUtils.hmset(Constants.REDIS_KEY_USER+userInfo.getUserId(),userMap,Constants.USER_TIME_OUT);
                    }
                    userRelationShip.setName(userMap.get("name").toString());
                    userRelationShip.setProType(Integer.parseInt(userMap.get("proType").toString()));
                    userRelationShip.setHouseNumber(Long.parseLong(userMap.get("houseNumber").toString()));
                    userRelationShip.setHead(userMap.get("head").toString());
                    userRelationShip.setSex(Integer.parseInt(userMap.get("sex").toString()));
                    userRelationShip.setBirthday(userMap.get("birthday").toString());
                    if(userMap.get("gxqm")!=null&&!CommonUtils.checkFull(userMap.get("gxqm").toString())){
                        userRelationShip.setGxqm(userMap.get("gxqm").toString());
                    }
                    firendList.add(userRelationShip);
                }
            }
            //按中文名称排序
            Collections.sort(firendList, new ComparatorUserFriendGroup());
            map.put("groupId", userFriendGroup.getId());
            map.put("groupName", userFriendGroup.getGroupName());
            map.put("userList",firendList);
            newList.add(map);
        }
        //清除缓存中的好友信息  防止并发
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);
        //更新到缓存
        redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),newList);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,StatusCode.CODE_SUCCESS.CODE_DESC,newList);
    }

    /***
     * 修改备注接口
     * @param userRelationShip
     * @return
     */
    @Override
    public ReturnData updateRemarkName(@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证userId
        if(CommonUtils.getMyId()!=userRelationShip.getUserId()){//与当前登录者身份不符
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误，与当前登录者ID不符，无权限操作",new JSONObject());
        }
        userRelationShipService.updateRemarkName(userRelationShip);
        //清除缓存中的好友信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+CommonUtils.getMyId(),0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     *  将好友移动到其他分组接口
     *  移入黑名单时groupId为固定值-3
     *  移出黑名单时groupId为固定值0
     * @param userRelationShip
     * @return
     */
    @Override
    public ReturnData moveFriend(@Valid @RequestBody UserRelationShip userRelationShip, BindingResult bindingResult) {
        //验证参数格式是否正确
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //验证userId
        if(CommonUtils.getMyId()!=userRelationShip.getUserId()){//与当前登录者身份不符
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误，与当前登录者ID不符，无权限操作",new JSONObject());
        }
        userRelationShipService.moveFriend(userRelationShip);
        //清除缓存中的分组信息
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+userRelationShip.getUserId(),0);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 批量查询手机号是否绑定云家园账号接口
     * @param phones
     * @return
     */
    @Override
    public ReturnData searchPhoneForContacts(@PathVariable String phones) {
        //验证参数
        if(CommonUtils.checkFull(phones)){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"手机号参数有误，不能为空",new JSONArray());
        }
        String[] phoneArray = phones.split(",");
        if(phoneArray==null||phoneArray.length<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"解析手机号出错，请检查手机号参数格式",new JSONArray());
        }
        List<PhoneAddressList> list = new ArrayList();
        for(int i=0;i<phoneArray.length;i++){
            String phone = phoneArray[i];
            PhoneAddressList phoneAddressList = new PhoneAddressList();
            phoneAddressList.setPhone(phone);
            Object obj = redisUtils.hget(Constants.REDIS_KEY_PHONENUMBER,phone);
            if(obj==null||CommonUtils.checkFull(obj.toString())){
                phoneAddressList.setStatus(2);//非云家园用户 或者已经很久没登陆了
            }else{
                long userId = Long.parseLong(obj.toString());
                //过滤自己
                if(userId==CommonUtils.getMyId()){
                    continue;
                }
                phoneAddressList.setUserId(userId);
                Map<String,Object> userMap = redisUtils.hmget(Constants.REDIS_KEY_USER+userId);
                if(userMap==null){
                    continue;
                }
                phoneAddressList.setName(userMap.get("name").toString());
                phoneAddressList.setHead(userMap.get("head").toString());
                //判断自己是否与该用户是好友关系
                if(userRelationShipService.checkFriend(CommonUtils.getMyId(),userId)){
                    phoneAddressList.setStatus(1);//已为好友关系
                }else{
                    phoneAddressList.setStatus(0);//不是好友关系
                }
            }
            list.add(phoneAddressList);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",list);
    }
}
