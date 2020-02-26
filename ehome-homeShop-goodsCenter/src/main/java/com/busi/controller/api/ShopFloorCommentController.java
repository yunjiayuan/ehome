//package com.busi.controller.api;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import com.busi.controller.BaseController;
//import com.busi.entity.ReturnData;
//import com.busi.entity.ShopFloorComment;
//import com.busi.entity.ShopFloorGoods;
//import com.busi.service.ShopFloorGoodsService;
//import com.busi.utils.CommonUtils;
//import com.busi.utils.Constants;
//import com.busi.utils.RedisUtils;
//import com.busi.utils.StatusCode;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestBody;
//
//import javax.validation.Valid;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//
///**
// * 楼店评论相关接口
// * author：ZhaoJiaJie
// * create time：2020-02-24 11:42:43
// */
//public class ShopFloorCommentController extends BaseController implements ShopFloorCommentApiController {
//
//    @Autowired
//    RedisUtils redisUtils;
//
//    @Autowired
//    private ShopFloorGoodsService goodsCenterService;
//
//    @Autowired
//    private ShopFloorCommentService shopFloorCommentService;
//
//    /***
//     * 添加楼店评论
//     * @param shopFloorComment
//     * @return
//     */
//    @Override
//    public ReturnData addFloorComment(@Valid @RequestBody ShopFloorComment shopFloorComment, BindingResult bindingResult) {
//        //查询该商品信息
//        ShopFloorGoods posts = null;
//        posts = goodsCenterService.findUserById(shopFloorComment.getGoodsId());
//        if (posts == null) {
//            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
//        }
//        //处理特殊字符
//        String content = shopFloorComment.getContent();
//        if (!CommonUtils.checkFull(content)) {
//            String filteringContent = CommonUtils.filteringContent(content);
//            if (CommonUtils.checkFull(filteringContent)) {
//                return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "评论内容不能为空并且不能包含非法字符！", new JSONArray());
//            }
//            shopFloorComment.setContent(filteringContent);
//        }
//        shopFloorComment.setTime(new Date());
//        shopFloorCommentService.addComment(shopFloorComment);
//
//        if (shopFloorComment.getReplyType() == 0) {//新增评论
//            //放入缓存(七天失效)
//            redisUtils.addListLeft(Constants.REDIS_KEY_EBLOG_COMMENT + shopFloorComment.getGoodsId(), shopFloorComment, Constants.USER_TIME_OUT);
//        } else {//新增回复
//            List list = null;
//            //先添加到缓存集合(七天失效)
//            redisUtils.addListLeft(Constants.REDIS_KEY_EBLOG_REPLY + shopFloorComment.getFatherId(), shopFloorComment, Constants.USER_TIME_OUT);
//            //再保证5条数据
//            list = redisUtils.getList(Constants.REDIS_KEY_EBLOG_REPLY + shopFloorComment.getFatherId(), 0, -1);
//            //清除缓存中的回复信息
//            redisUtils.expire(Constants.REDIS_KEY_EBLOG_REPLY + shopFloorComment.getFatherId(), 0);
//            if (list != null && list.size() > 5) {//限制五条回复
//                //缓存中获取最新五条回复
//                ShopFloorComment message = null;
//                List<ShopFloorComment> messageList = new ArrayList<>();
//                for (int j = 0; j < list.size(); j++) {
//                    if (j < 5) {
//                        message = (ShopFloorComment) list.get(j);
//                        if (message != null) {
//                            messageList.add(message);
//                        }
//                    }
//                }
//                if (messageList.size() == 5) {
//                    redisUtils.pushList(Constants.REDIS_KEY_EBLOG_REPLY + shopFloorComment.getFatherId(), messageList, 0);
//                }
//            }
//            //更新回复数
//            long num = 0;
//            num = shopFloorCommentService.findFatherId(shopFloorComment.getFatherId());
//            comment.setReplyNumber(num + 1);
//            shopFloorCommentService.updateCommentCounts(comment);
//        }
//        //更新评论数
//        posts.setCommentNumber(posts.getCommentNumber() + 1);
//        goodsCenterService.updateBlogCounts(posts);
//
//        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
//    }
//
//    /***
//     * 删除楼店评论
//     * @param id 评论ID
//     * @param goodsId 商品ID
//     * @return
//     */
//    @Override
//    public ReturnData delFloorComment(@PathVariable long id, @PathVariable long goodsId) {
//        return null;
//    }
//
//    /***
//     * 查询楼店评论记录接口
//     * @param goodsId     商品ID
//     * @param page       页码 第几页 起始值1
//     * @param count      每页条数
//     * @return
//     */
//    @Override
//    public ReturnData findFloorCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count) {
//        return null;
//    }
//
//    /***
//     * 查询楼店指定评论下的回复记录接口
//     * @param contentId     评论ID
//     * @param page       页码 第几页 起始值1
//     * @param count      每页条数
//     * @return
//     */
//    @Override
//    public ReturnData findFloorReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count) {
//        return null;
//    }
//}
