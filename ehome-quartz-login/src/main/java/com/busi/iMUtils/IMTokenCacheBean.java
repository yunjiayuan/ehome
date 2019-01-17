package com.busi.iMUtils;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @program: ehome
 * @description: IM环信 即时通讯中 APP token
 * @author: ZHaoJiaJie
 * @create: 2019-01-17 13:31
 */
@Setter
@Getter
public class IMTokenCacheBean implements Serializable {

    private String access_token;//token 值

    private int expires_in;//token 有效时间，以秒为单位，在有效期内不需要重复获取

    private String application;//当前 APP 的 UUID 值
}
