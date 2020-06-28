package com.busi.controller.api;

import com.busi.entity.*;
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

    /***
     * 申诉重置
     * @param homeAlbum
     * @return
     */
    @PostMapping("addChildPwdAppeal")
    ReturnData addChildPwdAppeal(@Valid @RequestBody ChildModelPwdAppeal homeAlbum, BindingResult bindingResult);

    /***
     * 查询申诉列表
     * @param page       页码 第几页 起始值1
     * @param count      每页条数
     * @return
     */
    @GetMapping("findChildAppealList/{page}/{count}")
    ReturnData findChildAppealList(@PathVariable int page, @PathVariable int count);

    /***
     * 更新申诉状态
     * @param homeAlbum
     * @return
     */
    @PutMapping("changeAppealState")
    ReturnData changeAppealState(@Valid @RequestBody ChildModelPwdAppeal homeAlbum, BindingResult bindingResult);
}
