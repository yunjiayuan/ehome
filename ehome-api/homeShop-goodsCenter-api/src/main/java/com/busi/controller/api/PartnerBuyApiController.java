package com.busi.controller.api;

import com.busi.entity.GoodsDescribe;
import com.busi.entity.HomeShopGoods;
import com.busi.entity.PartnerBuyGoods;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 合伙购相关接口
 * author：ZhaoJiaJie
 * create time：2020-04-17 17:25:58
 */
public interface PartnerBuyApiController {

    /***
     * 发起合伙购
     * @param partnerBuyGoods
     * @return
     */
    @PostMapping("addPartnerBuy")
    ReturnData addPartnerBuy(@Valid @RequestBody PartnerBuyGoods partnerBuyGoods, BindingResult bindingResult);

    /***
     * 查询列表
     * @param sort  查询条件:0全部，1我发起的，2我参与的
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findPartnerBuyList/{sort}/{page}/{count}")
    ReturnData findPartnerBuyList(@PathVariable int sort, @PathVariable int page, @PathVariable int count);

    /***
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping("getPartnerBuy/{id}")
    ReturnData getPartnerBuy(@PathVariable long id);

    /***
     * 加入合伙购
     * @param no 订单编号
     * @param id 合伙购ID
     * @return
     */
    @GetMapping("joinPartnerBuy/{no}/{id}")
    ReturnData joinPartnerBuy(@PathVariable String no, @PathVariable long id);

    /***
     * 查看合伙购人员
     * @param id
     * @return
     */
//    @GetMapping("getPartner/{id}")
//    ReturnData getPartner(@PathVariable long id);

    /***
     * 更新合伙购状态
     * @param partnerBuyGoods
     * @return
     */
    @PutMapping("changePartnerBuy")
    ReturnData changePartnerBuy(@Valid @RequestBody PartnerBuyGoods partnerBuyGoods, BindingResult bindingResult);
}
