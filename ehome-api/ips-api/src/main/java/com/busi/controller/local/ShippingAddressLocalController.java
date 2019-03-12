package com.busi.controller.local;

import com.busi.entity.ShippingAddress;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @program: ehome
 * @description: 收货地址相关接口（内部调用）
 * @author: ZHaoJiaJie
 * @create: 2019-03-06 15:21
 */
public interface ShippingAddressLocalController {

    /***
     * 查询收货地址
     * @param id
     * @return
     */
    @GetMapping("findAddres/{id}")
    ShippingAddress findAddres(@PathVariable(value = "id") long id);
}
