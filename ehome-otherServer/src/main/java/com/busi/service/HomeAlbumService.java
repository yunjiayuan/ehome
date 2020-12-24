package com.busi.service;

import com.busi.dao.HomeAlbumDao;
import com.busi.entity.*;
import com.busi.utils.CommonUtils;
import com.busi.utils.PageUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: ehome
 * @description: 存储室
 * @author: ZHaoJiaJie
 * @create: 2018-10-22 17:51
 */
@Service
public class HomeAlbumService {


    @Autowired
    private HomeAlbumDao homeAlbumDao;

    /***
     * 新增相册
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addAlbum(HomeAlbum homeAlbum) {
        return homeAlbumDao.addAlbum(homeAlbum);
    }

    /***
     * 更新相册删除状态
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delAlbum(HomeAlbum homeAlbum) {
        return homeAlbumDao.delAlbum(homeAlbum);
    }

    /***
     * 更新相册
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateAlbum(HomeAlbum homeAlbum) {
        return homeAlbumDao.updateAlbum(homeAlbum);
    }

    /***
     * 更新相册封面
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateAlbumCover(HomeAlbum homeAlbum) {
        return homeAlbumDao.updateAlbumCover(homeAlbum);
    }

    /***
     * 更新相册图片总数
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updateAlbumNum(HomeAlbum homeAlbum) {
        return homeAlbumDao.updateAlbumNum(homeAlbum);
    }

    /***
     * 查询指定相册下的图片
     * @return
     */
    public List<HomeAlbumPic> updateByAlbumId(long id) {
        List<HomeAlbumPic> list;
        list = homeAlbumDao.updateByAlbumId(id);
        return list;
    }

    /***
     * 新增图片
     * @param homeAlbumPic
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int uploadPic(HomeAlbumPic homeAlbumPic) {
        return homeAlbumDao.uploadPic(homeAlbumPic);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int add(HomeAlbumPicWhole homeAlbumPic) {
        return homeAlbumDao.add(homeAlbumPic);
    }

    /***
     * 编辑图片
     * @param homeAlbumPic
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePic(HomeAlbumPic homeAlbumPic) {
        return homeAlbumDao.updatePic(homeAlbumPic);
    }

    /***
     * 新增密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int addPwd(HomeAlbumPwd homeAlbumPwd) {
        return homeAlbumDao.addPwd(homeAlbumPwd);
    }

    /***
     * 更新密码
     * @param homeAlbumPwd
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePwd(HomeAlbumPwd homeAlbumPwd) {
        return homeAlbumDao.updatePwd(homeAlbumPwd);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int update(HomeAlbumPicWhole homeAlbumPwd) {
        return homeAlbumDao.update(homeAlbumPwd);
    }

    /***
     * 更新密码ID
     * @param homeAlbum
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int updatePwdId(HomeAlbum homeAlbum) {
        return homeAlbumDao.updatePwdId(homeAlbum);
    }

    /***
     * 统计该用户相册数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int findNum(long userId, int roomType) {
        return homeAlbumDao.findNum(userId, roomType);
    }

    /***
     * 统计该用户图片数量
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int countPic(long userId) {
        return homeAlbumDao.countPic(userId);
    }

    /***
     * 根据ID查询用户相册
     * @return
     */
    public HomeAlbum findById(long id) {
        return homeAlbumDao.findById(id);
    }

    public HomeAlbumPicWhole findWhole(long id, int time) {
        return homeAlbumDao.findWhole(id, time);
    }

    /***
     * 根据ID查询用户图片
     * @return
     */
    public HomeAlbumPic findAlbumInfo(long id) {
        return homeAlbumDao.findAlbumInfo(id);
    }

    /***
     * 根据ID查询用户相册密码
     * @return
     */
    public HomeAlbumPwd findByPwdId(long id) {
        return homeAlbumDao.findByPwdId(id);
    }

    /***
     * 删除密码
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delPwd(long id) {
        return homeAlbumDao.delPwd(id);
    }


    /***
     * 分页查询相册列表
     * @param userId 用户ID
     * @param roomType 房间类型 默认-1不限， 0花园,1客厅,2家店,3存储室-图片-童年,4存储室-图片-青年,5存储室-图片-中年,6存储室-图片-老年，7藏品室，8荣誉室
     * @param name 相册名
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeAlbum> findPaging(long userId, int roomType, String name, int page, int count) {

        List<HomeAlbum> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            if (roomType == 3 || roomType == 4 || roomType == 5 || roomType == 6 || roomType == 9) {
                list = homeAlbumDao.findPaging3(userId, roomType);
            } else {
                list = homeAlbumDao.findPaging2(userId, roomType);
            }
        } else {
            list = homeAlbumDao.findPaging(userId, roomType, name);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询指定相册图片
     * @param userId  用户ID
     * @param albumId 相册ID
     * @param name  图片名
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeAlbumPic> findAlbumPic(long userId, long albumId, String name, int page, int count) {

        List<HomeAlbumPic> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        if (CommonUtils.checkFull(name)) {
            list = homeAlbumDao.findAlbumPic2(userId, albumId);
        } else {
            list = homeAlbumDao.findAlbumPic(userId, albumId, name);
        }
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 分页查询图片
     * @param userId  用户ID
     * @param date  指定日期  0表示查所有   格式：20201212
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    public PageBean<HomeAlbumPic> findPicList(long userId, int date, int type, int page, int count) {
        List<HomeAlbumPic> list;
        Page p = PageHelper.startPage(page, count);//为此行代码下面的第一行sql查询结果进行分页
        list = homeAlbumDao.findPicList(userId, date, type);
        return PageUtils.getPageBean(p, list);
    }

    /***
     * 根据ID查询用户相册
     * @return
     */
    public List<Object> findByIds(String[] ids) {
        List<Object> list;
        list = homeAlbumDao.findByIds(ids);
        return list;
    }

    /***
     * 根据ID查询用户相册
     * @param id
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public long findByIds2(long id) {
        return homeAlbumDao.findByIds2(id);
    }

    /***
     * 统计用户各分类图片总数
     * @return
     */
    public List<HomeAlbumPic> findPicNumber(long userId) {
        List<HomeAlbumPic> list;
        list = homeAlbumDao.findPicNumber(userId);
        return list;
    }

    public List<HomeAlbumPicWhole> findPicDate(long userId, int startTime, int endTime, long albumId) {
        List<HomeAlbumPicWhole> list;
        list = homeAlbumDao.findPicDate(userId, startTime, endTime, albumId);
        return list;
    }

    /***
     * 删除图片
     * @param ids
     * @param userId
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int deletePic(long userId, long albumId, String[] ids) {
        return homeAlbumDao.deletePic(userId, albumId, ids);
    }

    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int delPic(long userId, String[] ids) {
        return homeAlbumDao.delPic(userId, ids);
    }

    /***
     * 更新图片记录
     * @return
     */
    @Transactional(rollbackFor = {RuntimeException.class, Exception.class})
    public int upPicNum(long num, long id) {
        return homeAlbumDao.upPicNum(num, id);
    }
}
