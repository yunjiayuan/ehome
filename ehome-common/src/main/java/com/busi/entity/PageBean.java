package com.busi.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * 分页实体类
 * author：SunTianJie
 * create time：2018/6/12 15:37
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageBean<T> {
    private long total;      // 总记录数
    private int pages;       // 总页数
    private int pageNum;     // 当前页码，当前是第几页
    private int pageSize;    // 每页要查询的记录数
    private int size;        // 当前页实际查询出的记录数 <= pageSize，该属性来自ArrayList的size属性
    private List<T> list;    // 结果集

}
