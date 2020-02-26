package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloorComment;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 楼店评论相关接口
 * author：ZhaoJiaJie
 * create time：2020-02-24 11:42:43
 */
public interface ShopFloorCommentApiController {
    /***
     * 添加楼店评论
     * @param shopFloorComment
     * @return
     */
    @PostMapping("addFloorComment")
    ReturnData addFloorComment(@Valid @RequestBody ShopFloorComment shopFloorComment, BindingResult bindingResult);

    /***
     * 删除楼店评论
     * @param id 评论ID
     * @param goodsId 商品ID
     * @return
     */
    @DeleteMapping("delFloorComment/{id}/{goodsId}")
    ReturnData delFloorComment(@PathVariable long id, @PathVariable long goodsId);

    /***
     * 查询楼店评论记录
     * @param goodsId     商品ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findFloorCommentList/{goodsId}/{page}/{count}")
    ReturnData findFloorCommentList(@PathVariable long goodsId, @PathVariable int page, @PathVariable int count);

    /***
     * 查询楼店指定评论下的回复记录接口
     * @param contentId     评论ID
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findFloorReplyList/{contentId}/{page}/{count}")
    ReturnData findFloorReplyList(@PathVariable long contentId, @PathVariable int page, @PathVariable int count);

}
