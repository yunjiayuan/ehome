package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.OnlineMusic;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.service.OnlineMusicService;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * @program: ehome
 * @description: 在线音乐
 * @author: ZHaoJiaJie
 * @create: 2018-10-31 13:30
 */
@RestController
public class OnlineMusicController extends BaseController implements OnlineMusicApiController {

    @Autowired
    OnlineMusicService onlineMusicService;

    /***
     * 新增音乐
     * @param onlineMusic
     * @return
     */
    @Override
    public ReturnData addMusic(@Valid @RequestBody OnlineMusic onlineMusic, BindingResult bindingResult) {
        onlineMusicService.add(onlineMusic);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询歌曲列表
     * @param name  歌名或歌手
     * @param songType 歌曲类型：0.热歌榜 1.流行 2.纯音乐 3.摇滚 4.神曲 5.DJ 6.电音趴 7.说唱 8.国风 9.欧美（PS：仅在name为空时有效）
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findMusicList(@PathVariable int songType, @PathVariable String name, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<OnlineMusic> pageBean = null;
        pageBean = onlineMusicService.findPaging(songType, name, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
