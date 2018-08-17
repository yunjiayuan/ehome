package com.busi.controller.api;

import com.busi.entity.Purse;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 钱包相关接口
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PurseApiController {

    /***
     * 查询用户钱包信息
     * @param userId 将要查询的用户ID
     * @return
     */
    @GetMapping("findPurseInfo/{userId}")
    ReturnData findPurseInfo(@PathVariable long userId);

}
