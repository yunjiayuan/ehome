package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.Version;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 版本号信息相关接口
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface VersionApiController {
    /***
     * 更新版本号
     * @param version
     * @return
     */
    @PutMapping("setVersion")
    ReturnData setVersion(@Valid @RequestBody Version version, BindingResult bindingResult);

    /***
     * 查询最新版本号
     * @return
     */
    @GetMapping("findVersion/{type}")
    ReturnData findVersion(@PathVariable int type);
}
