package com.busi.controller.local;

import com.busi.entity.HouseNumber;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.*;

/**
 * 门牌号相关接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface HouseNumberLocalController {

    /***
     * 更新门牌号记录接口
     * @param houseNumber
     * @return
     */
    @PutMapping("updateHouseNumber")
    ReturnData updateHouseNumber(@RequestBody HouseNumber houseNumber);

}
