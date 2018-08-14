package com.busi.aop;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 全局日志管理AOP
 * author：SunTianJie
 * create time：2018/3/29 13:22
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {
//	private Logger logger = LoggerFactory.getLogger(getClass());
	@Pointcut("execution(public * com.busi.controller..*.*(..))")
	public void webLog() {
	}

    /***
     * 拦截请求
     * @param joinPoint 接入点
     * @throws Throwable 异常
     */
	@Before("webLog()")
	public void doBefore(JoinPoint joinPoint) throws Throwable {
		// 接收到请求，记录请求内容
		ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		if(attributes==null){
			return;
		}
		HttpServletRequest request = attributes.getRequest();
		// 记录下请求内容
		log.info("####################--服务端接收到接口请求--####################");
		log.info("请求 URL  : " + request.getRequestURL().toString());
        log.info("CLASS方法 : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
		log.info("请求类型  : " + request.getMethod());
		log.info("客户端IP  : " + request.getRemoteAddr());
		Enumeration<String> headers = request.getHeaderNames();
		String headerParam = "";
		String user_agent ="";
		while (headers.hasMoreElements()) {
			String key = (String) headers.nextElement();
			String value = request.getHeader(key);
			if("user-agent".equalsIgnoreCase(key)){
				user_agent =value;
			}
			headerParam += key+"="+value+",";
//			logger.info("name:{},value:{}", key, value);
		}
		if(headerParam!=null&&headerParam.length()>0){
			headerParam = headerParam.substring(0,headerParam.length()-1);
		}
		log.info("客户端设备: " + user_agent);
		//放到头信息 方便后续使用
		log.info("请求header: " + headerParam);
		if("POST".equals(request.getMethod())||"PUT".equals(request.getMethod())){
			String bodys = "";
			Object[] bodyArray = joinPoint.getArgs();
			if(bodyArray!=null&&bodyArray.length>0){
				Object jsonObj = JSON.toJSON(bodyArray[0]);
				bodys = jsonObj.toString();
			}
//			for(int i=0;i<bodyArray.length;i++){
//				if(bodyArray[i]!=null){
//
//					Object jsonObj = JSON.toJSON(bodyArray[i]);
//					bodys += jsonObj.toString() + " ";
//				}
//			}
			log.info("请求body  : " + bodys);
		}

		//下面为 普通http请求参数解析 如 http://fdafdsf?id=1&name=2 不适合rsetfull风格
//		Enumeration<String> enu = request.getParameterNames();
//		String param = "";
//		while (enu.hasMoreElements()) {
//			String key = (String) enu.nextElement();
//			String value = request.getParameter(key);
//
//			param += key+"="+value+",";
////			logger.info("name:{},value:{}", key, value);
//		}
//		if(param!=null&&param.length()>0){
//			param = param.substring(0,param.length()-1);
//		}
//		log.info("请求参数 : "+param);

	}

    /***
     * 处理响应信息
     * @param ret 响应结果
     * @throws Throwable 异常
     */
	@AfterReturning(returning = "ret", pointcut = "webLog()")
	public void doAfterReturning(Object ret) throws Throwable {
		// 处理完请求，返回内容
		ObjectMapper objectMapper = new ObjectMapper();
		String resJson = objectMapper.writeValueAsString(ret);
        log.info("接口响应  : " + resJson);
        log.info("####################--服务端处理接口请求成功--####################");
	}

}