package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.OtherPosts;
import com.busi.service.OtherPostsService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;
import java.util.*;


/**
 * @program: 其他公告
 * @author: ZHaoJiaJie
 * @create: 2018-08-02 13:39
 */

@RestController
public class OtherPostsController extends BaseController implements OtherPostsApiController {

    @Autowired
    OtherPostsService otherPostsService;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    MqUtils mqUtils;


    /***
     * 新增
     * @param otherPosts
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addOther(@Valid @RequestBody OtherPosts otherPosts, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //计算公告分数
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(otherPosts.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(otherPosts.getContent());//内容

        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }

        otherPosts.setAuditType(2);
        otherPosts.setDeleteType(1);
        otherPosts.setFraction(fraction);
        otherPosts.setAddTime(new Date());
        otherPosts.setRefreshTime(new Date());
        otherPostsService.add(otherPosts);

        //新增任务
        mqUtils.sendTaskMQ(otherPosts.getUserId(),1,3);
        //新增足迹
        mqUtils.sendFootmarkMQ(otherPosts.getUserId(), otherPosts.getTitle(), null, null, null, otherPosts.getId() + "," + 6, 1);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", otherPosts.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + otherPosts.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 删除
     * @param id
     * @param userId 将要删除的userId
     * @return
     */
    @Override
    public ReturnData delOther(@PathVariable long id, @PathVariable long userId) {
        //验证参数
        if (userId <= 0 || id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //验证删除权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限删除用户[" + userId + "]的婚恋交友信息", new JSONObject());
        }
        //查询数据库
        OtherPosts posts = otherPostsService.findUserById(id);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        posts.setDeleteType(2);
        otherPostsService.updateDel(posts);
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + posts.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新
     * @param otherPosts
     * @return
     */
    @Override
    public ReturnData updateOther(@Valid @RequestBody OtherPosts otherPosts, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        //验证修改人权限
        if (CommonUtils.getMyId() != otherPosts.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + otherPosts.getUserId() + "]的其他公告信息", new JSONObject());
        }
        //计算公告分数
        int fraction = 0;//公告分数
        int num = CommonUtils.getStringLengsByByte(otherPosts.getTitle());//标题
        int num2 = CommonUtils.getStringLengsByByte(otherPosts.getContent());//内容

        if (num <= 5 * 2) {
            fraction += 5;
        }
        if (num > 5 * 2 && num <= 10 * 2) {
            fraction += 10;
        }
        if (num > 10 * 2 && num <= 20 * 2) {
            fraction += 20;
        }
        if (num > 20 * 2) {
            fraction += 30;
        }

        if (num2 <= 20 * 2) {
            fraction += 5;
        }
        if (num2 > 20 * 2 && num2 <= 50 * 2) {
            fraction += 10;
        }
        if (num2 > 50 * 2 && num2 <= 80 * 2) {
            fraction += 20;
        }
        if (num2 > 80 * 2) {
            fraction += 30;
        }

        otherPosts.setFraction(fraction);
        otherPosts.setRefreshTime(new Date());
        otherPostsService.update(otherPosts);

        Map<String, Object> map = new HashMap<>();
        map.put("infoId", otherPosts.getId());
        //清除缓存中的信息
        redisUtils.expire(Constants.REDIS_KEY_IPS_OTHERPOSTS + otherPosts.getId(), 0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /**
     * 查询
     * @param id
     * @return
     */
    @Override
    public ReturnData getOther(@PathVariable long id) {
        //验证参数
        if (id <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //查询缓存 缓存中不存在 查询数据库
        Map<String, Object> otherPostsMap = redisUtils.hmget(Constants.REDIS_KEY_IPS_OTHERPOSTS + id);
        if (otherPostsMap == null || otherPostsMap.size() <= 0) {
            OtherPosts posts = otherPostsService.findUserById(id);
            if (posts == null) {
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            //新增浏览记录
            mqUtils.sendLookMQ(CommonUtils.getMyId(),id,posts.getTitle(),6);
            //放入缓存
            otherPostsMap = CommonUtils.objectToMap(posts);
            redisUtils.hmset(Constants.REDIS_KEY_IPS_OTHERPOSTS + posts.getId(), otherPostsMap, Constants.USER_TIME_OUT);
        }
        //新增浏览记录
        mqUtils.sendLookMQ(CommonUtils.getMyId(),id,otherPostsMap.get("title").toString(),1);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", otherPostsMap);
    }

    /***
     * 分页查询接口
     * @param userId  用户ID
     * @param page   页码 第几页 起始值1
     * @param count  每页条数
     * @return
     */
    @Override
    public ReturnData findOtherList(@PathVariable long userId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        if (userId < 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误", new JSONObject());
        }
        //开始查询
        PageBean<OtherPosts> pageBean;
        pageBean = otherPostsService.findList(userId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        List<OtherPosts> otherPosts = new ArrayList<>();
        otherPosts = pageBean.getList();
        Collections.sort(otherPosts, new Comparator<OtherPosts>() {
            @Override
            public int compare(OtherPosts o1, OtherPosts o2) {
                // 按照置顶等级进行降序排列
                if (o1.getFrontPlaceType() > o2.getFrontPlaceType()) {
                    return -1;
                }
                if (o1.getFrontPlaceType() == o2.getFrontPlaceType()) {
                    return 0;
                }
                return 1;
            }
        });
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, pageBean);
    }

}
