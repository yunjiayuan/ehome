package com.busi.controller.local;

import com.busi.entity.PurseChangingLog;
import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 钱包交易明细相关接口(本地内部调用)
 * author：SunTianJie
 * create time：2018-8-16 09:46:30
 */
public interface PurseChangingLogLocalController {

    /***
     * 新增用户账户交易明细接口
     * @param purseChangingLog
     * @return
     */
    @PostMapping("addPurseChangingLog")
    ReturnData addPurseChangingLog(@Valid @RequestBody PurseChangingLog purseChangingLog);

}
