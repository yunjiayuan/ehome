package com.busi.filter;

import com.alibaba.fastjson.JSONObject;
import com.busi.utils.CommonUtils;
import com.busi.utils.Constants;
import com.busi.utils.RedisUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * zuul拦截器 对用户token身份进行全局验证
 * author：SunTianJie
 * create time：2018/6/13 13:04
 */
@Component
@Slf4j
public class TokenFilter extends ZuulFilter  {

    @Autowired
    RedisUtils redisUtils;

    @Override
    public String filterType() {
//        pre：可以在请求被路由之前调用
//        route：在路由请求时候被调用
//        post：在route和error过滤器之后被调用
//        error：处理请求时发生错误时被调用
        return "pre";//前置过滤
    }

    @Override
    public int filterOrder() {
        //优先级，数字越大，优先级越低
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        //是否执行该过滤器，true代表需要过滤
        return true;
    }

    /***
     * 过滤器主要业务逻辑
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        //过滤不需要验证的请求地址
        String uri = request.getRequestURI();
        String[] whiteListArray = Constants.REQUEST_WHITE_LIST.split(",");
        if(whiteListArray!=null&&whiteListArray.length>0){
            for(int i=0;i<whiteListArray.length;i++){
                if(uri.indexOf(whiteListArray[i])!=-1){
                    return null;//在白名单中 则不进行后面的验证
                }
            }
        }
        //获取传来的参数Token 和 设备唯一标识 并进行身份验证
        String token = request.getHeader("token");
        String clientId = request.getHeader("clientId");
        String myId = request.getHeader("myId");
        //防暴力限制
        Object obj = redisUtils.hget("request_error_count",clientId);
        int errorCount = 0;

        if(obj!=null&&!CommonUtils.checkFull(obj.toString())){
            errorCount = Integer.parseInt(obj.toString());
        }
        if(errorCount>100){//大于100次 今天禁止访问
            String errorInfo="{\"statusCode\":120,\"statusMsg\":\"您的设备进行非法请求的次数过多，系统已自动禁止该设备使用一天，如有疑问请联系官方客服\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            log.info("当前设备进行非法请求的次数过多，系统已自动禁止该设备使用一天，myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        //验证参数和权限
        if(CommonUtils.checkFull(token)){
            String errorInfo="{\"statusCode\":101,\"statusMsg\":\"token不能为空\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("zuul检测到请求token为空！");
            return null;
        }
        if(CommonUtils.checkFull(clientId)){
            String errorInfo="{\"statusCode\":101,\"statusMsg\":\"clientId不能为空\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
//            if(CommonUtils.checkFull(errorCount)){//第一次错误
//                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
//            }else{
//                redisUtils.hashIncr("request_error_count",clientId,1);
//            }
            log.info("zuul检测到请求clientId为空！");
            return null;
        }
        if(CommonUtils.checkFull(myId)){
            String errorInfo="{\"statusCode\":101,\"statusMsg\":\"myId不能为空\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("zuul检测到请求myId为空！");
            return null;
        }
        //与redis缓存中的token数据进行比对
        Map<String,Object> userMap =  redisUtils.hmget("user_"+Long.parseLong(myId));
        if(userMap==null||userMap.size()<=0){
            String errorInfo="{\"statusCode\":110,\"statusMsg\":\"用户登录已过期，请重新登录\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("缓存中未找到当前用户ID对应的对象，建议重新登录 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        String serverToken = String.valueOf(userMap.get("token"));
        String serverClientId = String.valueOf(userMap.get("clientId"));
        Object object = userMap.get("accountStatus");
        int accountStatus = 0;
        if(object!=null&&!CommonUtils.checkFull(object.toString())){
            accountStatus = Integer.parseInt(object.toString());
        }
        if(CommonUtils.checkFull(serverToken)||CommonUtils.checkFull(serverClientId)){
            String errorInfo="{\"statusCode\":110,\"statusMsg\":\"用户登录已过期，请重新登录\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("服务端的token和clientId可能为空，建议重新登录 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        if(!clientId.equals(serverClientId)){
            String errorInfo="{\"statusCode\":118,\"statusMsg\":\"您的账号已在其他设备上登录，请您重新登录!\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("当前登录的用户clientId与服务端不符，可能是其他设备登录过该账号，建议重新登录 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        if(!token.equals(serverToken)){
            String errorInfo="{\"statusCode\":119,\"statusMsg\":\"您的token不正确，请重新登录!\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            if(errorCount==0){//第一次错误
                redisUtils.hset("request_error_count",clientId,1,24*60*60);//设置1天后失效
            }else{
                redisUtils.hashIncr("request_error_count",clientId,1);
            }
            log.info("当前登录的用户token与服务端不符，建议重新登录 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        //判断账号状态
        if(accountStatus==1){//未激活
            String errorInfo="{\"statusCode\":121,\"statusMsg\":\"该账号未激活，暂时不能访问其他数据接口\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            log.info("该账号未激活，暂时不能访问其他数据接口，自动跳转到登录界面 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }else if(accountStatus==2){//已停用
            String errorInfo="{\"statusCode\":122,\"statusMsg\":\"该账号已被停用，如有疑问请联系官方客服\",\"data\":"+new JSONObject()+"}";
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody(errorInfo);
            ctx.getResponse().setContentType("application/json;charset=UTF-8");
            log.info("该账号已被停用，暂时不能访问其他数据接口，自动跳转到登录界面 myId="+myId+",clientId="+clientId+",token="+token);
            return null;
        }
        //如果有token，则进行路由转发  这里return的值没有意义，zuul框架没有使用该返回值
        return null;
    }
}
