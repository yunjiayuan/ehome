package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

/**
 * 更新生活圈评论数、点赞数、浏览量、转发量相关接口
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
@RestController
public class HomeBlogLController extends BaseController implements HomeBlogLocalController {

    @Autowired
    private HomeBlogService homeBlogService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;

    /***
     * 更新生活圈接口
     * @param homeBlog
     * @return
     */
    @Override
    public ReturnData updateBlog(@RequestBody HomeBlog homeBlog) {
        HomeBlog hb = homeBlogService.findBlogInfo(homeBlog.getId(),homeBlog.getUserId());
        if(hb==null){
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
        }
        if(hb.getBlogType()!=1&&hb.getSendType()==2&&hb.getLikeCount()>=Constants.EBLOG_LIKE_COUNT){
            //先将此对象从生活秀列表中清除 方便后续操作
            redisUtils.removeSetByValues(Constants.REDIS_KEY_EBLOGSET,hb);
        }
        //评论量
        long commentCount = hb.getCommentCount()+homeBlog.getCommentCount();
        if(commentCount<=0){
            commentCount = 0;
        }
        hb.setCommentCount(commentCount);
        //浏览量 只会增加
        if(homeBlog.getLookCount()!=0){
            hb.setLookCount(hb.getLookCount()+homeBlog.getLookCount());
        }
        //转发量 只会增加
        if(homeBlog.getShareCount()!=0){
            hb.setShareCount(hb.getShareCount()+homeBlog.getShareCount());
        }
        //点赞量
        if(homeBlog.getLikeCount()!=0){
            long likeCount = hb.getLikeCount()+homeBlog.getLikeCount();
            if(likeCount<0){
                likeCount = 0;
            }
            hb.setLikeCount(likeCount);
            //判断是否该给用户发送奖励  此活动结束后 将以下代码注释即可
            if(hb.getSendType()==2){//必须为视频
                double rewardMoney = 0;//奖励金额
                int rewardType = -1;//4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
                double[] moneyArray = new double[10000];//奖池 可自定义奖池大小
                Random random = new Random();
                if(hb.getLikeCount()==Constants.REWARD_EBLOG_LIKE_COUNT_10000){
                    //大区间为20-30  实际20-25居多
                    //开始构建奖池
                    for(int i=0;i<10000;i++){
                        moneyArray[i] = (random.nextInt(501) + 2000)/100.0;
                    }
                    //向奖池中添加20-30区间的红包 千分之一概率
                    for(int i=0;i<10;i++){
                        moneyArray[random.nextInt(10000)] = (random.nextInt(1001) + 2000)/100.0;
                    }
                    //向奖池中添加大额红包 万分之一概率  后续添加 需要再构建一个小奖池
                    //奖池构建完成 开始随机取值
                    rewardMoney = moneyArray[random.nextInt(10000)];
                    rewardType = 6;
                }else if(hb.getLikeCount()==Constants.REWARD_EBLOG_LIKE_COUNT_100){
                    //大区间为5-10  实际5-5.5居多
                    //开始构建奖池
                    for(int i=0;i<10000;i++){
                        moneyArray[i] = (random.nextInt(51) + 500)/100.0;
                    }
                    //向奖池中添加5-10区间的红包 千分之一概率
                    for(int i=0;i<10;i++){
                        moneyArray[random.nextInt(10000)] = (random.nextInt(501) + 500)/100.0;
                    }
                    //向奖池中添加大额红包 万分之一概率  后续添加 需要再构建一个小奖池
                    //奖池构建完成 开始随机取值
                    rewardMoney = moneyArray[random.nextInt(10000)];
                    rewardType = 5;
                }else if(hb.getLikeCount()==Constants.REWARD_EBLOG_LIKE_COUNT_10){
                    //大区间为1-3  实际1-1.5居多
                    //开始构建奖池
                    for(int i=0;i<10000;i++){
                        moneyArray[i] = (random.nextInt(51) + 100)/100.0;
                    }
                    //向奖池中添加1-3区间的红包 千分之一概率
                    for(int i=0;i<10;i++){
                        moneyArray[random.nextInt(10000)] = (random.nextInt(201) + 100)/100.0;
                    }
                    //向奖池中添加大额红包 万分之一概率  后续添加 需要再构建一个小奖池
                    //奖池构建完成 开始随机取值
                    rewardMoney = moneyArray[random.nextInt(10000)];
                    rewardType = 4;
                }
                if(rewardType>0){
                    mqUtils.addRewardLog(hb.getUserId(),rewardType,0,rewardMoney,homeBlog.getId());
                }
            }
        }
        //上边将生活秀删除 此处重新添加进去
        if(hb.getBlogType()!=1&&hb.getSendType()==2&&hb.getLikeCount()>=Constants.EBLOG_LIKE_COUNT){
            redisUtils.addSet(Constants.REDIS_KEY_EBLOGSET,hb);
        }
        //更新数据库和缓存中的实体对象
        int count = homeBlogService.updateBlog(hb);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新生活圈评论数、点赞数、浏览量、转发量操作失败",new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_EBLOG + homeBlog.getUserId()+"_"+homeBlog.getId(), 0);
        //重新将生活圈加载到缓存
        redisUtils.hmset(Constants.REDIS_KEY_EBLOG+homeBlog.getUserId()+"_"+homeBlog.getId(),CommonUtils.objectToMap(hb),Constants.USER_TIME_OUT);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
