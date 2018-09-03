package com.busi.dao;

import com.busi.entity.SearchGoods;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 寻人寻物失物招领Dao
 * author：zhaojiajie
 * create time：2018-8-7 15:23:37
 */
@Mapper
@Repository
public interface SearchGoodsDao {

    /***
     * 新增寻人寻物失物招领
     * @param searchGoods
     * @return
     */
    @Insert("insert into searchGoods(userId,title,content,refreshTime,addTime,auditType,deleteType,searchType,goodsName,missingPlace," +
            "missingDate,missingTime,contactsName,contactsPhone,missingSex,age,imgUrl,province,city,district,longitude,latitude,fraction,seeNumber) " +
            "values (#{userId},#{title},#{content},#{refreshTime},#{addTime},#{auditType},#{deleteType},#{searchType},#{goodsName},#{missingPlace}," +
            "#{missingDate},#{missingTime},#{contactsName},#{contactsPhone},#{missingSex},#{age},#{imgUrl},#{province},#{city},#{district},#{longitude},#{latitude},#{fraction},#{seeNumber})")
    @Options(useGeneratedKeys = true)
    int add(SearchGoods searchGoods);

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Delete(("delete from searchGoods where id=#{id} and userId=#{userId}"))
    int del(@Param("id") long id, @Param("userId") long userId);

    /***
     * 更新寻人寻物失物招领信息
     * @param searchGoods
     * @return
     */
    @Update("<script>" +
            "update searchGoods set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"goodsName != null and goodsName != ''\">" +
            " goodsName=#{goodsName}," +
            "</if>" +
            "<if test=\"missingPlace != null and missingPlace != ''\">" +
            " missingPlace=#{missingPlace}," +
            "</if>" +
            "<if test=\"missingDate != null and missingDate != ''\">" +
            " missingDate=#{missingDate}," +
            "</if>" +
            "<if test=\"missingTime != null and missingTime != ''\">" +
            " missingTime=#{missingTime}," +
            "</if>" +
            "<if test=\"contactsName != null and contactsName != ''\">" +
            " contactsName=#{contactsName}," +
            "</if>" +
            "<if test=\"contactsPhone != null and contactsPhone != ''\">" +
            " contactsPhone=#{contactsPhone}," +
            "</if>" +
            "<if test=\"missingSex ==1 or missingSex == 2 \">" +
            " missingSex=#{missingSex}," +
            "</if>" +
            "<if test=\"age > 0 \">" +
            " age=#{age}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            "<if test=\"longitude > 0\">" +
            " longitude=#{longitude}," +
            "</if>" +
            "<if test=\"latitude > 0\">" +
            " latitude=#{latitude}," +
            "</if>" +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(SearchGoods searchGoods);

    /***
     * 更新删除状态
     * @param searchGoods
     * @return
     */
    @Update("<script>" +
            "update searchGoods set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(SearchGoods searchGoods);

    /***
     * 更新公告状态
     * @param searchGoods
     * @return
     */
    @Update("<script>" +
            "update searchGoods set" +
            " afficheStatus=#{afficheStatus}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateStatus(SearchGoods searchGoods);

    /***
     * 刷新公告时间
     * @param searchGoods
     * @return
     */
    @Update("<script>" +
            "update searchGoods set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int updateTime(SearchGoods searchGoods);

    /***
     * 根据Id查询用户寻人寻物失物招领信息
     * @param id
     */
    @Select("select * from searchGoods where id=#{id} and deleteType=1 and auditType=2")
    SearchGoods findUserById(@Param("id") long id);


    /***
     * 分页查询  默认按时间降序排序
     * @param province  省
     * @param city  市
     * @param district  区
     * @param beginAge  开始年龄
     * @param endAge  结束年龄
     * @param missingSex  失踪人性别:1男,2女
     * @param searchType  查找类别:0不限 ,1寻人,2寻物,3失物招领
     * @return
     */
    @Select("<script>" +
            "select * from searchGoods" +
            " where 1=1" +
            "<if test=\"userId > 0 \">" +
            " and userId = #{userId}" +
            "</if>" +
            "<if test=\"searchType > 0 \">" +
            " and searchType = #{searchType}" +
            "</if>" +
//            "<if test=\"beginAge > 0 \">"+
//            " and beginAge = #{beginAge}"+
//            "</if>" +
//            "<if test=\"endAge >= beginAge and endAge > 0 \">"+
//            " and age = #{endAge} "+
//            "</if>" +
            "<if test=\"missingSex != 0 \">" +
            " and missingSex = #{missingSex}" +
            "</if>" +
            "<if test=\"province != -1 \">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city != -1 \">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district != -1 \">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"endAge != 0 \">" +
            " and age >= #{beginAge}" +
            " and #{endAge} >= age" +
            "</if>" +
            " and auditType = 2" +
            " and deleteType = 1" +
            " order by fraction,refreshTime desc" +
            "</script>")
    List<SearchGoods> findList(@Param("userId") long userId, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("beginAge") int beginAge, @Param("endAge") int endAge, @Param("missingSex") int missingSex, @Param("searchType") int searchType);

    @Select("<script>" +
            "select * from searchGoods" +
            " where userId = #{userId}" +
            " and auditType = 2" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<SearchGoods> findUList(@Param("userId") long userId);

}
