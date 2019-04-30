package com.busi.controller.api;

import com.busi.entity.ReturnData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 获取家主页信息接口
 * author：SunTianJie
 * create time：2018/7/16 13:48
 */
public interface HomePageInfoApiController {

    /***
     * 获取指定用户ID的家主页信息
     * @param userId
     * @return
     */
    @GetMapping("findHomePageInfo/{userId}")
    ReturnData findHomePageInfo(@PathVariable long userId);

    /***
     * 更新管理员权限中的相关操作
     * @param type   设置类型 type=0 修改“屏蔽主界面部分功能按钮”状态 type=1预留
     * @param status 状态值 0默认关闭  1开启
     * @return
     */
    @GetMapping("adminiSetUp/{type}/{status}")
    ReturnData adminiSetUp(@PathVariable int type,@PathVariable int status);
}
