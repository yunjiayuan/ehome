package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


/**
 *  搬家接口
 * author：SunTianJie
 * create time：2018/10/10 9:41
 */
public interface HouseMovingApiController {

    /***
     * 搬家接口
     * @param homeNumber     目标门牌号 格式：0_1003017
     * @param targetPassword 目标密码  (32位md5码)
     * @param password       当前密码 (32位md5码)
     * @param houseMoving    搬家类型：0搬进  1搬离
     * @return
     */
    @GetMapping("houseMoving/{homeNumber}/{targetPassword}/{password}/{houseMoving}")
    ReturnData houseMoving(@PathVariable String homeNumber,@PathVariable String targetPassword,
                           @PathVariable String password,@PathVariable int houseMoving);
}
