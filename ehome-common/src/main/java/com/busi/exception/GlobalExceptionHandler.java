package com.busi.exception;

import com.busi.entity.ReturnData;
import com.busi.utils.StatusCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常捕获处理工具类
 * author：SunTianJie
 * create time：2018/3/30 14:44
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /***
     * 全局异常捕获 解决报错无法返回客户端状态码问题
     * @param request 请求
     * @param exception 截获的异常对象
     * @return 错误json数据
     * @throws Exception 异常
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ReturnData exceptionHandler(HttpServletRequest request, Exception exception) throws Exception{
        exception.printStackTrace();
        ReturnData rData = new ReturnData();
        rData.setStatusCode(StatusCode.CODE_SERVER_ERROR.CODE_VALUE);
        rData.setStatusMsg(StatusCode.CODE_SERVER_ERROR.CODE_DESC);
        rData.setData("{}");
        return rData;
    }
}
