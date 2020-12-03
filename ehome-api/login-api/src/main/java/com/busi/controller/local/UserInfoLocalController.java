package com.busi.controller.local;


import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadNotes;
import com.busi.entity.UserInfo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;

/**
 * 用户信息相关接口（内部调用）
 * author：SunTianJie
 * create time：2018/6/7 16:02
 */
public interface UserInfoLocalController {

    /***
     * 查询用户信息
     * @param userId   根据用户ID组合查询
     * @return
     */
    @GetMapping("getUserInfo/{userId}")
    UserInfo getUserInfo(@PathVariable(value="userId") long userId);

    /***
     * 查询用户信息
     * @param houseNumber 根据门牌号组合查询
     * @return
     */
    @GetMapping("getUserInfoByHouseNumber/{houseNumber}")
    UserInfo getUserInfoByHouseNumber(@PathVariable(value = "houseNumber") String houseNumber);

    /***
     * 更新用户新人标识
     * @param userInfo
     * @return
     */
    @PutMapping("updateIsNew")
    ReturnData updateIsNew(@RequestBody UserInfo userInfo);

    /***
     * 更新用户V认证标识
     * @param userInfo
     * @return
     */
    @PutMapping("updateUserCe")
    ReturnData updateUserCe(@RequestBody UserInfo userInfo);

    /***
     * 更新用户找人倾诉状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateTalkToSomeoneStatus")
    ReturnData updateTalkToSomeoneStatus(@RequestBody UserInfo userInfo);

    /***
     * 更新用户聊天互动状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateChatnteractionStatus")
    ReturnData updateChatnteractionStatus(@RequestBody UserInfo userInfo);

    /***
     * 更新生活圈首次视频发布状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateHomeBlogStatus")
    ReturnData updateHomeBlogStatus(@RequestBody UserInfo userInfo);

    /***
     * 更新用户手机号绑定状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateBindPhone")
    ReturnData updateBindPhone(@RequestBody UserInfo userInfo);

    /***
     * 更新用户第三方平台账号绑定状态
     * @param userInfo
     * @return
     */
    @PutMapping("updateBindOther")
    ReturnData updateBindOther(@RequestBody UserInfo userInfo);

    /***
     * 更换欢迎视频接口(仅用于发布生活圈视频时更新机器人欢迎视频功能 刷假数据)
     * @param userHeadNotes
     * @return
     */
    @PutMapping("updateWelcomeVideoByHomeBlog")
    ReturnData updateWelcomeVideoByHomeBlog (@RequestBody UserHeadNotes userHeadNotes);

    /***
     * 新增靓号接口(本地调用)
     * @param userInfo
     * @return
     */
    @PutMapping("addGoodNumberToUser")
    ReturnData addGoodNumberToUser(@RequestBody UserInfo userInfo);

}
