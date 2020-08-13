package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.HotelOrder;
import com.busi.entity.HotelComment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 家门口酒店民宿订单相关接口
 * @author: ZhaoJiaJie
 * @create: 2020-08-03 17:06:12
 */
public interface HotelOrderApiController {
    /***
     * 新增订单
     * @param kitchenHotelOrders
     * @return
     */
    @PostMapping("addHotelOrder")
    ReturnData addHotelOrder(@Valid @RequestBody HotelOrder kitchenHotelOrders, BindingResult bindingResult);

    /***
     * 删除订单
     * @param id 订单ID
     * @return
     */
    @DeleteMapping("delHotelOrder/{id}")
    ReturnData delHotelOrder(@PathVariable long id);

    /***
     * 更改入住状态
     * 由未验票改为已验票
     * @param id  订单Id
     * @param voucherCode  凭证码
     * @return
     */
    @GetMapping("receiptHotel/{id}/{voucherCode}")
    ReturnData receiptHotel(@PathVariable long id, @PathVariable String voucherCode);

    /***
     * 更改订单状态
     * 由已验票改为已完成
     * @param id  订单Id
     * @return
     */
    @GetMapping("completeHotel/{id}")
    ReturnData completeHotel(@PathVariable long id);

    /***
     * 查看订单详情
     * @param no  订单编号
     * @return
     */
    @GetMapping("findHotelOrder/{no}")
    ReturnData findHotelOrder(@PathVariable String no);

    /***
     * 取消订单（更新订单类型）
     * @param id
     * @return
     */
    @GetMapping("cancelHotelOrders/{id}")
    ReturnData cancelHotelOrders(@PathVariable long id);

    /***
     * 订单管理条件查询
     * @param identity     身份区分：1买家 2商家
     * @param ordersType   订单类型:  订单类型:  0未付款（已下单未付款）1未验票(已付款未验票),2已验票,3已完成  4卖家取消订单 5用户取消订单
     * @param page         当前查询数据的页码
     * @param count        每页的显示条数
     * @return
     */
    @GetMapping("findHotelOrderList/{userId}/{identity}/{ordersType}/{page}/{count}")
    ReturnData findHotelOrderList(@PathVariable long userId, @PathVariable int identity, @PathVariable int ordersType, @PathVariable int page, @PathVariable int count);

    /***
     * 添加评论
     * @param shopHotelComment
     * @return
     */
    @PostMapping("addHotelComment")
    ReturnData addHotelComment(@Valid @RequestBody HotelComment shopHotelComment, BindingResult bindingResult);

    /***
     * 删除评论
     * @param id 评论ID
     * @param goodsId 景区ID
     * @return
     */
    @DeleteMapping("delHotelComment/{id}/{goodsId}")
    ReturnData delHotelComment(@PathVariable long id, @PathVariable long goodsId);

    /***
     * 查询评论记录
     * @param goodsId     景区ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findHotelCommentList/{goodsId}/{page}/{count}")
    ReturnData findHotelCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findHotelReplyList/{contentId}/{page}/{count}")
    ReturnData findHotelReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count);
}
