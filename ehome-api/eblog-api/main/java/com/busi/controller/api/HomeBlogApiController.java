package com.busi.controller.api;

import com.busi.entity.HomeBlog;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 生活圈相关接口
 * author：SunTianJie
 * create time：2018/10/23 9:25
 */
public interface HomeBlogApiController {

    /***
     * 生活圈发布接口
     * @param homeBlog
     * @return
     */
    @PostMapping("addBlog")
    ReturnData addBlog(@Valid @RequestBody HomeBlog homeBlog, BindingResult bindingResult);

    /***
     * 根据生活圈ID查询生活圈详情接口
     * @param userId 被查询用户ID
     * @param blogId 被查询生活圈ID
     * @return
     */
    @GetMapping("findBlogInfo/{userId}/{blogId}")
    ReturnData findBlogInfo(@PathVariable long userId,@PathVariable long blogId);

    /***
     * 删除指定生活圈接口
     * @param userId 生活圈发布者用户ID
     * @param blogId 将要被删除的生活圈
     * @return
     */
    @DeleteMapping("delBlog/{userId}/{blogId}")
    ReturnData delBlog(@PathVariable long userId,@PathVariable long blogId);

    /***
     * 条件查询生活圈接口（查询所有类型的生活圈）
     * @param userId     被查询用户ID 默认0查询所有
     * @param searchType 查询类型 0查看朋友圈 1查看关注 2查看兴趣话题 3查询指定用户
     * @param tags       被查询兴趣标签ID组合，逗号分隔例如：1,2,3 仅当searchType=2 时有效 默认传null
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findBlogList/{userId}/{searchType}/{tags}/{page}/{count}")
    ReturnData findBlogList(@PathVariable long userId,@PathVariable int searchType,@PathVariable String tags,
                            @PathVariable int page, @PathVariable int count);

    /***
     * 条件查询生活秀接口（只查询生活秀内容）
     * @param userId     被查询用户ID 默认0查询所有
     * @param searchType 查询类型 0查询首页推荐 1查同城 2查看朋友 3查询关注 4查询兴趣标签 5查询指定用户
     * @param tags       被查询兴趣标签ID组合，逗号分隔例如：1,2,3  仅当searchType=4 时有效 默认传null
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findBlogVideoList/{userId}/{searchType}/{tags}/{page}/{count}")
    ReturnData findBlogVideoList(@PathVariable long userId,@PathVariable int searchType,@PathVariable String tags,
                            @PathVariable int page, @PathVariable int count);

}
