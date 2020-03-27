package com.busi.dao;

import com.busi.entity.CommunityHouse;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 居委会
 * @author: suntj
 * @create: 2020-03-18 11:32:23
 */
@Mapper
@Repository
public interface CommunityHouseDao {
    /***
     * 删除房屋
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityHouse" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and userId = #{userId}" +
            "</script>")
    int delResident(@Param("ids") String[] ids,@Param("userId") long userId);

    /***
     * 新增房屋
     * @param communityHouse
     * @return
     */
    @Insert("insert into CommunityHouse(communityId,userId,villageName,lat,lon,address,houseNumber,houseCompany,unitNumber,unitCompany,roomNumber,roomState,idCard,realName,phone,review,time,residence,livingRoom,toilet,housingArea,household,householdUserIds) " +
            "values (#{communityId},#{userId},#{villageName},#{lat},#{lon},#{address},#{houseNumber},#{houseCompany},#{unitNumber},#{unitCompany},#{roomNumber},#{roomState},#{idCard},#{realName},#{phone},#{review},#{time},#{residence},#{livingRoom},#{toilet},#{housingArea},#{household},#{householdUserIds})")
    @Options(useGeneratedKeys = true)
    int addCommunityHouse(CommunityHouse communityHouse);

    /***
     * 更新房屋
     * @param communityHouse
     * @return
     */
    @Update("<script>" +
            "update CommunityHouse set" +
            " villageName=#{villageName}," +
            " houseNumber=#{houseNumber}," +
            " houseCompany=#{houseCompany}," +
            " unitNumber=#{unitNumber}," +
            " unitCompany=#{unitCompany}," +
            " roomNumber=#{roomNumber}," +
            " roomState=#{roomState}," +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " address=#{address}," +
            " idCard=#{idCard}," +
            " realName=#{realName}," +
            " residence=#{residence}," +
            " livingRoom=#{livingRoom}," +
            " toilet=#{toilet}," +
            " housingArea=#{housingArea}," +
            " household=#{household}," +
            " householdUserIds=#{householdUserIds}," +
            " phone=#{phone}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeCommunityHouse(CommunityHouse communityHouse);

    /***
     * 查询房屋信息列表
     * @return
     */
    @Select("<script>" +
            "select * from CommunityHouse" +
            " where 1=1" +
            "<if test=\"userId != null and userId !=''\">" +
                " and householdUserIds LIKE CONCAT('%',#{userId},'%')" +
            "</if>" +
            " and communityId = #{communityId}" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityHouse> findCommunityHouseList(@Param("communityId") long communityId,@Param("userId") String userId);

    /***
     * 根据ID查询房屋信息
     * @param id
     * @return
     */
    @Select("select * from CommunityHouse where id = #{id}")
    CommunityHouse findCommunityHouse(@Param("id") long id);

}
