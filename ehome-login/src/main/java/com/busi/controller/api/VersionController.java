package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.AdvertPic;
import com.busi.entity.ReturnData;
import com.busi.entity.Version;
import com.busi.service.VersionService;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Map;

/**
 * 版本号接口
 * author：SunTianJie
 * create time：2018/7/6 16:10
 */
@RestController
public class VersionController extends BaseController implements VersionApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    VersionService versionService;

    /***
     * 更新版本号
     * @param version
     * @return
     */
    @Override
    public ReturnData setVersion(@Valid @RequestBody Version version, BindingResult bindingResult) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String myId = request.getHeader("myId");//
        if(CommonUtils.checkFull(myId)||!"10076".equals(myId)){//只有固定用户才能更新
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"您无权限进行此操作！",new JSONObject());
        }
        versionService.update(version);
        //删除缓存信息
        redisUtils.expire(Constants.REDIS_KEY_VERSION+version.getClientType(),0);//设置过期0秒
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }

    /***
     * 查询最新版本号
     * @return
     */
    @Override
    public ReturnData findVersion(@PathVariable int type) {
        if(type<1||type>2){//参数有误
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"参数有误",new JSONObject());
        }
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_VERSION+type);
        if(map==null||map.size()<=0){
            Version version = versionService.findVersion(type);
            if(version==null){
                return returnData(StatusCode.CODE_SERVER_ERROR.CODE_VALUE,"版本号信息有误，请及时联系管理员",new JSONObject());
            }
            //放到缓存中
            redisUtils.hmset(Constants.REDIS_KEY_VERSION+type,CommonUtils.objectToMap(version));
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",version);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 查询过渡页
     * @param type 0表示苹果  1表示安卓
     * @return
     */
    @Override
    public ReturnData findAdvertPic(@PathVariable int type) {
        Map<String,Object> map = redisUtils.hmget(Constants.REDIS_KEY_ADVERTPICADDRESS);
        if(map==null||map.size()<=0){
            AdvertPic advertPic = new AdvertPic();
            advertPic.setAdvertPicAddress("image/advertPic/20200118/guoduye.png");
//            advertPic.setAdvertPicAddress("image/advertPic/20210208/ceshi.png");
            advertPic.setShowType(0);
            advertPic.setVersion(191);
            //默认图放到缓存中
            redisUtils.hmset(Constants.REDIS_KEY_ADVERTPICADDRESS,CommonUtils.objectToMap(advertPic));
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",advertPic);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",map);
    }

    /***
     * 更新过渡页
     * @param advertPic
     * @return
     */
    @Override
    public ReturnData setAdvertPic(@Valid @RequestBody AdvertPic advertPic, BindingResult bindingResult) {
        //将新过渡页放到缓存中
        redisUtils.hmset(Constants.REDIS_KEY_ADVERTPICADDRESS,CommonUtils.objectToMap(advertPic));
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE,"success",new JSONObject());
    }
}
