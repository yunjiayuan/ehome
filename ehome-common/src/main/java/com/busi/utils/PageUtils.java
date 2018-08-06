package com.busi.utils;

import com.busi.entity.PageBean;
import com.github.pagehelper.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页工具类
 * author：SunTianJie
 * create time：2018/6/12 15:56
 */
public class PageUtils {

    /***
     * 封装pageBean
     * @param page
     * @param list
     * @return
     */
    public static PageBean getPageBean(Page page, List list) {
        PageBean pageBean = new PageBean();
        if (list == null) {
            pageBean.setList(new ArrayList());
        } else {
            pageBean.setList(list);
        }
        if (page != null) {
            pageBean.setPageNum(page.getPageNum());
            pageBean.setPages(page.getPages());
            pageBean.setPageSize(page.getPageSize());
            pageBean.setSize(page.size());
            pageBean.setTotal(page.getTotal());
        }
        return pageBean;
    }
}
