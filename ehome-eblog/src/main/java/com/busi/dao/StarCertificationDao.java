package com.busi.dao;

import com.busi.entity.StarCertification;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

/**
 * @program: ehome
 * @description: 明星认证
 * @author: ZHaoJiaJie
 * @create: 2020-01-02 14:15
 */
@Mapper
@Repository
public interface StarCertificationDao {

    /***
     * 新增
     * @param starCertification
     * @return
     */
    @Insert("insert into StarCertification(userId,name,stageName,job,debutTime,idCard,age,sex,iDPositive,iDBack,state,addTime) " +
            "values (#{userId},#{name},#{stageName},#{job},#{debutTime},#{idCard},#{age},#{sex},#{iDPositive},#{iDBack},#{state},#{addTime})")
    @Options(useGeneratedKeys = true)
    int add(StarCertification starCertification);

    /***
     * 更新
     * @param starCertification
     * @return
     */
    @Update("<script>" +
            "update StarCertification set" +
            " state=#{state}" +
            " where userId=#{userId}" +
            "</script>")
    int update(StarCertification starCertification);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from StarCertification where userId = #{userId}")
    StarCertification find(@Param("userId") long userId);
}
