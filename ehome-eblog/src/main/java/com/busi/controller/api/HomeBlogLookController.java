package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 更新生活圈浏览量
 * @author: ZHaoJiaJie
 * @create: 2018-11-08 17:02
 */
@RestController
public class HomeBlogLookController extends BaseController implements HomeBlogLookApiController {

    @Autowired
    private MqUtils mqUtils;

    /***
     * 更新浏览量接口
     * @param userId 生活圈用户ID
     * @return
     */
    @Override
    public ReturnData updateLook(@PathVariable long userId, @PathVariable long blogId) {
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        if(blogId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"blogId参数有误",new JSONObject());
        }
        //更新评论数
        mqUtils.updateBlogCounts(userId, blogId, 3, 1);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
