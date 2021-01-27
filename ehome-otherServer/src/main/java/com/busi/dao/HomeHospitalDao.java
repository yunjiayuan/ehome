package com.busi.dao;

import com.busi.entity.HomeHospital;
import com.busi.entity.HomeHospitalRecord;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 家医馆相关Dao
 * author：zhaojiajie
 * create time：2020-1-7 17:31:45
 */
@Mapper
@Repository
public interface HomeHospitalDao {

    /***
     * 新增
     * @param kitchen
     * @return
     */
    @Insert("insert into HomeHospital(userId,businessStatus,department,auditType,title,physicianName,hospital,major,addTime,imgUrl,headCover,content,jobStatus,province,city,district," +
            "videoUrl,videoCoverUrl,helpNumber,practiceNumber,longitude,latitude,cityId,age,sex)" +
            "values (#{userId},#{businessStatus},#{department},#{auditType},#{title},#{physicianName},#{hospital},#{major},#{addTime},#{imgUrl},#{headCover},#{content},#{jobStatus},#{province},#{city},#{district}" +
            ",#{videoUrl},#{videoCoverUrl},#{helpNumber},#{practiceNumber},#{longitude},#{latitude},#{cityId},#{age},#{sex})")
    @Options(useGeneratedKeys = true)
    int add(HomeHospital kitchen);

    /***
     * 更新
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospital set" +
            " longitude=#{longitude}," +
            " physicianName=#{physicianName}," +
            " age=#{age}," +
            " sex=#{sex}," +
            " latitude=#{latitude}," +
            " cityId=#{cityId}," +
            " city=#{city}," +
            " major=#{major}," +
            " title=#{title}," +
            " imgUrl=#{imgUrl}," +
            " content=#{content}," +
            " hospital=#{hospital}," +
            " videoUrl=#{videoUrl}," +
            " province=#{province}," +
            " district=#{district}," +
            " jobStatus=#{jobStatus}," +
            " headCover=#{headCover}," +
            " department=#{department}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " practiceNumber=#{practiceNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(HomeHospital kitchen);

    @Update("<script>" +
            "update HomeHospital set" +
            " physicianName=#{physicianName}," +
            " age=#{age}," +
            " sex=#{sex}," +
            " imgUrl=#{imgUrl}," +
            " practiceNumber=#{practiceNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update2(HomeHospital kitchen);

    /***
     * 更新删除状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospital set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(HomeHospital kitchen);

    /***
     * 更新帮助人数
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospital set" +
            " helpNumber=#{helpNumber}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateNumber(HomeHospital kitchen);

    /***
     * 更新营业状态
     * @param kitchen
     * @return
     */
    @Update("<script>" +
            "update HomeHospital set" +
            " businessStatus=#{businessStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(HomeHospital kitchen);

    /***
     * 根据userId查询
     * @param userId
     * @return
     */
    @Select("select * from HomeHospital where userId=#{userId}")
    HomeHospital findByUserId(@Param("userId") long userId);

    @Select("select * from HomeHospital where id=#{id}")
    HomeHospital findById(@Param("id") long id);

    /***
     * 查询列表
     * @param search    模糊搜索（可以是：症状、疾病、医院、科室、医生名字）
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospital" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"department >= 0\">" +
            " and department=#{department}" +
            "</if>" +
            " and (physicianName LIKE CONCAT('%',#{search},'%')" +
            " or hospital LIKE CONCAT('%',#{search},'%')" +
            " or major LIKE CONCAT('%',#{search},'%'))" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by helpNumber desc" +
            "</script>")
    List<HomeHospital> findList2(@Param("watchVideos") int watchVideos, @Param("userId") long userId, @Param("search") String search, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("department") int department);

    /***
     * 查询列表
     * @param province     省
     * @param city      市
     * @param district    区
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospital" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId}" +
            "<if test=\"department >= 0\">" +
            " and department=#{department}" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"watchVideos == 1\">" +
            " and videoUrl != ''" +
            "</if>" +
            " order by helpNumber desc" +
            "</script>")
    List<HomeHospital> findList3(@Param("watchVideos") int watchVideos, @Param("userId") long userId, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("department") int department);

    /***
     * 查询列表
     * @param userId
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospital" +
            " where businessStatus=0 and deleteType = 0 and auditType=1 " +
            " and userId != #{userId} and cityId = #{cityId}" +
            "</script>")
    List<HomeHospital> findList4(@Param("cityId") int cityId, @Param("userId") long userId);

    /***
     * 按用户查询列表
     * @param users
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospital" +
            " where deleteType = 0 and auditType=1 " +
            " and userId in" +
            "<foreach collection='users' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    List<HomeHospital> findUsersList(@Param("users") String[] users);

    /***
     * 查询等待人员列表(默认第一位是正在咨询中，其余为等待中)
     * @param userId   医师ID
     * @return
     */
    @Select("<script>" +
            "select * from HomeHospitalRecord" +
            " where consultationStatus &lt; 2 and deleteType = 0 and payState=1 and doctorId=#{userId}" +
            " order by consultationStatus desc,time asc" +
            "</script>")
    List<HomeHospitalRecord> findWaitList(@Param("userId") long userId);
}
