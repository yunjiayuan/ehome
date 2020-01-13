package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.BlogTagService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 生活圈兴趣标签
 * @author: suntj
 * @create: 2020-1-8 17:25:30
 */
@RestController
public class BlogTagController extends BaseController implements BlogTagApiController {

    @Autowired
    private BlogTagService blogTagService;

    @Autowired
    RedisUtils redisUtils;

    /**
     * @Description: 新增兴趣标签
     * @Param: homeBlogTag
     * @return:
     */
    @Override
    public ReturnData addTag(@Valid @RequestBody HomeBlogTag homeBlogTag, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        blogTagService.add(homeBlogTag);
        redisUtils.expire(Constants.REDIS_KEY_HOMEBLOGTAG, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新兴趣标签
     * @Param: homeBlogTag
     * @return:
     */
    @Override
    public ReturnData updateTag(@Valid @RequestBody  HomeBlogTag homeBlogTag, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        blogTagService.update(homeBlogTag);
        redisUtils.expire(Constants.REDIS_KEY_HOMEBLOGTAG, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询兴趣标签列表
     * @return
     */
    @Override
    public ReturnData findBlogTaglist() {
        //从缓存中获取
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_HOMEBLOGTAG,0,-1);
        PageBean<HomeBlogTag> pageBean = null;
        if(list==null||list.size()<=0){//缓存中不存在 查询数据库
            pageBean = blogTagService.findList(0,100);
            if(pageBean==null){
                pageBean = new PageBean<HomeBlogTag>();
                pageBean.setList(new ArrayList<>());
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",pageBean);
            }
            if(pageBean.getList()!=null){
                //放入缓存中
                redisUtils.pushList(Constants.REDIS_KEY_HOMEBLOGTAG, pageBean.getList(), 0);
            }
        }else{
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(1);
            pageBean.setPageSize(list.size());
            pageBean.setList(list);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
