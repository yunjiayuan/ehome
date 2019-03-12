package com.busi.utils;

import com.busi.entity.ShippingAddress;
import com.busi.fegin.ShippingAddressLControllerFegin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @program: ehome
 * @description: 收货地址工具类
 * @author: ZHaoJiaJie
 * @create: 2019-03-06 16:17
 */
@Component
public class ShippingAddressUtils {

    @Autowired
    private ShippingAddressLControllerFegin shippingAddressLControllerFegin;

    /***
     * 查询收货地址详情
     * @param id
     * @return
     */
    public ShippingAddress findAddress(long id) {
        ShippingAddress is = shippingAddressLControllerFegin.findAddres(id);

        return is;
    }

}
