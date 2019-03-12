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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        //验证权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userId + "]的系统奖励信息", new JSONObject());
        }
        PageBean<RewardLog> pageBean;
        pageBean = rewardLogService.findList(userId, rewardType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, StatusCode.CODE_SUCCESS.CODE_DESC, new JSONArray());
        }
        //已读状态
        String ids = "";
        for (int i = 0; i < pageBean.getList().size(); i++) {
            RewardLog rewardLog = pageBean.getList().get(i);
            if(rewardLog!=null&&rewardLog.getIsNew()==0){
                if(i==pageBean.getList().size()-1){
                    ids += rewardLog.getId();
                }else{
                    ids += rewardLog.getId()+",";
                }
            }
        }
        if(!CommonUtils.checkFull(ids)){
            rewardLogService.updateIsNew(userId,ids);
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 查询是否有新奖励接口
     * @param userId  用户ID
     * @return
     */
    @Override
    public ReturnData findNewRewardLog(@PathVariable long userId) {
        //验证权限
        if (CommonUtils.getMyId() != userId) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数有误，当前用户[" + CommonUtils.getMyId() + "]无权限操作用户[" + userId + "]的奖励总金额信息", new JSONObject());
        }
        int isNewRewardLog = 0;//已读
        List<RewardLog> list =  rewardLogService.findRewardLogNewList(userId);
        if(list!=null&&list.size()>0){
            isNewRewardLog = 1;//未读 新的
        }
        Map<String,Object> map = new HashMap<>();
        map.put("isNewRewardLog",isNewRewardLog);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", map);
    }
}
