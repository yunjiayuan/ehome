package com.busi.controller.local;

import com.busi.controller.BaseController;
import com.busi.entity.ShippingAddress;
import com.busi.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @program: ehome
 * @description: 收货地址
 * @author: ZHaoJiaJie
 * @create: 2019-03-06 15:24
 */
@RestController
public class ShippingAddressLController extends BaseController implements ShippingAddressLocalController {

    @Autowired
    ShippingAddressService shippingAddressService;

    /***
     * 查询收货地址详情
     * @param id
     * @return
     */
    @Override
    public ShippingAddress findAddres(@PathVariable(value = "id") long id) {
        ShippingAddress is = shippingAddressService.findUserById(id);

        return is;
    }
}
