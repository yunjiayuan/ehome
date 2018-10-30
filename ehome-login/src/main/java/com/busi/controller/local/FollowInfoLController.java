package com.busi.controller.local;

import com.busi.controller.BaseController;
import com.busi.entity.FollowInfo;
import com.busi.entity.PageBean;
import com.busi.service.FollowInfoService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
@RestController
public class FollowInfoLController extends BaseController implements FollowInfoLocalController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    FollowInfoService followInfoService;

    /***
     * 查询指定用户的关注列表
     * @param userId
     * @return
     */
    @Override
    public String getFollowInfo(@PathVariable(value = "userId") long userId) {
        PageBean<FollowInfo> pageBean = followInfoService.findFollowList(userId,0,1,2000);
        String followUserIds = "";
        if(pageBean!=null&&pageBean.getList()!=null&&pageBean.getList().size()>0){
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
            redisUtils.set(Constants.REDIS_KEY_FOLLOW_LIST+userId,followUserIds,Constants.USER_TIME_OUT);
        }
        return followUserIds;
    }
}
