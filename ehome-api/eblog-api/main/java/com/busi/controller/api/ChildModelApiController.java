package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 儿童/青少年模式相关接口
 * author：zhaojiajie
 * create time：22020-06-16 14:07:52
 */
public interface ChildModelApiController {

    /***
     * 设置密码
     * @param homeAlbum
     * @return
     */
    @PostMapping("addChildPwd")
    ReturnData addChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult);

    /***
     * 更新密码
     * @param homeAlbum
     * @return
     */
    @PutMapping("modifyChildPwd")
    ReturnData modifyChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult);

    /***
     * 验证密码
     * @param password
     * @return
     */
    @GetMapping("ckChildPwd/{password}")
    ReturnData ckChildPwd(@PathVariable String password);

    /***
     * 查询是否设置密码
     * @param
     * @return
     */
    @GetMapping("getChildPwd")
    ReturnData getChildPwd();
}
