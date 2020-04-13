package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.ShopFloorCommentService;
import com.busi.service.ShopFloorGoodsService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.*;

/**
 * 楼店评论相关接口
 * author：ZhaoJiaJie
 * create time：2020-02-24 11:42:43
 */
@RestController
public class ShopFloorCommentController extends BaseController implements ShopFloorCommentApiController {

    @Autowired
    private UserInfoUtils userInfoUtils;

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    private ShopFloorGoodsService goodsCenterService;

    @Autowired
    private ShopFloorCommentService shopFloorCommentService;

    /***
     * 添加楼店评论
     * @param shopFloorComment
     * @return
     */
    @Override
    public ReturnData addFloorComment(@Valid @RequestBody ShopFloorComment shopFloorComment, BindingResult bindingResult) {
        //查询该商品信息
        ShopFloorGoods posts = null;
        posts = goodsCenterService.findUserById(shopFloorComment.getGoodsId());
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //处理特殊字符
        String content = shopFloorComment.getContent();
        if (!CommonUtils.checkFull(content)) {
            String filteringContent = CommonUtils.filteringContent(content);
            if (CommonUtils.checkFull(filteringContent)) {
                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "评论内容不能为空并且不能包含非法字符！", new JSONArray());
            }
            shopFloorComment.setContent(filteringContent);
        }
        shopFloorComment.setTime(new Date());
        shopFloorCommentService.addComment(shopFloorComment);
        if (shopFloorComment.getReplyType() == 0) {//新增评论
            //放入缓存(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + shopFloorComment.getGoodsId(), shopFloorComment, Constants.USER_TIME_OUT);
        } else {//新增回复
            List list = null;
            //先添加到缓存集合(七天失效)
            redisUtils.addListLeft(Constants.REDIS_KEY_SHOPFLOOR_REPLY + shopFloorComment.getFatherId(), shopFloorComment, Constants.USER_TIME_OUT);
            //再保证5条数据
            list = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_REPLY + shopFloorComment.getFatherId(), 0, -1);
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_REPLY + shopFloorComment.getFatherId(), 0);
            if (list != null && list.size() > 5) {//限制五条回复
                //缓存中获取最新五条回复
                ShopFloorComment message = null;
                List<ShopFloorComment> messageList = new ArrayList<>();
                for (int j = 0; j < list.size(); j++) {
                    if (j < 5) {
                        message = (ShopFloorComment) list.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                if (messageList.size() == 5) {
                    redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_REPLY + shopFloorComment.getFatherId(), messageList, 0);
                }
            }
            //更新回复数
            ShopFloorComment num = shopFloorCommentService.findById(shopFloorComment.getFatherId());
            if (num != null) {
                shopFloorComment.setReplyNumber(num.getReplyNumber() + 1);
                shopFloorCommentService.updateCommentNum(shopFloorComment);
            }
        }
        //更新评论数
        posts.setCommentNumber(posts.getCommentNumber() + 1);
        shopFloorCommentService.updateBlogCounts(posts);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除楼店评论
     * @param id 评论ID
     * @param goodsId 商品ID
     * @return
     */
    @Override
    public ReturnData delFloorComment(@PathVariable long id, @PathVariable long goodsId) {
        List list = null;
        List list2 = null;
        List list3 = null;
        List messList = null;
        ShopFloorComment comment = shopFloorCommentService.findById(id);
        if (comment == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "评论不存在", new JSONArray());
        }
        //查询该商品信息
        ShopFloorGoods posts = null;
        posts = goodsCenterService.findUserById(goodsId);
        if (posts == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "商品不存在", new JSONObject());
        }
        //判断操作人权限
        long userId = comment.getUserId();//评论者ID
        long myId = CommonUtils.getMyId();//登陆者ID
        if (myId != userId) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "参数有误，当前用户[" + myId + "]无权限删除用户[" + goodsId + "]", new JSONObject());
        }
        comment.setReplyStatus(1);//1删除
        shopFloorCommentService.update(comment);
        //同时删除此评论下回复
        String ids = "";
        messList = shopFloorCommentService.findMessList(id);
        if (messList != null && messList.size() > 0) {
            for (int i = 0; i < messList.size(); i++) {
                ShopFloorComment message = null;
                message = (ShopFloorComment) messList.get(i);
                if (message != null) {
                    ids += message.getId() + ",";
                }
            }
            //更新回复删除状态
            shopFloorCommentService.updateReplyState(ids.split(","));
        }
        //更新商品评论数
        int num = messList.size();
        posts.setCommentNumber(posts.getCommentNumber() - num - 1);
        shopFloorCommentService.updateBlogCounts(posts);
        if (comment.getReplyType() == 0) {
            //获取缓存中评论列表
            list = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, 0, -1);
            if (list != null && list.size() > 0) {
                for (int i = 0; i < list.size(); i++) {
                    ShopFloorComment comment2 = (ShopFloorComment) list.get(i);
                    if (comment2.getId() == id) {
                        //更新评论缓存
                        redisUtils.removeList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, 1, comment2);
                        //更新此评论下的回复缓存
                        redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_REPLY + comment.getFatherId(), 0);
                        break;
                    }
                }
            }
        } else {
            List<ShopFloorComment> messageList = new ArrayList<>();
            //清除缓存中的回复信息
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_REPLY + comment.getFatherId(), 0);
            //清除缓存中评论列表
            redisUtils.expire(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, 0);
            //数据库获取最新五条回复
            list2 = shopFloorCommentService.findMessList(comment.getFatherId());
            if (list2 != null && list2.size() > 0) {
                ShopFloorComment message = null;
                for (int j = 0; j < list2.size(); j++) {
                    if (j < 5) {
                        message = (ShopFloorComment) list2.get(j);
                        if (message != null) {
                            messageList.add(message);
                        }
                    }
                }
                redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_REPLY + comment.getFatherId(), messageList, 0);
            }
            //更新回复数
            ShopFloorComment floorComment = shopFloorCommentService.findById(comment.getFatherId());
            if (floorComment != null) {
                floorComment.setReplyNumber(floorComment.getReplyNumber() - 1);
                shopFloorCommentService.updateCommentNum(floorComment);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询楼店评论记录接口
     * @param goodsId     商品ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findFloorCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //获取缓存中评论列表
        List list = null;
        List list2 = null;
        List commentList = null;
        List commentList2 = null;
        List<ShopFloorComment> messageArrayList = new ArrayList<>();
        PageBean<ShopFloorComment> pageBean = null;
        long countTotal = redisUtils.getListSize(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId);
        int pageCount = page * count;
        if (pageCount > countTotal) {
            pageCount = -1;
        } else {
            pageCount = pageCount - 1;
        }
        commentList = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, (page - 1) * count, pageCount);
        //获取数据库中评论列表
        if (commentList == null || commentList.size() < count) {
            pageBean = shopFloorCommentService.findList(goodsId, page, count);
            commentList2 = pageBean.getList();
            if (commentList2 != null && commentList2.size() > 0) {
                for (int i = 0; i < commentList2.size(); i++) {
                    ShopFloorComment comment = null;
                    comment = (ShopFloorComment) commentList2.get(i);
                    if (comment != null) {
                        for (int j = 0; j < commentList.size(); j++) {
                            ShopFloorComment comment2 = null;
                            comment2 = (ShopFloorComment) commentList.get(j);
                            if (comment2 != null) {
                                if (comment.getId() == comment2.getId()) {
                                    redisUtils.removeList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, 1, comment2);
                                }
                            }
                        }
                    }
                }
                //更新缓存
                redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, commentList2, Constants.USER_TIME_OUT);
                //获取最新缓存
                commentList = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_COMMENT + goodsId, (page - 1) * count, page * count);
            }
        }
        if (commentList == null) {
            commentList = new ArrayList();
        }
        for (int j = 0; j < commentList.size(); j++) {//评论
            UserInfo userInfo = null;
            ShopFloorComment comment = null;
            comment = (ShopFloorComment) commentList.get(j);
            if (comment != null) {
                userInfo = userInfoUtils.getUserInfo(comment.getUserId());
                if (userInfo != null) {
                    comment.setUserHead(userInfo.getHead());
                    comment.setUserName(userInfo.getName());
                    comment.setHouseNumber(userInfo.getHouseNumber());
                    comment.setProTypeId(userInfo.getProType());
                }
                //获取缓存中回复列表
                list = redisUtils.getList(Constants.REDIS_KEY_SHOPFLOOR_REPLY + comment.getId(), 0, -1);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {//回复
                        ShopFloorComment message = null;
                        message = (ShopFloorComment) list.get(i);
                        if (message != null) {
                            userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                            if (userInfo != null) {
                                message.setReplayName(userInfo.getName());
                            }
                            userInfo = userInfoUtils.getUserInfo(message.getUserId());
                            if (userInfo != null) {
                                message.setUserName(userInfo.getName());
                            }
                        }
                    }
                    comment.setMessageList(list);
                } else {
                    //查询数据库 （获取最新五条回复）
                    list2 = shopFloorCommentService.findMessList(comment.getId());
                    if (list2 != null && list2.size() > 0) {
                        ShopFloorComment message = null;
                        for (int l = 0; l < list2.size(); l++) {
                            if (l < 5) {
                                message = (ShopFloorComment) list2.get(l);
                                if (message != null) {
                                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                                    if (userInfo != null) {
                                        message.setReplayName(userInfo.getName());
                                    }
                                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                                    if (userInfo != null) {
                                        message.setUserName(userInfo.getName());
                                    }
                                    messageArrayList.add(message);
                                }
                            }
                        }
                        comment.setMessageList(messageArrayList);
                        //更新缓存
                        redisUtils.pushList(Constants.REDIS_KEY_SHOPFLOOR_REPLY + comment.getId(), messageArrayList, 0);
                    }
                }
            }
        }
        pageBean = new PageBean<>();
        pageBean.setSize(commentList.size());
        pageBean.setPageNum(page);
        pageBean.setPageSize(count);
        pageBean.setList(commentList);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询楼店指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @Override
    public ReturnData findFloorReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        List list = null;
        PageBean<ShopFloorComment> pageBean = null;
        pageBean = shopFloorCommentService.findReplyList(contentId, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        long num = 0;
        UserInfo userInfo = null;
        list = pageBean.getList();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {//回复
                ShopFloorComment message = null;
                message = (ShopFloorComment) list.get(i);
                if (message != null) {
                    userInfo = userInfoUtils.getUserInfo(message.getReplayId());
                    if (userInfo != null) {
                        message.setReplayName(userInfo.getName());
                    }
                    userInfo = userInfoUtils.getUserInfo(message.getUserId());
                    if (userInfo != null) {
                        message.setUserHead(userInfo.getHead());
                        message.setUserName(userInfo.getName());
                        message.setProTypeId(userInfo.getProType());
                        message.setHouseNumber(userInfo.getHouseNumber());
                    }
                }
            }
            //消息
            num = shopFloorCommentService.getReplayCount(contentId);
        }
        Map<String, Object> map = new HashMap<>();
        map.put("num", num);
        map.put("list", list);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

}
