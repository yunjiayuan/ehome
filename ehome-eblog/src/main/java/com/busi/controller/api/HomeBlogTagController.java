package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.HomeBlogUserTag;
import com.busi.entity.ReturnData;
import com.busi.service.HomeBlogTagService;
import com.busi.utils.CommonUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: ehome
 * @description: 生活圈兴趣标签
 * @author: ZHaoJiaJie
 * @create: 2018-11-02 17:05
 */
@RestController
public class HomeBlogTagController extends BaseController implements HomeBlogTagApiController {

    @Autowired
    private HomeBlogTagService homeBlogTagService;

    /**
     * @Description: 更新兴趣标签
     * @Param: homeBlogUserTag
     * @return:
     */
    @Override
    public ReturnData editUserTag(@Valid @RequestBody HomeBlogUserTag homeBlogUserTag, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        HomeBlogUserTag userTag = homeBlogTagService.find(homeBlogUserTag.getUserId());
        if (userTag == null) {
            homeBlogTagService.add(homeBlogUserTag);
        } else {
            if (!CommonUtils.checkFull(homeBlogUserTag.getTags())) {
                String[] ids = homeBlogUserTag.getTags().split(",");
                if (ids.length > 10 || ids.length < 3) {
                    return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "标签数量超过上限", new JSONObject());
                }
            }
            homeBlogTagService.update(homeBlogUserTag);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询兴趣标签
     * @param userId     查询用户ID
     * @return
     */
    @Override
    public ReturnData findTaglist(@PathVariable long userId) {
        HomeBlogUserTag userTag = homeBlogTagService.find(userId);
        Map<String, String> map = new HashMap<>();
        if (userTag == null) {
            map.put("tag", "0,1,2,3,4");
        } else {
            map.put("tag", userTag.getTags());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
