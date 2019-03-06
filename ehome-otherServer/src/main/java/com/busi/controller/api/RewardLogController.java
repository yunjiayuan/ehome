package com.busi.controller.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.*;
import com.busi.service.RewardLogService;
import com.busi.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/***
 * 用户奖励记录相关接口
 * author：stj
 * create time：2019-3-6 16:30:31
 */
@RestController
public class RewardLogController extends BaseController implements RewardLogApiController {

    @Autowired
    RewardLogService rewardLogService;

    /***
     * 查询指定用户的奖励列表
     * @param userId  用户ID
     * @param rewardType  奖励类型 -1所以 0红包雨奖励 1新人注册奖励 2分享码邀请别人注册奖励 3生活圈首次发布视频奖励 4生活圈10赞奖励 5生活圈100赞奖励 6生活圈10000赞奖励
     * @return
     */
    @Override
    public ReturnData findRewardLogList(@PathVariable long userId,@PathVariable int rewardType,@PathVariable int page,@PathVariable int count) {
        //验证参数
        if (rewardType < -1 || rewardType > 6) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数rewardType有误，数值超出指定范围", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<RewardLog> pageBean;
        pageBean = rewardLogService.findList(userId, rewardType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }
}
