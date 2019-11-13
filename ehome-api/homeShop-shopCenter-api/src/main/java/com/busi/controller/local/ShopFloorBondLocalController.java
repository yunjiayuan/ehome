package com.busi.controller.local;

import com.busi.entity.ReturnData;
import com.busi.entity.ShopFloor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @program: ehome
 * @description: 楼店保证金
 * @author: ZHaoJiaJie
 * @create: 2019-04-26 11:40
 */
public interface ShopFloorBondLocalController {

    /**
     * @Description: 更新订单支付状态
     * @Param: shopFloor
     * @return:
     */
    @PutMapping("updatePayStates")
    ReturnData updatePayStates(@RequestBody ShopFloor shopFloor);
}
