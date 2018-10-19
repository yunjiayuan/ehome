package com.busi.controller.api;

import com.busi.controller.BaseController;
import com.busi.entity.HomeAlbum;
import com.busi.entity.Homealbumpic;
import com.busi.entity.ReturnData;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 存储室相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-10-19 14:51
 */
@RestController
public class HomeAlbumController extends BaseController implements HomeAlbumApiController {

    /**
     * 新建相册
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData addAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        return null;
    }

    /**
     * 更新相册
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData updateAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        return null;
    }

    /**
     * @Description: 删除相册
     * @return:
     */
    @Override
    public ReturnData delAlbum(@PathVariable long userId, @PathVariable long id) {
        return null;
    }

    /***
     * 分页查询相册列表
     * @param userId  用户ID
     * @param roomType 房间类型 默认-1不限， 0花园,1客厅,2家店,3存储室-图片-童年,4存储室-图片-青年,5存储室-图片-中年,6存储室-图片-老年，7藏品室，8荣誉室
     * @param name  相册名
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findAlbumList(@PathVariable long userId, @PathVariable int roomType, @PathVariable String name, @PathVariable int page, @PathVariable int count) {
        return null;
    }

    /**
     * 更新相册密码
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData modifyAlbumPwd(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        return null;
    }

    /**
     * 查询相册基本信息
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getAlbumInfo(@PathVariable long id, @PathVariable int roomType) {
        return null;
    }

    /**
     * 设置相册封面
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData updateAlbumCover(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        return null;
    }

    /**
     * 验证相册密码
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData ckAlbumPass(@PathVariable long id, @PathVariable String password) {
        return null;
    }

    /**
     * 统计相册图片总数
     *
     * @return
     */
    @Override
    public ReturnData picNumber() {
        return null;
    }

    /**
     * @Description: 删除图片
     * @return:
     */
    @Override
    public ReturnData delAlbum(@PathVariable long userId, @PathVariable int albumId, @PathVariable String ids) {
        return null;
    }

    /**
     * 更新图片信息
     *
     * @param homealbumpic
     * @return
     */
    @Override
    public ReturnData updatePic(@Valid @RequestBody Homealbumpic homealbumpic, BindingResult bindingResult) {
        return null;
    }

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
    @Override
    public ReturnData findAlbumPic(@PathVariable long userId, @PathVariable int albumId, @PathVariable String name, @PathVariable String password, @PathVariable int page, @PathVariable int count) {
        return null;
    }
}
