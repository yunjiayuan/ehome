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
     * 生活圈视频稿费评级
     * @param blogId 生活圈ID
     * @param userId 生活圈主任ID 注意不是当前登录这ID
     * @param grade 1是一级稿费作品 2是二级稿费作品 3是三级稿费作品 4是四级稿费作品
     * @param type  0:默认随机给钱 1:当前等级范围内最低金额 2:自定义金额
     * @param money  当type=2时，此字段有效
     * @return
     */
    @GetMapping("gradeBlog/{grade}")
    ReturnData gradeBlog(@PathVariable long userId,@PathVariable long blogId,@PathVariable int grade,@PathVariable int type,@PathVariable double money);

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
     * @param searchType 查询类型 0查询首页推荐 1查同城 2查看朋友 3查询关注 4查询兴趣标签 5查询指定用户 6查询附近的生活圈（家门口的生活圈）
     * @param tags       被查询兴趣标签ID组合，逗号分隔例如：1,2,3  仅当searchType=4 时有效 默认传null
     * @param cityId     城市ID 当searchType=1时有效 默认传0
     * @param lat        纬度 小数点后6位 当searchType=6时有效
     * @param lon        经度 小数点后6位 当searchType=6时有效
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findBlogVideoList/{userId}/{searchType}/{tags}/{cityId}/{lat}/{lon}/{page}/{count}")
    ReturnData findBlogVideoList(@PathVariable long userId,@PathVariable int searchType,
                                 @PathVariable String tags,@PathVariable int cityId,
                                 @PathVariable double lat,@PathVariable double lon,
                                 @PathVariable int page, @PathVariable int count);

    /***
     * 检测当前登录用户与被检测用户之间的好友关系和关注关系
     * @param userId     被检测的用户ID
     * @return
     */
    @GetMapping("checkFirendAndFollowStatus/{userId}")
    ReturnData checkFirendAndFollowStatus(@PathVariable long userId);

}
