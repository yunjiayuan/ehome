package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.ImageDeleteLogControllerFegin;
import com.busi.Feign.VisitViewControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.controller.local.ImageDeleteLogLocalController;
import com.busi.entity.ImageDeleteLog;
import com.busi.entity.VisitView;
import com.busi.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 同步图片删除信息
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class ImageDeleteLogService implements MessageAdapter {

    @Autowired
    private ImageDeleteLogControllerFegin imageDeleteLogControllerFegin;

    /***
     * fegin 调用login服务中的 新增用户登录信息接口
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            String delImageUrls = body.getString("delImageUrls");
            if(CommonUtils.checkFull(delImageUrls)){
                log.info("消息服务平台处理用户添加删除文件记录，同步文件删除记录表操作失败，参数有误："+body.toJSONString());
                return;
            }
            //验证当前用户是否有权限删除该图片
            String newImages = "";
            String[] image = delImageUrls.split(",");
            boolean falg  = false;
            if(image!=null&&image.length>0){
                for(int i=0;i<image.length;i++){
                    if((userId+"").equals(image[i].split("_")[0])){
                        if(!falg){//第一次
                            newImages = image[i];
                            falg = true;
                        }else{
                            newImages += ","+image[i];
                        }
                    }
                }
            }
            if(!CommonUtils.checkFull(newImages)){
                //合法  可以删除  添加进删除吧
                ImageDeleteLog imageDeleteLog = new ImageDeleteLog();
                imageDeleteLog.setUserId(userId);
                imageDeleteLog.setImageUrl(newImages);
                imageDeleteLog.setTime(new Date());
                imageDeleteLogControllerFegin.addImageDeleteLog(imageDeleteLog);//fegin调用更新操作
                log.info("消息服务平台处理用户添加删除文件记录，同步文件删除记录表操作成功！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理用户添加删除文件记录，同步文件删除记录表操作失败，参数有误："+body.toJSONString());
        }
    }
}
