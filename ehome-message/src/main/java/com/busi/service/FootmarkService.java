package com.busi.service;

import com.alibaba.fastjson.JSONObject;
import com.busi.Feign.FootmarkLControllerFegin;
import com.busi.adapter.MessageAdapter;
import com.busi.entity.Footmark;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 足迹系统
 * author suntj
 * Create time 2018/6/3 17:48
 */
@Component
@Slf4j
public class FootmarkService implements MessageAdapter {

    @Autowired
    private FootmarkLControllerFegin footmarkLControllerFegin;

    /***
     * fegin 调用otherServer服务中的 新增任务操作
     * @param body
     */
    @Override
    public void sendMsg(JSONObject body) {
        try {
            long userId = Long.parseLong(body.getString("userId"));
            String title = body.getString("title");
            String imgUrl = body.getString("imgUrl");// 图片路径
            String videoUrl = body.getString("videoUrl");//视频路径     1个
            String audioUrl = body.getString("audioUrl");//音频路径     1个
            String infoId = body.getString("infoId");//信息id 公告ID和分类ID(用,分隔，格式：123,4):1婚恋交友,2二手手机,3寻人,4寻物,5失物招领,6其他（注：后续添加）
            int footmarkType = Integer.parseInt(body.getString("footmarkType"));//足迹类型 0.默认全部 1.发布公告 2.发布家博 3.图片上传 4.音频上传 5.视频上传  6记事
            Footmark footmark = new Footmark();
            footmark.setUserId(userId);
            footmark.setTitle(title);
            footmark.setImgUrl(imgUrl);
            footmark.setVideoUrl(videoUrl);
            footmark.setAudioUrl(audioUrl);
            footmark.setInfoId(infoId);
            footmark.setFootmarkType(footmarkType);
            footmarkLControllerFegin.addFootmark(footmark);
            log.info("消息服务平台处理新增足迹操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            log.info("消息服务平台处理新增足迹操作失败，参数有误："+body.toJSONString());
        }
    }
}
