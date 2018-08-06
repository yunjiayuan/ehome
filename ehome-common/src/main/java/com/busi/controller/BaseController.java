package com.busi.controller;

import com.busi.entity.ReturnData;
import com.busi.utils.StatusCode;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

/**
 * 所有service的父类  用于接口数据类型封装
 * author：SunTianJie
 * create time：2018/5/29 15:11
 */
public class BaseController {

    /***
     * * 封装返回数据 有参
     * @param statusCode 状态码
     * @param statusMsg 状态信息
     * @param data 返回数据对象
     */
    public ReturnData returnData(int statusCode,String statusMsg,Object data){
        ReturnData rData = new ReturnData();
        rData.setStatusCode(statusCode);
        rData.setStatusMsg(statusMsg);
        rData.setData(data);
        return rData;
    }
    /***
     * * 封装返回数据 无参 仅返回成功状态码时可用
     */
    public ReturnData returnData(){
        ReturnData rData = new ReturnData();
        rData.setStatusCode(StatusCode.CODE_SUCCESS.CODE_VALUE);
        rData.setStatusMsg(StatusCode.CODE_SUCCESS.CODE_DESC);
        rData.setData("{}");
        return rData;
    }

    /***
     * 统一验证请求参数格式
     * @param bindingResult
     * @return
     */
    public String checkParams(BindingResult bindingResult){
        String errorInfo = "";
        for (ObjectError error : bindingResult.getAllErrors()) {
            errorInfo = error.getDefaultMessage()+",";
        }
        return errorInfo;
    }

}
