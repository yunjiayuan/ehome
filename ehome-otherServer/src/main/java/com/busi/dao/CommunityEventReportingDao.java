package com.busi.dao;

import com.busi.entity.CommunityEventReporting;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @program: ehome
 * @description: 冠状病毒报备DAO
 * @author: suntj
 * @create: 2020-03-18 11:32:23
 */
@Mapper
@Repository
public interface CommunityEventReportingDao {
    /***
     * 删除冠状病毒报备
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from CommunityEventReporting" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and ( userId = #{userId} or communityHouseUserId = #{userId})" +
            "</script>")
    int delCommunityEventReporting(@Param("ids") String[] ids, @Param("userId") long userId);

    /***
     * 新增冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Insert("insert into CommunityEventReporting(communityId,communityHouseId,communityHouseUserId,userId,eventReportingType,villageName,houseNumber,houseCompany,unitNumber,unitCompany,roomNumber,roomState,idCard,realName,phone,review,time,departTime,placeOfDeparture,arriveTime,vehicle,trainNumber,contactHistory,remarks) " +
            "values (#{communityId},#{communityHouseId},#{communityHouseUserId},#{userId},#{eventReportingType},#{villageName},#{houseNumber},#{houseCompany},#{unitNumber},#{unitCompany},#{roomNumber},#{roomState},#{idCard},#{realName},#{phone},#{review},#{time},#{departTime},#{placeOfDeparture},#{arriveTime},#{vehicle},#{trainNumber},#{contactHistory},#{remarks})")
@Options(useGeneratedKeys = true)
    int addCommunityEventReporting(CommunityEventReporting communityEventReporting);

    /***
     * 更新冠状病毒报备
     * @param communityEventReporting
     * @return
     */
    @Update("<script>" +
            "update CommunityEventReporting set" +
            " eventReportingType=#{eventReportingType}," +
            " villageName=#{villageName}," +
            " houseNumber=#{houseNumber}," +
            " houseCompany=#{houseCompany}," +
            " unitNumber=#{unitNumber}," +
            " unitCompany=#{unitCompany}," +
            " roomNumber=#{roomNumber}," +
            " roomState=#{roomState}," +
            " idCard=#{idCard}," +
            " realName=#{realName}," +
            " departTime=#{departTime}," +
            " placeOfDeparture=#{placeOfDeparture}," +
            " arriveTime=#{arriveTime}," +
            " vehicle=#{vehicle}," +
            " trainNumber=#{trainNumber}," +
            " contactHistory=#{contactHistory}," +
            " remarks=#{remarks}," +
            " communityHouseId=#{communityHouseId}," +
            " communityHouseUserId=#{communityHouseUserId}," +
            " review=#{review}," +
            " phone=#{phone}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int changeCommunityEventReporting(CommunityEventReporting communityEventReporting);

    /***
     * 更新冠状病毒报备审核状态
     * @param communityEventReporting
     * @return
     */
    @Update("<script>" +
            "update CommunityEventReporting set" +
            " review=#{review}," +
            " message=#{message}" +
            " where id=#{id}" +
            "</script>")
    int toExamineCommunityEventReporting(CommunityEventReporting communityEventReporting);

    /***
     * 查询冠状病毒报备列表
     * @param communityId  居委会ID
     * @param userId  业主ID 0表示为业主或者管理员  具体的值表示住户查询
     * @param communityHouseId       大于0时 查询指定房屋下的报备信息
     * @param review         -1表示查询所有 0表示查询未审核 1表示查询已审核 2表示查询审核失败
     * @return
     */
    @Select("<script>" +
            "select * from CommunityEventReporting" +
            " where 1=1" +
            "<if test=\"communityHouseId > 0\">" +
                " and communityHouseId=#{communityHouseId}" +
            "</if>" +
            "<if test=\"userId > 0\">" +
                " and userId=#{userId}" +
            "</if>" +
            "<if test=\"review != -1\">" +
            " and review=#{review}" +
            "</if>" +
            " and communityId = #{communityId}" +
            " ORDER BY time desc" +
            "</script>")
    List<CommunityEventReporting> findCommunityEventReportingList(@Param("communityId") long communityId,@Param("userId") long userId, @Param("communityHouseId") long communityHouseId, @Param("review") int review);

    /***
     * 根据ID查询冠状病毒报备
     * @param id
     * @return
     */
    @Select("select * from CommunityEventReporting where id = #{id}")
    CommunityEventReporting findCommunityEventReporting(@Param("id") long id);

}
