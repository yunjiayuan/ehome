package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.CommunityResidentTag;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.CommunityResidentTagService;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: ehome
 * @description: 居委会居民标签相关
 * @author: ZHaoJiaJie
 * @create: 2020-03-17 17:07:43
 */
@RestController
public class CommunityResidentTagController extends BaseController implements CommunityResidentTagApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private CommunityResidentTagService residentTagService;

    /**
     * @Description: 新增标签
     * @Param: residentTag
     * @return:
     */
    @Override
    public ReturnData addTag(@Valid @RequestBody CommunityResidentTag residentTag, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        residentTagService.add(residentTag);
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_TAG + residentTag.getCommunityId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新标签
     * @Param: residentTag
     * @return:
     */
    @Override
    public ReturnData updateTag(@Valid @RequestBody CommunityResidentTag residentTag, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        residentTagService.update(residentTag);
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_TAG + residentTag.getCommunityId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询标签列表
     * @return
     */
    @Override
    public ReturnData findTaglist(@PathVariable long id) {
        //从缓存中获取
        List list = null;
        list = redisUtils.getList(Constants.REDIS_KEY_COMMUNITY_TAG + id, 0, -1);
        PageBean<CommunityResidentTag> pageBean = null;
        if (list == null || list.size() <= 0) {//缓存中不存在 查询数据库
            pageBean = residentTagService.findList(id, 0, 100);
            if (pageBean == null) {
                pageBean = new PageBean<CommunityResidentTag>();
                pageBean.setList(new ArrayList<>());
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
            }
            if (pageBean.getList() != null) {
                //放入缓存中
                redisUtils.pushList(Constants.REDIS_KEY_COMMUNITY_TAG + id, pageBean.getList(), 0);
            }
        } else {
            pageBean = new PageBean<>();
            pageBean.setSize(list.size());
            pageBean.setPageNum(1);
            pageBean.setPageSize(list.size());
            pageBean.setList(list);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /**
     * @param ids
     * @Description: 删除标签
     * @return:
     */
    @Override
    public ReturnData delTags(@PathVariable long communityId, @PathVariable String ids) {
        residentTagService.delTags(ids.split(","));
        redisUtils.expire(Constants.REDIS_KEY_COMMUNITY_TAG + communityId, 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }
}
