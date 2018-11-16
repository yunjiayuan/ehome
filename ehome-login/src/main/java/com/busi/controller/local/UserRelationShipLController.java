package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.*;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

/**
 * 好友关系相关接口
 * author：SunTianJie
 * create time：2018/7/16 17:20
 */
@RestController
public class UserRelationShipLController extends BaseController implements UserRelationShipLocalApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    UserRelationShipService userRelationShipService;

    @Autowired
    UserFriendGroupService userFriendGroupService;

    /***
     * 获取好友列表接口
     * @return
     */
    @Override
    public List findLocalFriendList(@PathVariable(value="userId") long userId) {
        //从缓存中获取好友列表
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+userId,0,-1);
        if(list!=null&&list.size()>0){//缓存中存在 直接返回
            //每次查询好友列表时 都延长生命周期
            redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+userId,Constants.USER_TIME_OUT);
            return list;
        }
        //缓存中不存在 查询数据库 并同步到缓存中
        //查询分组数据
        List groupList = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDGROUP+userId,0,-1);
        PageBean<UserFriendGroup> groupPageBean = null;
        if(groupList==null||groupList.size()<=0){
            //缓存中不存在 查询数据库 并同步到缓存中
            groupPageBean  = userFriendGroupService.findList(userId,1, 100);
            groupList = groupPageBean.getList();
            if(groupList==null){
                return null;
            }
            redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDGROUP+userId,groupList,Constants.USER_TIME_OUT);
        }
        //查询好友数据
        PageBean<UserRelationShip> pageBean;
        List userFriendList = null;
        pageBean  = userRelationShipService.findList(userId,1, 2000);
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
        redisUtils.expire(Constants.REDIS_KEY_USERFRIENDLIST+userId,0);
        //更新到缓存
        redisUtils.pushList(Constants.REDIS_KEY_USERFRIENDLIST+userId,newList);
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+userId,0,-1);
        return list;
    }

}
