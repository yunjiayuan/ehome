package com.busi.utils;

import com.busi.fegin.UserRelationShipLocalControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * 查询用户信息工具类
 * author：SunTianJie
 * create time：2018/9/7 19:08
 */
@Component
public class UserRelationShipUtils {

    @Autowired
    private UserRelationShipLocalControllerFegin userRelationShipLocalControllerFegin;

    @Autowired
    RedisUtils redisUtils;

    public List getFirendList(long userId){
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+userId,0,-1);
        if(list==null||list.size()<=0){//缓存无好友列表存在 直接返回
            userRelationShipLocalControllerFegin.findLocalFriendList(userId);
        }
        list = redisUtils.getList(Constants.REDIS_KEY_USERFRIENDLIST+userId,0,-1);
        return list;
    }
}
