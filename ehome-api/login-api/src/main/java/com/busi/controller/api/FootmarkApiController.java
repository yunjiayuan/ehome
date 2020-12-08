package com.busi.controller.api;

import com.busi.entity.Footmarkauthority;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 足迹相关接口
 * author：zhaojiajie
 * create time：2018-9-29 13:01:12
 */
public interface FootmarkApiController {

    /***
     * 查询足迹列表
     * @param userId  用户ID
     * @param footmarkType  足迹类型 1.发布公告 2.发布家博 3.图片上传 4.音频上传 5.视频上传  6记事  0.默认全部
     * @param startTime  开始时间
     * @param endTime   结束时间
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findFootmarkList/{userId}/{footmarkType}/{startTime}/{endTime}/{page}/{count}")
    ReturnData findFootmarkList(@PathVariable long userId, @PathVariable int footmarkType, @PathVariable String startTime, @PathVariable String endTime, @PathVariable int page, @PathVariable int count);

    /**
     * @Description: 删除
     * @return:
     */
    @DeleteMapping("delFootmark/{userId}/{id}")
    ReturnData delFootmark(@PathVariable long userId, @PathVariable long id);

    /**
     * @Description: 设置权限
     * @Param: footmarkauthority
     * @return:
     */
    @PutMapping("setAffiche")
    ReturnData setAffiche(@Valid @RequestBody Footmarkauthority footmarkauthority, BindingResult bindingResult);

    /***
     * 查询权限详情
     * @return
     */
    @GetMapping("findAuthority")
    ReturnData findAuthority();

    /***
     * 查找房间
     * @param roomName  房间名称
     * @param lat  纬度
     * @param lon   经度
     * @return
     */
    @GetMapping("findFootRoom/{roomName}/{lat}/{lon}")
    ReturnData findFootRoom(@PathVariable String roomName, @PathVariable double lat, @PathVariable double lon);

}
