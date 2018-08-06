package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadNotes;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户头像相册（主界面各房间封面）相关接口
 * author：SunTianJie
 * create time：2018/7/20 16:52
 */
public interface UserHeadNotesApiController {

    /***
     * 查询用户头像相册
     * @param userId 被查询者的用户ID
     * @return
     */
    @GetMapping("findUserHeadNote/{userId}")
    ReturnData findUserHeadNote(@PathVariable long userId);

    /***
     * 更换封面接口
     * @param userHeadNotes
     * @return
     */
    @PutMapping("updateUserHeadNoteCover")
    ReturnData updateUserHeadNoteCover (@Valid @RequestBody UserHeadNotes userHeadNotes, BindingResult bindingResult);

    /***
     * 更换欢迎视频接口
     * @param userHeadNotes
     * @return
     */
    @PutMapping("updateWelcomeVideo")
    ReturnData updateWelcomeVideo (@Valid @RequestBody UserHeadNotes userHeadNotes, BindingResult bindingResult);

}
