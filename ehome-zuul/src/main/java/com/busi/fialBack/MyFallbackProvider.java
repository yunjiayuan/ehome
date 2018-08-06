package com.busi.fialBack;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.route.FallbackProvider;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * zuul的回退机制
 * //feign的Fallback是针对一个类，zuul的Fallback是针对一个微服务。
 * author：SunTianJie
 * create time：2018/6/19 14:24
 */
@Component
@Slf4j
public class MyFallbackProvider implements FallbackProvider {

    /**
     * 将要回退的微服务
     *  "*"或null 表示监控所有微服务 此处可以是单个微服务的serviceId
     * @return
     */
    @Override
    public String getRoute() {
        return "*";
    }

    /***
     * 响应客户端部分信息设置
     * @param route
     * @param cause
     * @return
     */
    @Override
    public ClientHttpResponse fallbackResponse(String route, Throwable cause) {

        return new ClientHttpResponse() {
            @Override
            public HttpStatus getStatusCode() throws IOException {
                // fallback时的状态码
                return HttpStatus.OK;
            }

            @Override
            public int getRawStatusCode() throws IOException {
                // 数字类型的状态码，本例返回的其实就是200，详见HttpStatus
                return this.getStatusCode().value();
            }

            @Override
            public String getStatusText() throws IOException {
                // 状态文本，本例返回的其实就是OK，详见HttpStatus
                return this.getStatusCode().getReasonPhrase();
            }

            @Override
            public void close() {
            }

            /***
             * 具体返回给客户端显示的内容
             * @return
             * @throws IOException
             */
            @Override
            public InputStream getBody() throws IOException {
                String errorInfo="{\"statusCode\":109,\"statusMsg\":\"网络请求超时，请稍后重试，服务["+route+"]已开启熔断机制\",\"data\":"+new JSONObject()+"}";
                log.info("--zuul熔断机制开启，微服务["+route+"]异常,请及时查看--");
                return new ByteArrayInputStream(errorInfo.getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                // headers设定
                HttpHeaders headers = new HttpHeaders();
                MediaType mt = new MediaType("application", "json", Charset.forName("UTF-8"));
                headers.setContentType(mt);

                return headers;
            }
        };
    }


}
