package com.busi.controller.local;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.controller.api.HomeBlogApiController;
import com.busi.entity.*;
import com.busi.service.HomeBlogLikeService;
import com.busi.service.HomeBlogService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
