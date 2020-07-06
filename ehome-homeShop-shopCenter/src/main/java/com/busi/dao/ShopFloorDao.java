package com.busi.dao;

import com.busi.entity.ShopFloor;
import com.busi.entity.ShopFloorStatistics;
import com.busi.entity.ShopFloorTimeStatistics;
import com.busi.entity.YongHuiGoodsSort;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 楼店
 * @author: ZHaoJiaJie
 * @create: 2019-11-12 16:53
 */
@Mapper
@Repository
public interface ShopFloorDao {

    /***
     * 新增楼店
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into ShopFloor(userId,shopName,shopHead,videoUrl,videoCoverUrl,content,payState,deleteType,addTime,lat,lon,address,villageName,villageOnly,identity,communityId,communityName,telephone,distributionState,province,city,district)" +
            "values (#{userId},#{shopName},#{shopHead},#{videoUrl},#{videoCoverUrl},#{content},#{payState},#{deleteType},#{addTime},#{lat},#{lon},#{address},#{villageName},#{villageOnly},#{identity},#{communityId},#{communityName},#{telephone},#{distributionState},#{province},#{city},#{district})")
    @Options(useGeneratedKeys = true)
    int addHomeShop(ShopFloor homeShopCenter);

    /***
     * 更新楼店
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " telephone=#{telephone}," +
            " identity=#{identity}," +
            " province=#{province}," +
            " city=#{city}," +
            " district=#{district}," +
            " shopName=#{shopName}," +
            " shopHead=#{shopHead}," +
            " videoUrl=#{videoUrl}," +
            " videoCoverUrl=#{videoCoverUrl}," +
            " content=#{content}," +
            " address=#{address}," +
            " villageOnly=#{villageOnly}," +
            " villageName=#{villageName}," +
            " lon=#{lon}," +
            " lat=#{lat}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateHomeShop(ShopFloor homeShopCenter);

    /***
     * 新增楼店统计
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into ShopFloorStatistics(province,city,number,time,distributionState)" +
            "values (#{province},#{city},#{number},#{time},#{distributionState})")
    @Options(useGeneratedKeys = true)
    int addStatistics(ShopFloorStatistics homeShopCenter);

    /***
     * 新增楼店统计
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into ShopFloorTimeStatistics(province,city,number,time,distributionState)" +
            "values (#{province},#{city},#{number},#{time},#{distributionState})")
    @Options(useGeneratedKeys = true)
    int addStatistics2(ShopFloorTimeStatistics homeShopCenter);

    /***
     * 更新楼店统计
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloorStatistics set" +
            " time=#{time}," +
            " number=#{number}" +
            " where id=#{id}" +
            "</script>")
    int upStatistics(ShopFloorStatistics homeShopCenter);

    /***
     * 更新楼店统计
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloorTimeStatistics set" +
            " number=#{number}" +
            " where id=#{id}" +
            "</script>")
    int upStatistics2(ShopFloorTimeStatistics homeShopCenter);

    /***
     * 更新楼店保证金支付状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " payState=#{payState}" +
            " where userId=#{userId} and villageOnly=#{villageOnly}" +
            "</script>")
    int updatePayStates(ShopFloor homeShopCenter);

    /***
     * 更新楼店营业状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " payState=1," +
            " shopState=#{shopState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(ShopFloor homeShopCenter);

    /***
     * 更新楼店配货状态
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " distributionState=1," +
            " distributionTime=#{distributionTime}" +
            " where id=#{id} and deleteType=0 and payState=1" +
            "</script>")
    int upDistributionStatus(ShopFloor homeShopCenter);

    /***
     * 根据用户ID查询楼店状态
     * @param userId
     * @return
     */
    @Select("select * from ShopFloor where userId=#{userId} and deleteType=0 and villageOnly = #{villageOnly}")
    ShopFloor findByUserId(@Param("userId") long userId, @Param("villageOnly") String villageOnly);

    /***
     * 根据ID查询楼店
     * @param id
     * @return
     */
    @Select("select * from ShopFloor where id=#{id} and deleteType=0 and payState=1 ")
    ShopFloor findId(@Param("id") long id);

    /***
     * 根据ID查询楼店
     * @param id
     * @return
     */
    @Select("select * from ShopFloor where id=#{id} and deleteType=0")
    ShopFloor findId2(@Param("id") long id);

    /***
     * 查询所有店铺
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloor" +
            " where 1=1" +
            " and villageOnly in" +
            "<foreach collection='villageOnly' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            " and deleteType=0" +
            "</script>")
    List<ShopFloor> findByIds(@Param("villageOnly") String[] villageOnly);

    /***
     * 查询黑店列表
     * @param province     省 (经纬度>0时默认-1)
     * @param city      市 (经纬度>0时默认-1)
     * @param district    区 (经纬度>0时默认-1)
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloor where" +
            " deleteType = 0 and shopState=1 and payState=1" +
            "<if test=\"shopState >= 0\">" +
            " and distributionState = #{shopState}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"date != null and date != ''\">" +
            " and TO_DAYS(addTime)=TO_DAYS(#{date})" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloor> findNearbySFList(@Param("date") String date, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("shopState") int shopState);

    @Select("<script>" +
            "select * from ShopFloor where" +
            " deleteType = 0" +
            "<if test=\"shopState >= 0\">" +
            " and distributionState = #{shopState}" +
            "</if>" +
            "<if test=\"province >= 0\">" +
            " and province = #{province}" +
            "</if>" +
            "<if test=\"city >= 0\">" +
            " and city = #{city}" +
            "</if>" +
            "<if test=\"district >= 0\">" +
            " and district = #{district}" +
            "</if>" +
            "<if test=\"date != null and date != ''\">" +
            " and TO_DAYS(addTime)=TO_DAYS(#{date})" +
            "</if>" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloor> findNearbySFList4(@Param("date") String date, @Param("province") int province, @Param("city") int city, @Param("district") int district, @Param("shopState") int shopState);


    @Select("select * from ShopFloorStatistics where" +
            " distributionState = 0" +
            " and province = #{province}" +
            " and city = #{city}"
    )
    ShopFloorStatistics findStatistics(@Param("province") int province, @Param("city") int city);

    @Select("select * from ShopFloorStatistics where" +
            " distributionState = 1" +
            " and province = #{province}" +
            " and city = #{city}"
    )
    ShopFloorStatistics findStatistics2(@Param("province") int province, @Param("city") int city);

    @Select("select * from ShopFloorTimeStatistics where" +
            " distributionState = 0" +
            " and province = #{province}" +
            " and city = #{city}" +
            " and TO_DAYS(time)=TO_DAYS(NOW())"
    )
    ShopFloorTimeStatistics findStatistics3(@Param("province") int province, @Param("city") int city);

    @Select("select * from ShopFloorTimeStatistics where" +
            " distributionState = 0" +
            " and province = #{province}" +
            " and city = #{city}"
    )
    ShopFloorTimeStatistics findStatistics4(@Param("province") int province, @Param("city") int city);

    @Select("select * from ShopFloorTimeStatistics where" +
            " distributionState = 1" +
            " and province = #{province}" +
            " and city = #{city}"
    )
    ShopFloorTimeStatistics findStatistics5(@Param("province") int province, @Param("city") int city);

    @Select("<script>" +
//            "select * from ShopFloor where" +
//            " deleteType = 0 and shopState=1 and payState=1" +
//            " and lat > #{lat}-0.045045" +  //只对于经度和纬度大于或小于该用户5公里（1度111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
//            " and lat &lt; #{lat}+0.045045 and lon > #{lon}-0.045045" +
//            " and lon &lt; #{lon}+0.045045 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            " select *, ROUND(6378.138*2*ASIN(SQRT(POW(SIN((#{lat}*PI()/180-lat*PI()/180)/2),2)+COS(#{lat}*PI()/180)*COS(lat*PI()/180)*POW(SIN((#{lon}*PI()/180-lon*PI()/180)/2),2)))*1000) AS juli " +
            " from ShopFloor " +
            " where shopState=1 and deleteType = 0 and payState=1" +
            " and lat > #{lat}-0.045045" +  //只对于经度和纬度大于或小于该用户5公里（1度111公里)范围内的用户进行距离计算
            " and lat &lt; #{lat}+0.045045" +
            " and lon > #{lon}-0.045045" +
            " and lon &lt; #{lon}+0.045045" +
            " order by juli asc" +
            "</script>")
    List<ShopFloor> findNearbySFList3(@Param("lat") double lat, @Param("lon") double lon);


    /***
     * 模糊查询楼店
     * @param shopName   店铺名称
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloor where" +
            " deleteType = 0 and shopState>0" +
            " and payState=1 and shopName LIKE CONCAT('%',#{shopName},'%')" +
            " order by addTime desc" +
            "</script>")
    List<ShopFloor> findNearbySFList2(@Param("shopName") String shopName);

    /***
     * 查询用户楼店
     * @param userId   用户
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloor where" +
            " deleteType = 0 and shopState>0" +
            " and payState=1 and userId=#{userId}" +
            "</script>")
    List<ShopFloor> findUserSFlist(@Param("userId") long userId);

    /***
     * 查询楼店
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloorStatistics where" +
            " number > 0" +
            "<if test=\"shopState >= 0\">" +
            " and distributionState=#{shopState}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<ShopFloorStatistics> findRegionSFlist(@Param("shopState") int shopState);

    @Select("<script>" +
            "select * from ShopFloorTimeStatistics where" +
            " number > 0" +
            "<if test=\"shopState >= 0\">" +
            " and distributionState=#{shopState}" +
            "</if>" +
            " order by time desc" +
            "</script>")
    List<ShopFloorTimeStatistics> findTimeSFlist(@Param("shopState") int shopState);

    /***
     * 新增永辉分类
     * @param homeShopCenter
     * @return
     */
    @Insert("insert into YongHuiGoodsSort(name,levelOne,levelTwo,levelThree,letter,picture,enabled)" +
            "values (#{name},#{levelOne},#{levelTwo},#{levelThree},#{letter},#{picture},#{enabled})")
    @Options(useGeneratedKeys = true)
    int addYHSort(YongHuiGoodsSort homeShopCenter);

    /***
     * 查询永辉分类
     * @param levelOne 商品1级分类    -2为不限
     * @param levelTwo 商品2级分类    -2为不限
     * @param levelThree 商品3级分类  -2为不限
     * @return
     */
    @Select("<script>" +
            "select * from YongHuiGoodsSort" +
            " where 1=1" +

            "<if test=\"levelOne == -2 \">" +
            " and levelOne > -1" +
            " and levelTwo = -1" +
            " and levelThree = -1" +
            "</if>" +

            "<if test=\"levelOne >= 0 \">" +
            "<if test=\"levelTwo == -2 \">" +
            " and levelOne = #{levelOne}" +
            " and levelTwo > -1" +
            " and levelThree = -1" +
            "</if>" +
            "<if test=\"levelTwo > -1 \">" +
            " and levelOne = #{levelOne}" +
            " and levelTwo = #{levelTwo}" +
            "<if test=\"levelThree >= 0\">" +
            " and levelThree = #{levelThree}" +
            "</if>" +
            "<if test=\"levelThree == -2\">" +
            " and levelThree > -1" +
            "</if>" +
            "</if>" +
            "</if>" +

            " and enabled = 0" +
            "</script>")
    List<YongHuiGoodsSort> findYHSort(@Param("levelOne") int levelOne, @Param("levelTwo") int levelTwo, @Param("levelThree") int levelThree, @Param("letter") String letter);

    /***
     * 更新永辉分类
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update YongHuiGoodsSort set" +
            " name=#{name}," +
            " levelOne=#{levelOne}," +
            " levelTwo=#{levelTwo}," +
            " levelThree=#{levelThree}," +
            " letter=#{letter}," +
            " picture=#{picture}," +
            " enabled=#{enabled}" +
            " where id=#{id}" +
            "</script>")
    int changeYHSort(YongHuiGoodsSort homeShopCenter);


    /***
     * 删除永辉分类
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from YongHuiGoodsSort" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delYHSort(@Param("ids") String[] ids);
}
