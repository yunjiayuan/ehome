package com.busi.controller.api;

import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadAlbum;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;

/**
 * 用户个人资料界面的九张头像相册 接口
 * author：SunTianJie
 * create time：2018/7/25 15:18
 */
public interface UserHeadAlbumApiController {
    /***
     * 查询用户九张头像相册
     * @param userId 被查询者的用户ID
     * @return
     */
    @GetMapping("findUserHeadAlbum/{userId}")
    ReturnData findUserHeadAlbum(@PathVariable long userId);

    /***
     * 更新接口
     * @param userHeadAlbum
     * @return
     */
    @PutMapping("updateUserHeadAlbum")
    ReturnData updateUserHeadAlbum (@Valid @RequestBody UserHeadAlbum userHeadAlbum, BindingResult bindingResult);
}
