package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.HomeAlbumService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @program: ehome
 * @description: 存储室相关接口
 * @author: ZHaoJiaJie
 * @create: 2018-10-19 14:51
 */
@RestController
public class HomeAlbumController extends BaseController implements HomeAlbumApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    HomeAlbumService homeAlbumService;

    @Autowired
    UserMembershipUtils userMembershipUtils;

    /***
     * 新建相册
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData addAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(homeAlbum.getUserId());
        int memberShipStatus = 0;
        int numLimit = Constants.UPLOADALBUMCOUNT;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 200;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 500;
        }
        //验证相册个数
        int count_alb = homeAlbumService.findNum(homeAlbum.getUserId(), homeAlbum.getRoomType());
        if (count_alb >= numLimit) {
            return returnData(StatusCode.CODE_FOLDER_MAX.CODE_VALUE, "创建相册达到上限！", new JSONObject());
        }
        long purviewId = 0L;    //相册密码
        if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
            HomeAlbumPwd pwd = new HomeAlbumPwd();
            String status = CommonUtils.getRandom(6, 0);
            String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
            pwd.setPassword(code);
            pwd.setStatus(status);

            homeAlbumService.addPwd(pwd);
            purviewId = pwd.getId();
        }
//        if (homeAlbum.getRoomType() == 0) {
//            homeAlbum.setRoomType(9);//默认类型为其他
//        }
        homeAlbum.setAlbumState(0);//0正常
        homeAlbum.setPhotoSize(0); //相册图片数量
        homeAlbum.setCreateTime(new Date()); //相册创建时间
        homeAlbum.setAlbumPurview(purviewId);  //相册密码ID
        homeAlbumService.addAlbum(homeAlbum);

        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新相册
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData updateAlbum(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        HomeAlbum album = homeAlbumService.findById(homeAlbum.getId());
        if (album == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "数据错误，相册不存在", new JSONObject());
        }
        HomeAlbumPwd pwd = null;
        //更新前判断是否有密码
        if (album.getAlbumPurview() > 0) {
            boolean flag = false;
            //对密码进行操作
            if (!CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                //比对密码
                pwd = homeAlbumService.findByPwdId(album.getAlbumPurview());
                if (pwd != null) {
                    String status = pwd.getStatus();
                    if (CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32).equals(pwd.getPassword())) {
                        flag = true;
                    }
                }
            } else {
                flag = true;
            }
            if (flag) {
                //对密码进行操作
                if (!CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                    if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
                        //更新密码
                        String status = CommonUtils.getRandom(6, 0);
                        String codes = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                        pwd.setPassword(codes);
                        pwd.setStatus(status);

                        homeAlbumService.updatePwd(pwd);

                    } else {
                        //删除密码
                        homeAlbumService.delPwd(album.getAlbumPurview());
                        homeAlbum.setAlbumPurview(0L);
                    }
                }
                homeAlbumService.updateAlbum(homeAlbum);
            } else {
                return returnData(StatusCode.CODE_FOLDER_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        } else {
            long purviewId = 0L;    //相册密码
            if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
                pwd = new HomeAlbumPwd();
                String status = CommonUtils.getRandom(6, 0);
                String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                pwd.setPassword(code);
                pwd.setStatus(status);
                homeAlbumService.addPwd(pwd);
                purviewId = pwd.getId();
            }
            homeAlbum.setAlbumPurview(purviewId);  //相册密码ID
            homeAlbumService.updateAlbum(homeAlbum);
        }
        if (!CommonUtils.checkFull(homeAlbum.getDelUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeAlbum.getUserId(), homeAlbum.getDelUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除相册
     * @return:
     */
    @Override
    public ReturnData delAlbum(@PathVariable long userId, @PathVariable long id) {
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的图片信息", new JSONObject());
        }
        HomeAlbum album = homeAlbumService.findById(id);
        if (album == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "数据错误，相册不存在", new JSONObject());
        }
        //更新相册
        album.setAlbumState(1);//1删除
        homeAlbumService.delAlbum(album);
        //更新相册下面的图片
        List list = null;
        String ids = "";
        list = homeAlbumService.updateByAlbumId(album.getId());
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HomeAlbumPic albumPic = (HomeAlbumPic) list.get(i);
                if (albumPic != null) {
                    ids += albumPic.getId() + ",";
                }
            }
            if (!CommonUtils.checkFull(ids)) {
                homeAlbumService.deletePic(userId, id, ids.split(","));
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //验证是否房间锁
        if (CommonUtils.getMyId() != userId) {

        }
        //查询相册列表
        HomeAlbum album = null;
        List<Object> picSizeList = null;//相册内图片数量列表
        List<HomeAlbum> albumList = null;//相册列表
        PageBean<HomeAlbum> pageBean = null;
        pageBean = homeAlbumService.findPaging(userId, roomType, name, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        //遍历拿到相册ID的组合
        albumList = pageBean.getList();
        String albumIds = "";
        if (albumList != null && albumList.size() > 0) {
            for (int i = 0; i < albumList.size(); i++) {
                album = albumList.get(i);
                album.setPhotoSize(0);
                if (album != null) {
                    if (i == albumList.size() - 1) {
                        albumIds += album.getId();
                    } else {
                        albumIds += album.getId() + ",";
                    }
                }
            }
            //根据相册ID组合 查询对应相册内的图片数量
            long num = 0;
            String[] ids = albumIds.split(",");
            for (int i = 0; i < ids.length; i++) {
                num = homeAlbumService.findByIds2(Long.parseLong(ids[i]));
                for (int j = 0; j < albumList.size(); j++) {
                    album = albumList.get(j);
                    if (album.getId() == Long.parseLong(ids[i])) {
                        album.setPhotoSize(num);
                    }
                }

            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", albumList);
    }

    /**
     * 更新相册密码
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData modifyAlbumPwd(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        HomeAlbum album = homeAlbumService.findById(homeAlbum.getId());
        if (album == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "数据错误，相册不存在", new JSONObject());
        }
        HomeAlbumPwd pwd = null;
        //更新前判断是否有密码
        if (album.getAlbumPurview() > 0) {
            boolean flag = false;
            //对密码进行操作
            if (!CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                //比对密码
                pwd = homeAlbumService.findByPwdId(album.getAlbumPurview());
                if (pwd != null) {
                    String status = pwd.getStatus();
                    if (CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32).equals(pwd.getPassword())) {
                        flag = true;
                    }
                }
            } else {
                flag = true;
            }
            if (flag) {
                //对密码进行操作
                if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
                    //更新密码
                    String status = CommonUtils.getRandom(6, 0);
                    String codes = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                    pwd.setPassword(codes);
                    pwd.setStatus(status);

                    homeAlbumService.updatePwd(pwd);
                    homeAlbum.setAlbumPurview(pwd.getId());  //相册密码ID
                } else {
                    //删除密码
                    homeAlbumService.delPwd(album.getAlbumPurview());
                    homeAlbum.setAlbumPurview(0L);
                }
                homeAlbumService.updatePwdId(homeAlbum);
            } else {
                return returnData(StatusCode.CODE_FOLDER_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        } else {
            long purviewId = 0L;    //相册密码
            if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
                pwd = new HomeAlbumPwd();
                String status = CommonUtils.getRandom(6, 0);
                String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                pwd.setPassword(code);
                pwd.setStatus(status);
                homeAlbumService.addPwd(pwd);
                purviewId = pwd.getId();
            }
            homeAlbum.setAlbumPurview(purviewId);  //相册密码ID
            homeAlbumService.updatePwdId(homeAlbum);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 查询相册基本信息
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData getAlbumInfo(@PathVariable long id) {
        HomeAlbum album = homeAlbumService.findById(id);
        if (album == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
        }
        //验证是否房间锁
        if (CommonUtils.getMyId() != album.getUserId()) {

        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", album);
    }

    /**
     * 设置相册封面
     *
     * @param homeAlbum
     * @return
     */
    @Override
    public ReturnData updateAlbumCover(@Valid @RequestBody HomeAlbum homeAlbum, BindingResult bindingResult) {
        homeAlbumService.updateAlbumCover(homeAlbum);
        if (!CommonUtils.checkFull(homeAlbum.getDelUrls())) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(homeAlbum.getUserId(), homeAlbum.getDelUrls());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 验证相册密码
     *
     * @param id
     * @return
     */
    @Override
    public ReturnData ckAlbumPass(@PathVariable long id, @PathVariable String password) {
        HomeAlbum alb = homeAlbumService.findById(id);
        if (alb == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
        }
        //删除前判断是否有密码
        boolean flag = false;
        HomeAlbumPwd pwd = null;
        if (alb.getAlbumPurview() > 0) {
            //比对密码
            pwd = homeAlbumService.findByPwdId(alb.getAlbumPurview());
            if (pwd != null) {
                String status = pwd.getStatus();
                if (CommonUtils.strToMD5(password + status, 32).equals(pwd.getPassword())) {
                    flag = true;
                }
            }
        } else {
            flag = true;
        }
        if (!flag) {
            return returnData(StatusCode.CODE_FOLDER_PASSWORD_ERROR.CODE_VALUE, "密码验证错误!", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * 统计相册图片总数
     *
     * @return
     */
    @Override
    public ReturnData picNumber() {
        List list = null;
        int picCount = 0;//图片
        int collectionCount = 0; //藏品
        int honorCount = 0; //荣誉
        int audioCount = 0; //音频
        int videoCount = 0; //视频
        list = homeAlbumService.findPicNumber(CommonUtils.getMyId());//图片
        if (list != null && list.size() > 0) {
            HomeAlbumPic hAlbumPic = null;
            for (int i = 0; i < list.size(); i++) {
                hAlbumPic = (HomeAlbumPic) list.get(i);
                if (hAlbumPic != null) {
                    if (hAlbumPic.getRoomType() <= 6) {//图片
                        picCount++;
                    } else if (hAlbumPic.getRoomType() > 6 && hAlbumPic.getRoomType() < 8) {//藏品
                        collectionCount++;
                    } else {//荣誉
                        honorCount++;
                    }
                }
            }
        }
        Map<String, Object> map = new HashMap<>();
        map.put("picCount", picCount);
        map.put("collectionCount", collectionCount);
        map.put("honorCount", honorCount);
        map.put("audioCount", audioCount);
        map.put("videoCount", videoCount);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }

    /***
     * 新增图片
     * @param homeAlbumPic
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData uploadPic(@Valid @RequestBody HomeAlbumPic homeAlbumPic, BindingResult bindingResult) {
        if (CommonUtils.checkFull(homeAlbumPic.getImgUrl())) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        //获取会员等级 根据用户会员等级 获取最大次数 后续添加
        UserMembership memberMap = userMembershipUtils.getUserMemberInfo(homeAlbumPic.getUserId());
        int memberShipStatus = 0;
        int numLimit = Constants.UPLOADIMGCOUNT;
        if (memberMap != null) {
            memberShipStatus = memberMap.getMemberShipStatus();
        }
        if (memberShipStatus == 1) {//普通会员
            numLimit = 1000;
        } else if (memberShipStatus > 1) {//高级以上
            numLimit = 2000;
        }
        int num = homeAlbumService.countPic(homeAlbumPic.getUserId());
        if (num >= numLimit) {
            return returnData(StatusCode.CODE_FILE_MAX.CODE_VALUE, "图片上传达到上限", new JSONObject());
        }
        //判断是否需要设为封面
        String homePicArray[] = homeAlbumPic.getImgUrl().split(",");
        if (homeAlbumPic.getAlbumId() > 0) {//是否是相册上传
            HomeAlbum alb = homeAlbumService.findById(homeAlbumPic.getAlbumId());
            if (alb == null) {
                return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
            }
            long phoneSize = alb.getPhotoSize();
            if (phoneSize <= 0 && CommonUtils.checkFull(alb.getImgCover())) {
                alb.setImgCover(homePicArray[0]);
                homeAlbumService.updateAlbumCover(alb);
            }
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        int time = Integer.valueOf(format.format(new Date()));//当前系统时间(Date转int)
        for (int i = 0; i < homePicArray.length; i++) {
            homeAlbumPic.setImgUrl(homePicArray[i]);
            homeAlbumPic.setTime(new Date());
            homeAlbumPic.setNewTime(time);
            homeAlbumService.uploadPic(homeAlbumPic);
        }
        //新增存储室图片记录
        //查询当前时间有无图片上传记录
        HomeAlbumPicWhole whole = homeAlbumService.findWhole(homeAlbumPic.getUserId(), time);
        if (whole == null) {
            whole = new HomeAlbumPicWhole();
            whole.setNum(homePicArray.length);
            whole.setTime(time);
            whole.setAlbumId(homeAlbumPic.getAlbumId());
            whole.setUserId(homeAlbumPic.getUserId());
            homeAlbumService.add(whole);
        } else {
            whole.setNum(whole.getNum() + homePicArray.length);
            homeAlbumService.update(whole);
        }
        //新增任务
        mqUtils.sendTaskMQ(CommonUtils.getMyId(), 0, 6);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 删除图片
     * @return:
     */
    @Override
    public ReturnData delAlbumPic(@PathVariable long userId, @PathVariable long albumId, @PathVariable String ids) {
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的图片信息", new JSONObject());
        }
        HomeAlbum alb = homeAlbumService.findById(albumId);
        if (alb == null) {
            return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
        }
        //返回删除条数
        int qlen = homeAlbumService.deletePic(userId, albumId, ids.split(","));
        if (qlen > 0) {
            long newphoneSize = alb.getPhotoSize();
            newphoneSize -= qlen;    //更新本相册图片张数
            if (newphoneSize < 0) {
                //避免特殊情况
                newphoneSize = 0;
            }
            alb.setPhotoSize(newphoneSize);
            homeAlbumService.updateAlbumNum(alb);
        }
        if (!CommonUtils.checkFull(ids)) {
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(userId, ids);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 删除图片（新）
     * @param userId
     * @param ids    格式：20201212#1,2,3;//日期#图片ID，ID，ID；
     * @return
     */
    @Override
    public ReturnData delPic(@PathVariable long userId, @PathVariable String ids) {
        //验证修改人权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + userId + "]的图片信息", new JSONObject());
        }
        if (CommonUtils.checkFull(ids)) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        String idss = "";
        String[] strings = ids.split(";");
        for (int i = 0; i < strings.length; i++) {
            String[] num = strings[i].split("#");//获取图片ID
            idss += num[1] + ",";//图片ID组合
        }
        if (idss.split(",").length > 100) {//暂定单次删除上限为100张
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "超出上限，最多删除100张", new JSONObject());
        }
        //删除图片
        if (!CommonUtils.checkFull(idss)) {
            homeAlbumService.delPic(userId, idss.split(","));
            //调用MQ同步 图片到图片删除记录表
            mqUtils.sendDeleteImageMQ(userId, idss);
        }
        for (int i = 0; i < strings.length; i++) {//图片ID组合  格式：20201212#1,2,3;//日期#图片ID，ID，ID；
            String[] num = strings[i].split("#");
            //查询当前时间有无图片上传记录
            HomeAlbumPicWhole whole = homeAlbumService.findWhole(userId, Integer.parseInt(num[0]));
            if (whole == null) {
                continue;
            }
            //更新图片上传数量
            int num2 = num[1].split(",").length;
            homeAlbumService.upPicNum(whole.getNum() - num2, whole.getId());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 更新图片信息
     * @param homeAlbumPic
     * @return
     */
    @Override
    public ReturnData updatePic(@Valid @RequestBody HomeAlbumPic homeAlbumPic, BindingResult bindingResult) {
        //验证修改人权限
        if (CommonUtils.getMyId() != homeAlbumPic.getUserId()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限修改用户[" + homeAlbumPic.getUserId() + "]的图片信息", new JSONObject());
        }
        if (homeAlbumPic.getAlbumId() > 0) {
            HomeAlbum alb = homeAlbumService.findById(homeAlbumPic.getAlbumId());
            if (alb == null) {
                return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
            }
        }
        HomeAlbumPic albumPic = homeAlbumService.findAlbumInfo(homeAlbumPic.getId());
        if (albumPic == null) {
            return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE, "图片不存在", new JSONObject());
        }
        homeAlbumService.updatePic(homeAlbumPic);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
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
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<HomeAlbumPic> pageBean = null;
        if (CommonUtils.checkFull(name)) {
            //验证是否房间锁
            if (CommonUtils.getMyId() != userId) {

            }
            HomeAlbum alb = homeAlbumService.findById(albumId);
            if (alb == null) {
                return returnData(StatusCode.CODE_FOLDER_NOT_FOUND.CODE_VALUE, "相册不存在", new JSONObject());
            }
            //判断是否有密码
            boolean flag = false;
            HomeAlbumPwd pwd = null;
            if (alb.getAlbumPurview() > 0 && userId != CommonUtils.getMyId()) {
                //比对密码
                pwd = homeAlbumService.findByPwdId(alb.getAlbumPurview());
                if (pwd != null) {
                    String status = pwd.getStatus();
                    if (CommonUtils.strToMD5(password + status, 32).equals(pwd.getPassword())) {
                        flag = true;
                    }
                }
            } else {
                flag = true;
            }
            if (!flag) {
                return returnData(StatusCode.CODE_FOLDER_PASSWORD_ERROR.CODE_VALUE, "密码验证错误!", new JSONObject());
            }
        }
        pageBean = homeAlbumService.findAlbumPic(userId, albumId, name, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 分页查询图片
     * @param albumId 相册ID  0时查全部图片  >0时查指定相册的
     * @param type  查询入口：0日期、全部图片界面  1搜索界面
     * @param userId  用户ID
     * @param date  指定日期  0表示查所有   格式：20201212
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findPicList(@PathVariable long albumId, @PathVariable int type, @PathVariable long userId, @PathVariable int date, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        //开始查询
        PageBean<HomeAlbumPic> pageBean = null;
//        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//        int time = Integer.valueOf(format.format(new Date()));//当前系统时间(Date转int)
        pageBean = homeAlbumService.findPicList(albumId, userId, date, type, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询上传图片日期
     * @param albumId 相册ID  0时查全部图片  >0时查指定相册的
     * @param findType   查询带标记的日期  findType=0时 格式：201802  findType=1时 格式：2018
     * @param startTime   选择日期
     * @return
     */
    @Override
    public ReturnData findPicDate(@PathVariable long albumId, @PathVariable int findType, @PathVariable int startTime) {
        String time = "";
        int endTime = 0;
        List list = null;
        if (findType == 0) {//查询带标记的日期  findType=0时 格式：201802  findType=1时 格式：2018
            endTime = ((startTime + 1) * 100);
            startTime = startTime * 100;
        } else {
            endTime = ((startTime + 1) * 10000);
            startTime = startTime * 10000;
        }
        list = homeAlbumService.findPicDate(CommonUtils.getMyId(), startTime, endTime, albumId);
        if (list.size() > 0 && list != null) {
            int len = list.size();
            HomeAlbumPicWhole notepad = null;
            for (int i = 0; i < len; i++) {
                notepad = (HomeAlbumPicWhole) list.get(i);
                time += notepad.getTime() + (i == len - 1 ? "" : ",");
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("time", time);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
