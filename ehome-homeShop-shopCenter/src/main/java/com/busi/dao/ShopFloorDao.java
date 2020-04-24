package com.busi.dao;

import com.busi.entity.ShopFloor;
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
    @Insert("insert into ShopFloor(userId,shopName,shopHead,videoUrl,videoCoverUrl,content,payState,deleteType,addTime,lat,lon,address,villageName,villageOnly,identity,communityId,communityName)" +
            "values (#{userId},#{shopName},#{shopHead},#{videoUrl},#{videoCoverUrl},#{content},#{payState},#{deleteType},#{addTime},#{lat},#{lon},#{address},#{villageName},#{villageOnly},#{identity},#{communityId},#{communityName})")
    @Options(useGeneratedKeys = true)
    int addHomeShop(ShopFloor homeShopCenter);

    /***
     * 更新楼店
     * @param homeShopCenter
     * @return
     */
    @Update("<script>" +
            "update ShopFloor set" +
            " identity=#{identity}," +
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
            " shopState=#{shopState}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int updateBusiness(ShopFloor homeShopCenter);

    /***
     * 根据用户ID查询楼店状态
     * @param userId
     * @return
     */
    @Select("select * from ShopFloor where userId=#{userId} and deleteType=0 and villageOnly = #{villageOnly}")
    ShopFloor findByUserId(@Param("userId") long userId, @Param("villageOnly") String villageOnly);

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
     * 查询附近楼店
     * @param lat      纬度
     * @param lon      经度
     * @return
     */
    @Select("<script>" +
            "select * from ShopFloor where" +
            " deleteType = 0 and shopState=1" +
            " and lat > #{lat}-0.018018" +  //只对于经度和纬度大于或小于该用户两公里（1度111公里)范围内的用户进行距离计算,同时对数据表中的经度和纬度两个列增加了索引来优化where语句执行时的速度.
            " and lat &lt; #{lat}+0.018018 and lon > #{lon}-0.018018" +
            " and lon &lt; #{lon}+0.018018 order by ACOS(SIN((#{lat} * 3.1415) / 180 ) *SIN((lat * 3.1415) / 180 ) +COS((#{lat} * 3.1415) / 180 ) * COS((lat * 3.1415) / 180 ) *COS((#{lon}* 3.1415) / 180 - (lon * 3.1415) / 180 ) ) * 6380 asc" +
            "</script>")
    List<ShopFloor> findNearbySFList(@Param("lat") double lat, @Param("lon") double lon);

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
