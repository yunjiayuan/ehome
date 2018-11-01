package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.validation.Valid;

/**
 * 生活圈 点赞 相关接口
 * author：SunTianJie
 * create time：2018/10/23 10:35
 */
@RestController
public class HomeBlogLikeController extends BaseController implements HomeBlogLikeApiController {


    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private MqUtils mqUtils;


    /***
     * 新增点赞接口
     * @param homeBlogLike
     * @return
     */
    @Override
    public ReturnData addHomeBlogLike(@Valid @RequestBody HomeBlogLike homeBlogLike, BindingResult bindingResult) {
        return null;
    }

    /***
     * 删除点赞接口
     * @param userId 将要删除的点赞用户ID
     * @param blogId 将要操作的生活圈ID
     * @return
     */
    @Override
    public ReturnData delHomeBlogLike(@PathVariable long userId,@PathVariable long blogId) {
        return null;
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
        return null;
    }
}
