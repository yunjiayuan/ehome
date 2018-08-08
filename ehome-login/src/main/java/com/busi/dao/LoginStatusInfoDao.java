package com.busi.dao;

import com.busi.entity.LoginStatusInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.springframework.stereotype.Repository;

/**
 * 用户登录信息记录DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface LoginStatusInfoDao {

    /***
     * 新增记录
     * @param loginStatusInfo
     * @return
     */
    @Insert("insert into loginStatusInfo(id,userId,clientType,clientModel,clientSystemModel,serverVersion,appVersion,ip,time) values (#{id},#{userId},#{clientType},#{clientModel},#{clientSystemModel},#{serverVersion},#{appVersion},#{ip},#{time})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int add(LoginStatusInfo loginStatusInfo);

}
