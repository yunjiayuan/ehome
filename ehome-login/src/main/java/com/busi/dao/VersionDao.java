package com.busi.dao;

import com.busi.entity.UserInfo;
import com.busi.entity.Version;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 版本信息DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface VersionDao {

    /***
     * 更新版本信息
     * @param version
     * @return
     */
    @Update(("update version set updateType=#{updateType},version=#{version} where clientType=#{clientType}"))
    int update(Version version);

    /***
     * 查询版本号信息
     * @param clientType
     * @return
     */
    @Select(("select * from version where clientType=#{clientType}"))
    Version findVersion(@Param("clientType") int clientType);

}
