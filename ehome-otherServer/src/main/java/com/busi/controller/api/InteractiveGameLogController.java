package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.InteractiveGameLog;
import com.busi.entity.ReturnData;
import com.busi.service.InteractiveGameLogService;
import com.busi.utils.MqUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;
import java.util.Random;

/**
 * 互动游戏胜负记录接口
 * author：SunTianJie
 * create time：2018/8/22 10:04
 */
@RestController
public class InteractiveGameLogController extends BaseController implements InteractiveGameLogApiController {

    @Autowired
    private InteractiveGameLogService interactiveGameLogService;

    @Autowired
    MqUtils mqUtils;

    /***
     * 新增互动游戏胜负记录接口
     * @param interactiveGameLog
     * @param bindingResult
     * @return 返回胜负结果
     */
    @Override
    public ReturnData addInteractiveGameLog(@Valid @RequestBody InteractiveGameLog interactiveGameLog, BindingResult bindingResult) {
        //验证参数格式
        if(bindingResult.hasErrors()){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,checkParams(bindingResult),new JSONObject());
        }
        //单独验证赌注  目前赌注只支持20和40家点
        if(interactiveGameLog.getHomePoint()!=20&&interactiveGameLog.getHomePoint()!=40){
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE,"homePoint参数有误",new JSONObject());
        }
        int myPoint = 0;//邀请者点数
        int userPoint = 0;//参与者点数
        //判断游戏类型
        if(interactiveGameLog.getGameType()==0){//骰子
            //开始随机胜负结果
            Random r = new Random();
            myPoint = r.nextInt(6)+1;
            userPoint = r.nextInt(6)+1;
            if(myPoint>userPoint){
                interactiveGameLog.setGameResults(interactiveGameLog.getMyId());
            }else if(myPoint<userPoint){
                interactiveGameLog.setGameResults(interactiveGameLog.getUserId());
            }else{//平局
                interactiveGameLog.setGameResults(0);
            }
        }else{//猜拳
            //开始随机胜负结果
            Random r = new Random();
            myPoint = r.nextInt(2);//0 石头 1剪刀 2布
            userPoint = r.nextInt(2);//0 石头 1剪刀 2布
            if(myPoint==0){//石头
                if(userPoint==0){//石头
                    interactiveGameLog.setGameResults(0);//平局
                }else if(userPoint==1){//剪刀
                    interactiveGameLog.setGameResults(interactiveGameLog.getMyId());
                }else{//布
                    interactiveGameLog.setGameResults(interactiveGameLog.getUserId());
                }
            }else if(myPoint==1){//剪刀
                if(userPoint==0){//石头
                    interactiveGameLog.setGameResults(interactiveGameLog.getUserId());
                }else if(userPoint==1){//剪刀
                    interactiveGameLog.setGameResults(0);//平局
                }else{//布
                    interactiveGameLog.setGameResults(interactiveGameLog.getMyId());
                }
            }else{//布
                if(userPoint==0){//石头
                    interactiveGameLog.setGameResults(interactiveGameLog.getMyId());
                }else if(userPoint==1){//剪刀
                    interactiveGameLog.setGameResults(interactiveGameLog.getUserId());
                }else{//布
                    interactiveGameLog.setGameResults(0);//平局
                }
            }
        }
        interactiveGameLog.setMyPoint(myPoint);
        interactiveGameLog.setUserPoint(userPoint);
        interactiveGameLog.setTime(new Date());
        //更新双方钱包余额
        if(interactiveGameLog.getMyId()==interactiveGameLog.getGameResults()){
            mqUtils.sendPurseMQ(interactiveGameLog.getMyId(),19,2,interactiveGameLog.getHomePoint());//更新赢家
            mqUtils.sendPurseMQ(interactiveGameLog.getUserId(),18,2,interactiveGameLog.getHomePoint()*-1);//更新输家
        }
        if(interactiveGameLog.getUserId()==interactiveGameLog.getGameResults()){
            mqUtils.sendPurseMQ(interactiveGameLog.getUserId(),19,2,interactiveGameLog.getHomePoint());//更新赢家
            mqUtils.sendPurseMQ(interactiveGameLog.getMyId(),18,2,interactiveGameLog.getHomePoint()*-1);//更新输家
        }
        //更新双方游戏结果记录
        interactiveGameLogService.addInteractiveGameLog(interactiveGameLog);
        //更新任务系统
        mqUtils.sendTaskMQ(interactiveGameLog.getMyId(),1,8);
        mqUtils.sendTaskMQ(interactiveGameLog.getUserId(),1,8);
        //返回胜负接口
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", interactiveGameLog);
    }
}
