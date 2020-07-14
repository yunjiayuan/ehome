package com.busi.dao;

import com.busi.entity.HomeShopShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @program: ehome
 * @description: 二货购物车
 * @author: ZhaoJiaJie
 * @create: 2020-07-13 13:35:09
 */
@Mapper
@Repository
public interface HomeShopShoppingCartDao {

    /***
     * 新增
     * @param homeShopGoods
     * @return
     */
    @Insert("insert into HomeShopShoppingCart(userId,goodsId,goodsCoverUrl,goodsTitle,basicDescribe,specs,price,addTime,number) " +
            "values (#{userId},#{goodsId},#{goodsCoverUrl},#{goodsTitle},#{basicDescribe},#{specs},#{price},#{addTime},#{number})")
    @Options(useGeneratedKeys = true)
    int add(HomeShopShoppingCart homeShopGoods);

    /***
     * 更新
     * @param homeShopGoods
     * @return
     */
    @Update("<script>" +
            "update HomeShopShoppingCart set" +
            " number=#{number}," +
            " deleteType=#{deleteType}" +
            " where id=#{id} and userId=#{userId}" +
            "</script>")
    int update(HomeShopShoppingCart homeShopGoods);

    /***
     * 批量删除商品
     * @param ids
     * @return
     */
    @Update("<script>" +
            "update HomeShopShoppingCart set" +
            " deleteType=1" +
            " where id in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int updateDels(@Param("ids") String[] ids);

    /***
     * 删除购物车商品
     * @param ids
     * @return
     */
    @Delete("<script>" +
            "delete from HomeShopShoppingCart" +
            " where deleteType=0 and goodsId in" +
            "<foreach collection='ids' index='index' item='item' open='(' separator=',' close=')'>" +
            " #{item}" +
            "</foreach>" +
            "</script>")
    int delGoods(@Param("ids") String[] ids);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from HomeShopShoppingCart where userId=#{userId} and deleteType=0 order by addTime desc")
    List<HomeShopShoppingCart> findList(@Param("userId") long userId);

    /***
     * 根据userId查询
     * @param userId
     */
    @Select("select * from HomeShopShoppingCart where userId=#{userId} and deleteType>=1 order by deleteTime desc")
    List<HomeShopShoppingCart> findDeleteGoods(@Param("userId") long userId);

    /***
     * 根据goodsId查询
     * @param goodsId
     */
    @Select("select * from HomeShopShoppingCart where userId=#{userId} and goodsId=#{goodsId}")
    HomeShopShoppingCart findGoodsId(@Param("userId") long userId, @Param("goodsId") long goodsId);

    /***
     * 根据Id查询
     * @param id
     */
    @Select("select * from HomeShopShoppingCart where userId=#{userId} and id=#{id}")
    HomeShopShoppingCart findId(@Param("userId") long userId, @Param("id") long id);

    /***
     * 统计用户购物车商品数量
     * @param userId
     * @return
     */
    @Select("<script>" +
            "select count(id) from HomeShopShoppingCart" +
            " where 1=1 " +
            " and userId = #{userId} and deleteType=0" +
            "</script>")
    int findNum(@Param("userId") long userId);
}
