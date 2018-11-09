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

    /***
     * 更新生活圈接口
     * @param homeBlog
     * @return
     */
    @Override
    public ReturnData updateBlog(@RequestBody HomeBlog homeBlog) {
        HomeBlog hb = homeBlogService.findBlogInfo(homeBlog.getId(),homeBlog.getUserId());
        //点赞量
        if(hb.getLikeCount()<=0){
            if(homeBlog.getLikeCount()>0){
                hb.setLikeCount(hb.getLikeCount()+homeBlog.getLikeCount());
            }
        }else{
            if(homeBlog.getLikeCount()!=0){
                hb.setLikeCount(hb.getLikeCount()+homeBlog.getLikeCount());
            }
        }
        //评论量
        if(hb.getCommentCount()<=0){
            if(homeBlog.getCommentCount()>0){
                hb.setCommentCount(hb.getCommentCount()+homeBlog.getCommentCount());
            }
        }else{
            if(homeBlog.getCommentCount()!=0){
                hb.setCommentCount(hb.getCommentCount()+homeBlog.getCommentCount());
            }
        }
        //浏览量 只会增加
        if(homeBlog.getLookCount()!=0){
            hb.setLookCount(hb.getLookCount()+homeBlog.getLookCount());
        }
        //转发量 只会增加
        if(homeBlog.getShareCount()!=0){
            hb.setShareCount(hb.getShareCount()+homeBlog.getShareCount());
        }
        int count = homeBlogService.updateBlog(hb);
        if(count<=0){
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"更新生活圈评论数、点赞数、浏览量、转发量操作失败",new JSONObject());
        }
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_EBLOG + homeBlog.getUserId()+"_"+homeBlog.getId(), 0);
        //更新生活秀首页推荐列表
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_EBLOGLIST, 0, Constants.REDIS_KEY_EBLOGLIST_COUNT+1);
        if(list!=null){
            boolean falg = false;
            for (int i = 0; i < list.size(); i++) {
                HomeBlog redisHomeBlog = (HomeBlog) list.get(i);
                if(redisHomeBlog!=null){
                    if(redisHomeBlog.getId()==hb.getId()){//推荐列表中存在 更新
                        redisUtils.updateListByIndex(Constants.REDIS_KEY_EBLOGLIST,i,hb);
                        falg = true;
                        break;
                    }
                }
            }
            if(!falg){//推荐列表中不存在
                //判断点赞量是否达到推荐级别
                if(hb.getLikeCount()>=Constants.EBLOG_LIKE_COUNT){
                    redisUtils.addListLeft(Constants.REDIS_KEY_EBLOGLIST, hb, 0);
                }
            }
            if (list.size() > Constants.REDIS_KEY_EBLOGLIST_COUNT) {
                //清除缓存中多余的信息
                redisUtils.expire(Constants.REDIS_KEY_EBLOGLIST, 0);
                redisUtils.pushList(Constants.REDIS_KEY_EBLOGLIST, list, 0);
            }
        }else{//不存在
            //判断点赞量是否达到推荐级别
            if(hb.getLikeCount()>=Constants.EBLOG_LIKE_COUNT){
                redisUtils.addListLeft(Constants.REDIS_KEY_EBLOGLIST, hb, 0);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
