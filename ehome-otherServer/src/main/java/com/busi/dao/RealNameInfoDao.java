package com.busi.dao;

import com.busi.entity.RealNameInfo;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户实名DAO
 * author：SunTianJie
 * create time：2018/6/26 12:22
 */
@Mapper
@Repository
public interface RealNameInfoDao {

    /***
     * 新增
     * @param realNameInfo
     * @return
     */
    @Insert("insert into realNameInfo(userId,realName,cardNo,addrCode,birth,sex,length,checkBit,addr,province,city,area,time) " +
            "values (#{userId},#{realName},#{cardNo},#{addrCode},#{birth},#{sex},#{length},#{checkBit},#{addr},#{province},#{city},#{area},#{time})")
    @Options(useGeneratedKeys = true)
    int add(RealNameInfo realNameInfo);

    /***
     * 查询实名信息
     * @param realName 真实姓名
     * @param cardNo   身份证号
     * @return
     */
    @Select(("select * from realNameInfo where realName=#{realName} and cardNo=#{cardNo}"))
    List<RealNameInfo> findRealNameInfo(@Param("realName") String realName, @Param("cardNo") String cardNo);

}
