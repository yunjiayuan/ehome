package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.entity.UserHeadNotes;
import com.busi.service.UserHeadNotesService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;
import java.util.Random;

/**
 * 用户头像相册（主界面各房间封面）相关接口
 * author：SunTianJie
 * create time：2018/7/20 16:59
 */
@RestController
public class UserHeadNotesController extends BaseController implements UserHeadNotesApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    UserHeadNotesService userHeadNotesService;

    /***
     * 查询用户头像相册
     * @param userId 被查询者的用户ID
     * @return
     */
    @Override
    public ReturnData findUserHeadNote(@PathVariable long userId) {
        //验证参数
        if(userId<=0){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"userId参数有误",new JSONObject());
        }
        //查询缓存中是否存在
        Map<String,Object> userHeadNoteMap = redisUtils.hmget(Constants.REDIS_KEY_USER_HEADNOTES+userId );
        UserHeadNotes userHeadNotes = null;
        if(userHeadNoteMap==null||userHeadNoteMap.size()<=0){
            //缓存中没有 查询数据库
            userHeadNotes = userHeadNotesService.findUserHeadNotesById(userId);
            if(userHeadNotes==null){//数据库中不存在 就新增默认图片
                userHeadNotes = new UserHeadNotes();
                userHeadNotes.setUserId(userId);
                int number = new Random().nextInt(3) + 1;
                int hy = new Random().nextInt(11) + 1;
                if(hy>=10){
                    userHeadNotes.setGardenCover("/image/roomCover/pub_hy_image_0"+hy+".jpg");
                }else{
                    userHeadNotes.setGardenCover("/image/roomCover/pub_hy_image_00"+hy+".jpg");
                }
                userHeadNotes.setLivingRoomCover("/image/roomCover/pub_kt_image_00"+number+".jpg");
                userHeadNotes.setHomeStoreCover("/image/roomCover/pub_jd_image_00"+number+".jpg");
                userHeadNotes.setStorageRoomCover("/image/roomCover/pub_ccs_image_00"+number+".jpg");
                userHeadNoteMap = CommonUtils.objectToMap(userHeadNotes);
                if(userId==CommonUtils.getMyId()){
                    userHeadNotesService.add(userHeadNotes);
                    //将用户头像相册信息存入缓存中
                    redisUtils.hmset(Constants.REDIS_KEY_USER_HEADNOTES+userId,userHeadNoteMap,Constants.USER_TIME_OUT);
                }
            }else{
                userHeadNoteMap = CommonUtils.objectToMap(userHeadNotes);
                //将用户头像相册信息存入缓存中
                redisUtils.hmset(Constants.REDIS_KEY_USER_HEADNOTES+userId,userHeadNoteMap,Constants.USER_TIME_OUT);
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",userHeadNoteMap);
    }

    /***
     * 更换封面接口
     * @param userHeadNotes
     * @return
     */
    @Override
    public ReturnData updateUserHeadNoteCover(@Valid @RequestBody UserHeadNotes userHeadNotes, BindingResult bindingResult) {
        //验证修改人权限
        if(CommonUtils.getMyId()!=userHeadNotes.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userHeadNotes.getUserId()+"]的房间封面",new JSONObject());
        }
        //开始修改
        userHeadNotesService.updateUserHeadNoteCover(userHeadNotes);
        //将缓存中数据 清除
        redisUtils.expire(Constants.REDIS_KEY_USER_HEADNOTES+userHeadNotes.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 更换欢迎视频接口
     * @param userHeadNotes
     * @return
     */
    @Override
    public ReturnData updateWelcomeVideo(@Valid @RequestBody UserHeadNotes userHeadNotes, BindingResult bindingResult) {
        //验证参数
        if(CommonUtils.checkFull(userHeadNotes.getWelcomeVideoCoverPath())||CommonUtils.checkFull(userHeadNotes.getWelcomeVideoPath())){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，欢迎视频和封面不能为空",new JSONObject());
        }
        //验证修改人权限
        if(CommonUtils.getMyId()!=userHeadNotes.getUserId()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userHeadNotes.getUserId()+"]的欢迎视频",new JSONObject());
        }
        //开始修改
        userHeadNotesService.updateWelcomeVideo(userHeadNotes);
        //将缓存中数据 清除
        redisUtils.expire(Constants.REDIS_KEY_USER_HEADNOTES+userHeadNotes.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
    /***
     * 删除欢迎视频接口
     * @param userId
     * @return
     */
    @Override
    public ReturnData delWelcomeVideo(@Valid @PathVariable long userId) {
        //验证修改人权限
        if(CommonUtils.getMyId()!=userId){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误，当前用户["+CommonUtils.getMyId()+"]无权限修改用户["+userId+"]的欢迎视频",new JSONObject());
        }
        UserHeadNotes userHeadNotes = new UserHeadNotes();
        userHeadNotes.setUserId(userId);
        userHeadNotes.setWelcomeVideoCoverPath("");
        userHeadNotes.setWelcomeVideoPath("");
        //开始修改
        userHeadNotesService.updateWelcomeVideo(userHeadNotes);
        //将缓存中数据 清除
        redisUtils.expire(Constants.REDIS_KEY_USER_HEADNOTES+userHeadNotes.getUserId(),0);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
