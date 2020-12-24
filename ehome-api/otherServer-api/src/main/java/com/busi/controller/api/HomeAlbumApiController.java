package com.busi.controller.api;

import com.busi.entity.HomeAlbum;
import com.busi.entity.HomeAlbumPic;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/***
 * 存储室相关接口
 * author：zhaojiajie
 * create time：2018-10-19 12:41:59
 */
public interface HomeAlbumApiController {

    /**
     * 新建相册
     *
     * @param homeAlbum
     * @return
     */
    @PostMapping("addAlbum")
    ReturnData addAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult);

    /**
     * 更新相册
     *
     * @param homeAlbum
     * @return
     */
    @PutMapping("updateAlbum")
    ReturnData updateAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult);

    /**
     * @Description: 删除相册
     * @return:
     */
    @DeleteMapping("delAlbum/{userId}/{id}")
    ReturnData delAlbum(@PathVariable long userId, @PathVariable long id);

    /***
     * 分页查询相册列表
     * @param userId  用户ID
     * @param roomType 房间类型 默认-1不限， 0花园,1客厅,2家店,3存储室-图片-童年,4存储室-图片-青年,5存储室-图片-中年,6存储室-图片-老年，7藏品室，8荣誉室
     * @param name  相册名
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findAlbumList/{userId}/{roomType}/{name}/{page}/{count}")
    ReturnData findAlbumList(@PathVariable long userId, @PathVariable int roomType, @PathVariable String name, @PathVariable int page, @PathVariable int count);

    /**
     * 更新相册密码
     *
     * @param homeAlbum
     * @return
     */
    @PutMapping("modifyAlbumPwd")
    ReturnData modifyAlbumPwd(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult);

    /**
     * 查询相册基本信息
     *
     * @param id
     * @return
     */
    @GetMapping("getAlbumInfo/{id}")
    ReturnData getAlbumInfo(@PathVariable long id);

    /**
     * 设置相册封面
     *
     * @param homeAlbum
     * @return
     */
    @PutMapping("updateAlbumCover")
    ReturnData updateAlbumCover(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult);

    /**
     * 清除相册密码(同上更新密码)
     *
     * @param id
     * @return
     */
//    @GetMapping("resetAlbumPwd/{id}")
//    ReturnData resetAlbumPwd(@PathVariable long id);

    /**
     * 验证相册密码
     *
     * @param id
     * @return
     */
    @GetMapping("ckAlbumPass/{id}/{password}")
    ReturnData ckAlbumPass(@PathVariable long id, @PathVariable String password);

    /**
     * 统计相册图片总数
     *
     * @return
     */
    @GetMapping("picNumber")
    ReturnData picNumber();

    /**
     * 新增图片
     *
     * @param homeAlbumPic
     * @return
     */
    @PostMapping("uploadPic")
    ReturnData uploadPic(@Valid @RequestBody HomeAlbumPic homeAlbumPic, BindingResult bindingResult);

    /**
     * @Description: 删除相册图片
     * @return:
     */
    @DeleteMapping("delAlbumPic/{userId}/{albumId}/{ids}")
    ReturnData delAlbumPic(@PathVariable long userId, @PathVariable long albumId, @PathVariable String ids);

    /**
     * @Description: 删除图片
     * @return:
     */
    @DeleteMapping("delPic/{userId}/{ids}")
    ReturnData delPic(@PathVariable long userId, @PathVariable String ids);

    /**
     * 更新图片信息
     *
     * @param homeAlbumPic
     * @return
     */
    @PutMapping("updatePic")
    ReturnData updatePic(@Valid @RequestBody HomeAlbumPic homeAlbumPic, BindingResult bindingResult);

    /***
     * 分页查询指定相册图片
     * @param userId  用户ID
     * @param albumId 相册ID
     * @param name  图片名
     * @param password  相册密码
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findAlbumPic/{userId}/{albumId}/{name}/{password}/{page}/{count}")
    ReturnData findAlbumPic(@PathVariable long userId, @PathVariable int albumId, @PathVariable String name, @PathVariable String password, @PathVariable int page, @PathVariable int count);


    /***
     * 分页查询图片
     * @param type  查询入口：0日期、全部图片界面  1搜索界面
     * @param userId  用户ID
     * @param date  指定日期  0表示查所有   格式：20201212
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @GetMapping("findPicList/{type}/{userId}/{date}/{page}/{count}")
    ReturnData findPicList(@PathVariable int type, @PathVariable long userId, @PathVariable int date, @PathVariable int page, @PathVariable int count);

    /***
     * 查询上传图片日期
     * @param albumId 相册ID
     * @param startTime   选择日期
     * @return
     */
    @GetMapping("findPicDate/{albumId}/{findType}/{startTime}")
    ReturnData findPicDate(@PathVariable long albumId, @PathVariable int findType, @PathVariable int startTime);
}
