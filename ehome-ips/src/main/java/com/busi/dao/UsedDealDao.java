package com.busi.dao;

import com.busi.entity.UsedDeal;
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
public interface UsedDealDao {

    /***
     * 新增
     * @param usedDeal
     * @return
     */
    @Insert("insert into usedDeal(usedSort1,usedSort2,usedSort3,basicParame1,basicParame2,basicParame3,basicParame4,userId,title,content," +
            "seeNumber,deleteType,auditType,releaseTime,refreshTime,imgUrl,problemType,otherProblem,sellingPrice,buyingPrice,pinkageType,negotiable,toPay,merchantType,expressMode,province,city,district,sellType,lat,lon,fraction) " +
            "values (#{usedSort1},#{usedSort2},#{usedSort3},#{basicParame1},#{basicParame2},#{basicParame3},#{basicParame4},#{userId},#{title},#{content}," +
            "#{seeNumber},#{deleteType},#{auditType},#{releaseTime},#{refreshTime},#{imgUrl},#{problemType},#{otherProblem},#{sellingPrice},#{buyingPrice},#{pinkageType},#{negotiable},#{toPay},#{merchantType},#{expressMode},#{province},#{city},#{district},#{sellType},#{lat},#{lon},#{fraction})")
    @Options(useGeneratedKeys = true)
    int add(UsedDeal usedDeal);

    /***
     * 删除
     * @param id
     * @param userId
     * @return
     */
    @Delete(("delete from usedDeal where id=#{id} and userId=#{userId}"))
    int del(@Param("id") long id, @Param("userId") long userId);

    /***
     * 更新
     * @param usedDeal
     * @return
     */
    @Update("<script>" +
            "update usedDeal set" +
            "<if test=\"title != null and title != ''\">" +
            " title=#{title}," +
            "</if>" +
            "<if test=\"content != null and content != ''\">" +
            " content=#{content}," +
            "</if>" +
            "<if test=\"imgUrl != null and imgUrl != ''\">" +
            " imgUrl=#{imgUrl}," +
            "</if>" +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " usedSort1=#{usedSort1}," +
            " usedSort2=#{usedSort2}," +
            " usedSort3=#{usedSort3}," +
            " basicParame1=#{basicParame1}," +
            " basicParame2=#{basicParame2}," +
            " basicParame3=#{basicParame3}," +
            " basicParame4=#{basicParame4}," +
            " problemType=#{problemType}," +
            " otherProblem=#{otherProblem}," +
            " sellingPrice=#{sellingPrice}," +
            " buyingPrice=#{buyingPrice}," +
            " pinkageType=#{pinkageType}," +
            " negotiable=#{negotiable}," +
            " sellingPrice=#{sellingPrice}," +
            " toPay=#{toPay}," +
            " merchantType=#{merchantType}," +
            " expressMode=#{expressMode}," +
            " lat=#{lat}," +
            " lon=#{lon}," +
            " fraction=#{fraction}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(UsedDeal usedDeal);

    /***
     * 更新删除状态
     * @param usedDeal
     * @return
     */
    @Update("<script>" +
            "update usedDeal set" +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateDel(UsedDeal usedDeal);

    /***
     * 更新公告买卖状态
     * @param usedDeal
     * @return
     */
    @Update("<script>" +
            "update usedDeal set" +
            " sellType=#{sellType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateStatus(UsedDeal usedDeal);

    /***
     * 刷新公告时间
     * @param usedDeal
     * @return
     */
    @Update("<script>" +
            "update usedDeal set" +
            " refreshTime=#{refreshTime}" +
            " where id=#{id} and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int updateTime(UsedDeal usedDeal);

    /***
     * 置顶公告
     * @param usedDeal
     * @return
     */
    @Update("<script>" +
            "update usedDeal set" +
            " frontPlaceType=#{frontPlaceType}" +
            " where id=#{id} and userId=#{userId} " +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int setTop(UsedDeal usedDeal);

    /***
     * 统计当月置顶次数
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(*) from usedDeal" +
            " where DATE_FORMAT( refreshTime, '%Y%m' ) = DATE_FORMAT( CURDATE( ) , '%Y%m' )" +
            " and frontPlaceType > 0" +
            " and userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "</script>")
    int statistics(@Param("userId") long userId);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from usedDeal where id=#{id} and deleteType=1 and auditType=2")
    UsedDeal findUserById(@Param("id") long id);


    /***
     * 分页查询
     * @param sort  排序条件:0默认排序，1最新发布，2价格最低，3价格最高，4离我最近
     * @param userId  用户ID
     * @param minPrice  最小价格
     * @param maxPrice  最大价格
     * @param usedSort1  一级分类:起始值为0,默认-1为不限 :二手手机 、数码、汽车...
     * @param usedSort2  二级分类:起始值为0,默认-1为不限 : 苹果,三星,联想....
     * @param usedSort3  三级分类:起始值为0,默认-1为不限 :iPhone6s.iPhone5s....
     * @param province  省
     * @param city  市
     * @param district  区
     * @return
     */
    @Select("<script>" +
            "select * from usedDeal" +
            " where sellType = 1" +
            "<if test=\"userId > 0\">" +
            " and userId = #{userId}" +
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
            "<if test=\"usedSort3 >= 0\">" +
            " and usedSort3 = #{usedSort3}" +
            "</if>" +
            "<if test=\"usedSort2 >= 0\">" +
            " and usedSort2 = #{usedSort2}" +
            "</if>" +
            "<if test=\"usedSort1 >= 0\">" +
            " and usedSort1 = #{usedSort1}" +
            "</if>" +
            "<if test=\"maxPrice > 0\">" +
            " <![CDATA[ " +
            " and sellingPrice <= #{minPrice} and sellingPrice <= #{maxPrice}" +
            "  ]]> " +
            "</if>" +
            "<if test=\"maxPrice &lt;= 0\">" +
            " and sellingPrice >= #{minPrice}" +
            "</if>" +
            " and auditType = 2" +
            " and deleteType = 1" +
            "<if test=\"sort &lt;= 1\">" +
            " order by refreshTime desc" +
            "</if>" +
            "<if test=\"sort == 2\">" +
            " order by sellingPrice desc" +
            "</if>" +
            "<if test=\"sort == 3\">" +
            " order by sellingPrice asc" +
            "</if>" +
            "</script>")
    List<UsedDeal> findList(@Param("sort") int sort, @Param("userId") long userId, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, @Param("usedSort1") int usedSort1, @Param("usedSort2") int usedSort2, @Param("usedSort3") int usedSort3);

    @Select("<script>" +
            "select * from usedDeal" +
            " where userId = #{userId}" +
            " and sellType=#{sellType}" +
            " and auditType = 2" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<UsedDeal> findUList(@Param("userId") long userId, @Param("sellType") int sellType);

    @Select("<script>" +
            "select * from usedDeal" +
            " where auditType = 2" +
            "<if test=\"userId != 0\">" +
            " and userId = #{userId}" +
            "</if>" +
            " and deleteType = 1" +
            " order by refreshTime desc" +
            "</script>")
    List<UsedDeal> findHomeList(@Param("userId") long userId);

    /***
     * 分页查询
     * @param lat  纬度
     * @param lon  经度
     * @return
     */
    @Select("<script>" +
            "select id,usedSort1,usedSort2,usedSort3,basicParame1,basicParame2,basicParame3,basicParame4,userId,title,content,seeNumber,deleteType,auditType,releaseTime,refreshTime,imgUrl,problemType,otherProblem,sellingPrice,buyingPrice,pinkageType,negotiable,toPay,merchantType,expressMode,province,city,district,sellType,lat,lon," +
            " ROUND(6378.138 * 2 * ASIN(SQRT(POW(SIN((#{lat} * PI() / 180 - lon * PI() / 180) / 2),2) + COS(#{lat} * PI() / 180) * COS(lon * PI() / 180) * POW(SIN((#{lon} * PI() / 180 - lat * PI() / 180) / 2),2))) * 1000) AS distance" +
            " FROM usedDeal ORDER BY distance ASC" +
            "</script>")
    List<UsedDeal> findAoList(@Param("lat") double lat, @Param("lon") double lon);


    /***
     * 统计已上架,已卖出已下架,我的订单数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from usedDeal" +
            " where userId=#{userId}" +
            " and auditType = 2 and deleteType = 1" +
            "<if test=\"type == 1\">" +
            " and sellType=1" +
            "</if>" +
            "<if test=\"type == 2\">" +
            " and sellType=2" +
            "</if>" +
            "</script>")
    int findNum(@Param("userId") long userId, @Param("type") int type);

}
