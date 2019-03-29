package com.busi.controller.local;

import com.busi.entity.GoodNumber;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 预售靓号相关业务接口(内部调用)
 * author：suntj
 * create time：2019-3-28 18:39:46
 */
public interface GoodNumberLocalController {

    /***
     * 新增靓号门牌号
     * @param goodNumber
     * @param bindingResult
     * @return
     */
    @PostMapping("addGoodNumber")
    ReturnData addGoodNumber(@Valid @RequestBody GoodNumber goodNumber, BindingResult bindingResult);

    /***
     * 更新靓号状态
     * @param goodNumber
     * @return
     */
    @PutMapping("updateGoodNumber")
    ReturnData updateGoodNumber(@RequestBody GoodNumber goodNumber);


}
