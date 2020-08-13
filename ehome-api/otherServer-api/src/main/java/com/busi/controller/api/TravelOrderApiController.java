package com.busi.controller.api;

import com.busi.entity.ScenicSpotComment;
import com.busi.entity.ScenicSpotOrder;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 家门口旅游订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-07-30 13:19:48
 */
public interface TravelOrderApiController {

    /***
     * 新增订单
     * @param kitchenTravelOrders
     * @return
     */
    @PostMapping("addTravelOrder")
    ReturnData addTravelOrder(@Valid @RequestBody ScenicSpotOrder kitchenTravelOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delTravelOrder/{id}")
    ReturnData delTravelOrder(@PathVariable long id);

    /***
     * 更改订单状态
     * 由未验票改为已验票
     * @param id  订单Id
     * @param voucherCode  凭证码
     * @return
     */
    @GetMapping("receiptTravel/{id}/{voucherCode}")
    ReturnData receiptTravel(@PathVariable long id, @PathVariable String voucherCode);

    /***
     * 更改订单状态
     * 由已验票改为已完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("completeTravel/{id}")
    ReturnData completeTravel(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("findTravelOrder/{no}")
    ReturnData findTravelOrder(@PathVariable String no);

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @GetMapping("cancelTravelOrders/{id}")
    ReturnData cancelTravelOrders(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型:  订单类型:  0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成  4卖家取消订单 5用户取消订单
     * @param page         当前查询数据的页码
     * @param count        每页的显示条数
     * @return
     */
    @GetMapping("findTravelOrderList/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findTravelOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 添加评论
     * @param shopTravelComment
     * @return
     */
    @PostMapping("addTravelComment")
    ReturnData addTravelComment(@Valid @RequestBody ScenicSpotComment shopTravelComment, BindingResult bindingResult);

    /***
     * 删除评论
     * @param id 评论ID
     * @param goodsId 景区ID
     * @return
     */
    @DeleteMapping("delTravelComment/{id}/{goodsId}")
    ReturnData delTravelComment(@PathVariable long id, @PathVariable long goodsId);

    /***
     * 查询评论记录
     * @param goodsId     景区ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findTravelCommentList/{goodsId}/{page}/{count}")
    ReturnData findTravelCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findTravelReplyList/{contentId}/{page}/{count}")
    ReturnData findTravelReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count);

}
