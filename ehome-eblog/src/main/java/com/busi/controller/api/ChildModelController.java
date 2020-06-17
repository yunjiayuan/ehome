package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.ReturnData;
import com.busi.service.ChildModelService;
import com.busi.utils.CommonUtils;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/***
 * 儿童/青少年模式相关接口
 * author：zhaojiajie
 * create time：22020-06-16 14:07:52
 */
@RestController
public class ChildModelController extends BaseController implements ChildModelApiController {

    @Autowired
    MqUtils mqUtils;

    @Autowired
    ChildModelService homeAlbumService;

    /***
     * 设置密码
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult) {
        if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
            String status = CommonUtils.getRandom(6, 0);
            String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
            homeAlbum.setPassword(code);
            homeAlbum.setStatus(status);
//            homeAlbum.setState(1);
            homeAlbumService.addPwd(homeAlbum);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 修改密码
     * @param homeAlbum
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData modifyChildPwd(@Valid @RequestBody ChildModelPwd homeAlbum, BindingResult bindingResult) {
        ChildModelPwd modelPwd = homeAlbumService.findById(CommonUtils.getMyId());
        if (!CommonUtils.checkFull(homeAlbum.getPassword())) {
            if (modelPwd == null) {
                String status = CommonUtils.getRandom(6, 0);
                String code = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                homeAlbum.setPassword(code);
                homeAlbum.setStatus(status);
//                homeAlbum.setState(1);
                homeAlbumService.addPwd(homeAlbum);
                return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
            }
            if (CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
            //对密码进行操作
            //比对密码
            String status = modelPwd.getStatus();
            if (modelPwd.getPassword().equals(CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32))) {
                //更新密码
                status = CommonUtils.getRandom(6, 0);
                String codes = CommonUtils.strToMD5(homeAlbum.getPassword() + status, 32);
                modelPwd.setPassword(codes);
                modelPwd.setStatus(status);
//                modelPwd.setState(1);
                homeAlbumService.updatePwd(modelPwd);
            } else {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        } else {//密码为空时验证通过则删除密码/关闭模式
            if (CommonUtils.checkFull(homeAlbum.getOldPassword())) {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
            //比对密码
            String status = modelPwd.getStatus();
            if (modelPwd.getPassword().equals(CommonUtils.strToMD5(homeAlbum.getOldPassword() + status, 32))) {
                //删除密码/关闭模式
                homeAlbumService.delPwd(CommonUtils.getMyId());
            } else {
                return returnData(StatusCode.CODE_PASSWORD_ERROR.CODE_VALUE, "密码输入错误！", new JSONObject());
            }
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 验证密码
     * @param password
     * @return
     */
    @Override
    public ReturnData ckChildPwd(@PathVariable String password) {
        //删除前判断是否有密码
        boolean flag = false;
        ChildModelPwd pwd = null;
        //比对密码
        pwd = homeAlbumService.findById(CommonUtils.getMyId());
        if (pwd != null) {
            String status = pwd.getStatus();
            if (CommonUtils.strToMD5(password + status, 32).equals(pwd.getPassword())) {
                flag = true;
            }
        }
        if (!flag) {
            return returnData(StatusCode.CODE_FOLDER_PASSWORD_ERROR.CODE_VALUE, "密码验证错误!", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询是否设置密码
     * @return
     */
    @Override
    public ReturnData getChildPwd() {
        Map<String, Object> map = new HashMap<>();
        ChildModelPwd album = homeAlbumService.findById(CommonUtils.getMyId());
        if (album == null) {
            map.put("state", 0);//未开启
        } else {
            map.put("state", 1);//已开启
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
