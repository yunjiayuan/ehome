package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/***
 * 新人分享红包信息相关接口
 * author：zhaojiajie
 * create time：2018-9-27 14:40:20
 */
public interface SharingPromotionApiController {

    /***
     * 查询红包记录
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findShareList/{page}/{count}")
    ReturnData findShareList(@PathVariable int page, @PathVariable int count);

    /***
     * 领红包
     * @param paymentKey  私钥
     * @param shareCode 分享码
     * @return
     */
    @GetMapping("receive/{paymentKey}/{shareCode}")
    ReturnData receive(@PathVariable String paymentKey, @PathVariable String shareCode);

}
