package com.busi.controller.api;

import com.busi.entity.InteractiveGameLog;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import javax.validation.Valid;

/**
 * 互动游戏胜负记录接口
 * author：SunTianJie
 * create time：2018/8/22 9:57
 */
public interface InteractiveGameLogApiController {

    /***
     * 新增互动游戏胜负记录接口
     * @param interactiveGameLog
     * @param bindingResult
     * @return 返回胜负结果
     */
    @PostMapping("addInteractiveGameLog")
    ReturnData addInteractiveGameLog(@Valid @RequestBody InteractiveGameLog interactiveGameLog, BindingResult bindingResult);
}
