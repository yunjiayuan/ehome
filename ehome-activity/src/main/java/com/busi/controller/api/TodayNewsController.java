package com.busi.controller.api;

import com.alibaba.fastjson.JSONObject;
import com.busi.controller.BaseController;
import com.busi.entity.PageBean;
import com.busi.entity.ReturnData;
import com.busi.entity.TodayNews;
import com.busi.service.TodayNewsService;
import com.busi.utils.CommonUtils;
import com.busi.utils.RedisUtils;
import com.busi.utils.StatusCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Date;

/**
 * @program: ehome
 * @description: 资讯
 * @author: ZHaoJiaJie
 * @create: 2018-09-27 11:50
 */
@RestController
public class TodayNewsController extends BaseController implements TodayNewsApiController {

    @Autowired
    RedisUtils redisUtils;

    @Autowired
    TodayNewsService todayNewsService;

    /***
     * 新增
     * @param todayNews
     * @param bindingResult
     * @return
     */
    @Override
    public ReturnData addNews(@Valid @RequestBody TodayNews todayNews, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        int newsFormat = todayNews.getNewsFormat();
        todayNews.setNewsState(0);
        todayNews.setAddTime(new Date());
        todayNews.setRefreshTime(new Date());
        if (newsFormat == 1 || newsFormat == 2) {//发布类型  0纯文  1一图  2多图  3视频
            if (!CommonUtils.checkFull(todayNews.getImgUrls())) {
                String[] arrey = todayNews.getImgUrls().split(",");
                if (arrey.length > 2) {
                    todayNews.setNewsFormat(2);
                } else {
                    todayNews.setNewsFormat(1);
                }
            }
        } else if (newsFormat == 3) {
            if (!CommonUtils.checkFull(todayNews.getVideoUrl())) {
                todayNews.setNewsFormat(3);
            }
        } else {
            todayNews.setNewsFormat(0);
        }
        todayNewsService.add(todayNews);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /**
     * @Description: 更新
     * @Param: todayNews
     * @return:
     */
    @Override
    public ReturnData editNews(@Valid @RequestBody TodayNews todayNews, BindingResult bindingResult) {
        //验证参数格式是否正确
        if (bindingResult.hasErrors()) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, checkParams(bindingResult), new JSONObject());
        }
        todayNews.setRefreshTime(new Date());
        todayNewsService.editNews(todayNews);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
    }

    /***
     * 查询新闻列表
     * @param newsType 发布新闻类型0今日人物  1今日企业  2今日新闻
     * @param page  页码 第几页 起始值1
     * @param count 每页条数
     * @return
     */
    @Override
    public ReturnData findNewsList(@PathVariable int newsType, @PathVariable int page, @PathVariable int count) {
        //验证参数
        if (newsType < 0 || newsType > 2) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "参数taskType有误", new JSONObject());
        }
        if (page < 0 || count <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "分页参数有误", new JSONObject());
        }
        PageBean<TodayNews> pageBean;
        pageBean = todayNewsService.findList(newsType, page, count);
        if (pageBean == null) {
            return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", new JSONObject());
        }
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", pageBean);
    }

    /***
     * 根据ID查询
     * @param infoId 资讯ID
     * @return
     */
    @Override
    public ReturnData findPress(@PathVariable long infoId) {
        if (infoId <= 0) {
            return returnData(StatusCode.CODE_PARAMETER_ERROR.CODE_VALUE, "infoId参数有误", new JSONObject());
        }
        TodayNews tn = todayNewsService.findInfo(infoId);
        return returnData(StatusCode.CODE_SUCCESS.CODE_VALUE, "success", tn);
    }
}
