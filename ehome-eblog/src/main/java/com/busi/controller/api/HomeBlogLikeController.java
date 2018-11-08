package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeBlogLikeService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 生活圈 点赞 相关接口
 * author：SunTianJie
 * create time：2018/10/23 10:35
 */
@RestController
public class HomeBlogLikeController extends BaseController implements HomeBlogLikeApiController {

    @Autowired
    private HomeBlogLikeService homeBlogLikeService;

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;

    /***
     * 新增点赞接口
     * @param homeBlogLike
     * @return
     */
    @Override
    public ReturnData addHomeBlogLike(@Valid @RequestBody HomeBlogLike homeBlogLike, BindingResult bindingResult) {
        //验证参数
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //判断操作人权限
        if(CommonUtils.getMyId()!=homeBlogLike.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限以用户["+homeBlogLike.getUserId()+"]的身份进行相关操作",new JSONObject());
        }
        //检测是否点过赞
        boolean isMember = redisUtils.isMember(Constants.EBLOG_LIKE_LIST+homeBlogLike.getBlogId(),homeBlogLike.getUserId());
        if(isMember){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您已经点过赞了",new JSONObject());
        }else{
            HomeBlogLike hbl = homeBlogLikeService.checkHomeBlogLike(homeBlogLike.getUserId(),homeBlogLike.getBlogId());
            if(hbl!=null){//已经点过赞
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您已经点过赞了",new JSONObject());
            }
        }
        //新增点赞记录
        homeBlogLike.setTime(new Date());
        homeBlogLikeService.addHomeBlogLike(homeBlogLike);
        //更新缓存中的点赞记录
        redisUtils.addSetAndTime(Constants.EBLOG_LIKE_LIST+homeBlogLike.getBlogId(),0,homeBlogLike.getUserId());
        //更新当前生活圈的点赞数
        mqUtils.updateBlogCounts(homeBlogLike.getBlogUserId(),homeBlogLike.getBlogId(),0,1);
        //更新消息系统 后续开发
        mqUtils.addMessage(homeBlogLike.getUserId(),homeBlogLike.getBlogUserId(),homeBlogLike.getBlogId(),0,"",4);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除点赞接口
     * @param userId 将要删除的点赞用户ID
     * @param blogId 将要操作的生活圈ID
     * @return
     */
    @Override
    public ReturnData delHomeBlogLike(@PathVariable long userId,@PathVariable long blogId) {
        //验证参数
        if(userId<=0||blogId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
        }
        //判断操作人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限以用户["+userId+"]的身份进行相关操作",new JSONObject());
        }
        //检测是否点过赞
        HomeBlogLike homeBlogLike = homeBlogLikeService.checkHomeBlogLike(userId,blogId);
        if(homeBlogLike==null){//未点过赞 直接返回
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        homeBlogLikeService.delHomeBlogLike(userId, blogId);
        //更新缓存中的点赞记录
        redisUtils.removeSetByValues(Constants.EBLOG_LIKE_LIST+blogId,userId);
        //更新当前生活圈的点赞数
        mqUtils.updateBlogCounts(homeBlogLike.getBlogUserId(),homeBlogLike.getBlogId(),0,-1);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 条件查询点赞列表接口
     * @param blogId     将要操作的生活圈ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findHomeBlogLike(@PathVariable long blogId,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if(blogId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
        }
        if(page<1){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"page参数有误",new JSONObject());
        }
        if(count<0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"count参数有误",new JSONObject());
        }
        PageBean<HomeBlogLike> pageBean = null;
        pageBean = homeBlogLikeService.findHomeBlogLikeList(blogId, page, count);
        if(pageBean==null){
            pageBean = new PageBean<HomeBlogLike>();
            pageBean.setList(new ArrayList<>());
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
        }
        List<HomeBlogLike> list  = pageBean.getList();
        for(int i=0;i<list.size();i++){
            HomeBlogLike homeBlogLike = list.get(i);
            if(homeBlogLike==null){
                continue;
            }
            //设置用户信息
            UserInfo userInfo = userInfoUtils.getUserInfo(homeBlogLike.getUserId());
            if(userInfo!=null){
                homeBlogLike.setUserName(userInfo.getName());
                homeBlogLike.setUserHead(userInfo.getHead());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

}
