package com.busi.controller.api;

import com.busi.entity.AdvertPic;
import com.busi.entity.ReturnData;
import com.busi.entity.Version;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    /***
     * 查询过渡页
     * @param type 0表示苹果 1表示安卓
     * @return
     */
    @GetMapping("findAdvertPic/{type}")
    ReturnData findAdvertPic(@PathVariable int type);

    /***
     * 更新过渡页
     * @param advertPic
     * @return
     */
    @PutMapping("setAdvertPic")
    ReturnData setAdvertPic(@Valid @RequestBody AdvertPic advertPic , BindingResult bindingResult);
}
