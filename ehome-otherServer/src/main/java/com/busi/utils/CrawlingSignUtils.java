package com.busi.utils;

import com.busi.entity.Drawings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

/***
 * 获取指定HTML标签的相关内容
 * author：zhaojiajie
 * create time：2020-09-15 13:30:41
 */
@Component
public class CrawlingSignUtils extends Thread {

    /**
     * @param url 访问路径
     * @return
     */
    public Document getDocument(String url) {
        try {
            //5000是设置连接超时时间，单位ms
            return Jsoup.connect(url).timeout(5000).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 测试
     *
     * @return
     */
//    public static void main(String[] args) {

//        CrawlingSign t = new CrawlingSign();
//        Document doc = t.getDocument("http://www.d5168.com/chouqian/guanyin/4/1/");
//        // 获取目标HTML代码
//        Elements elements1 = doc.select("[class=result-detail divcss5-b]");
//
//        Elements content = elements1.select("P");
//        String goodOrBad = elements1.get(2).text();
//        System.out.println("吉凶宫位： " + goodOrBad + elements1.get(3).text());
//
//        String poeticFlavour = content.get(5).text();
//        System.out.println("诗意： " + poeticFlavour);
//
//        String heSaid = content.get(7).text();
//        System.out.println("解曰： " + heSaid);
//
//        String immortalMachine = content.get(9).text();
//        System.out.println("仙机：" + immortalMachine);
//
//        String whole = content.get(11).text();
//        System.out.println("整体解译：" + whole + content.get(12).text() + content.get(13).text() + content.get(14).text() + content.get(15).text());
//
//        String quintessence = content.get(17).text();
//        System.out.println("本签精髓：" + quintessence);
//
//        String allusion = content.get(20).text();
//        System.out.println("典故：" + allusion);
//
//        String cause = content.get(23).text();
//        System.out.println("工作求职/创业事业：" + cause + content.get(24).text());
//
//        String business = content.get(9).text();
//        System.out.println("经商生意：" + business);
//
//        String investment = content.get(10).text();
//        System.out.println("投资理财：" + investment);
//
//        String love = content.get(11).text();
//        System.out.println("爱情婚姻：" + love);
//
//        String work = content.get(12).text();
//        System.out.println("凡事做事：" + work);
//
//        String travelFar = content.get(13).text();
//        System.out.println("远行出国：" + travelFar);
//
//        String seek = content.get(14).text();
//        System.out.println("寻人寻物：" + seek);
//
//        String lawsuit = content.get(15).text();
//        System.out.println("官司诉讼：" + lawsuit);
//
//        String prayForAson = content.get(16).text();
//        System.out.println("求孕求子：" + prayForAson);
//
//        String examination = content.get(17).text();
//        System.out.println("考试竞赛/升迁竞选：" + examination);
//
//        String transaction = content.get(18).text();
//        System.out.println("房地交易：" + transaction);
//
//        String changes = content.get(19).text();
//        System.out.println("转换变更：" + changes);
//
//        String healthy = content.get(20).text();
//        System.out.println("治病健康：" + healthy);

//        t.run();
//
//    }

    //文件路径
    public static String PATH = "D:\\JSOUP\\";

    /**
     * 创建文件
     *
     * @param fileName
     * @return
     */
    public static void createFile(File fileName) throws Exception {
        try {
            if (!fileName.exists()) { // 判断文件或文件夹是否存在。如果存在，就不创建文件，如果不存在，就创建一个文件
                fileName.createNewFile();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //将获取的内容输入到后缀为.txt的文本中
    public static void writeTxtFile(String content, File fileName) throws Exception {
        RandomAccessFile mm = null; // 随机访问文件的读取与写入
        FileOutputStream o = null; // 输出流，写出数据
        try {
            o = new FileOutputStream(fileName); // 对文件fileName进行写出
            o.write(content.getBytes("UTF-8")); // 写出编码格式为utf-8
            o.close(); // 关闭写出
        } catch (Exception e) {

            e.printStackTrace();
        } finally {
            if (mm != null) {
                mm.close();
            }
        }
    }

    //提取网页文本并保存到本地
    public Drawings run(@RequestBody Drawings drawings) {
//        currentThread().setName("新增中···"); // 获取线程名称：新增中···
//        String title; // 标题
        String content; // 内容
        CrawlingSignUtils t = new CrawlingSignUtils();
        int signNum = drawings.getSignNum();//阿拉伯签号
        Document doc = t.getDocument("http://www.d5168.com/chouqian/guanyin/4/" + signNum + "/");
        // 获取目标HTML代码
        Elements elements1 = doc.select("[class=result-detail divcss5-b]");
        try {
//            title = doc.select("title").get(0).text(); // 获取标签为p的标题
//            content = elements1.select("p").text(); // 获取标签为p的内容
            content = elements1.text(); // 获取内容

            drawings.setSignNum(signNum);//数字签号
            System.out.println("数字签号： " + drawings.getSignNum());


            drawings.setSign("第" + int2chineseNum(signNum) + "签");//大写签号
            System.out.println("中文签号： " + drawings.getSign());

            drawings.setAllusionName(content.substring(content.indexOf("观音灵签+" + signNum + "+签 观音灵签解签 第" + signNum + "签 ") + 21, content.indexOf("签) 您抽到了") - 3));//典故名称
            System.out.println("典故名称： " + drawings.getAllusionName());

            String goodOrBad = content.substring(content.indexOf("吉凶宫位: ") + 6, content.indexOf("观音灵签: 诗意"));
            StringBuffer stringBuffer = new StringBuffer(goodOrBad);
            stringBuffer.insert(0, "[");
            stringBuffer.insert(5, "]");
            drawings.setGoodOrBad(stringBuffer.toString());
            System.out.println("吉凶宫位： " + drawings.getGoodOrBad());

            drawings.setPoeticFlavour(content.substring(content.indexOf("观音灵签: 诗意 ") + 9, content.indexOf("观音灵签: 解曰")));
            System.out.println("诗意： " + drawings.getPoeticFlavour());

            drawings.setHeSaid(content.substring(content.indexOf("观音灵签: 解曰 ") + 9, content.indexOf("观音灵签: 仙机")));
            System.out.println("解曰： " + drawings.getHeSaid());

            drawings.setImmortalMachine(content.substring(content.indexOf("观音灵签: 仙机 ") + 9, content.indexOf("整体解译")));
            drawings.setImmortalMachine(drawings.getImmortalMachine().substring(0, drawings.getImmortalMachine().indexOf(" 观音灵签")));
            System.out.println("仙机：" + drawings.getImmortalMachine());

            drawings.setWhole(content.substring(content.indexOf("整体解译 ") + 5, content.indexOf("本签精髓")));
            System.out.println("整体解译：" + drawings.getWhole());

            drawings.setQuintessence(content.substring(content.indexOf("本签精髓 ") + 5, content.indexOf(" 观音灵签白话详解如下")));
            System.out.println("本签精髓：" + drawings.getQuintessence());

            drawings.setAllusion(content.substring(content.indexOf("签 典故 ") + 5, content.indexOf(" 工作求职 创业事业")));
            System.out.println("典故：" + drawings.getAllusion());

            drawings.setCause(content.substring(content.indexOf("工作求职 创业事业 ") + 10, content.indexOf(" 经商生意")));
            System.out.println("工作求职/创业事业：" + drawings.getCause());

            drawings.setBusiness(content.substring(content.indexOf(" 经商生意") + 5, content.indexOf(" 投资理财")));
            System.out.println("经商生意：" + drawings.getBusiness());

            drawings.setInvestment(content.substring(content.indexOf(" 投资理财 ") + 6, content.indexOf(" 爱情婚姻")));
            System.out.println("投资理财：" + drawings.getInvestment());

            drawings.setLove(content.substring(content.indexOf(" 爱情婚姻 ") + 6, content.indexOf(" 凡事做事 ")));
            System.out.println("爱情婚姻：" + drawings.getLove());

            drawings.setWork(content.substring(content.indexOf(" 凡事做事 ") + 6, content.indexOf(" 远行出国")));
            System.out.println("凡事做事：" + drawings.getWork());

            drawings.setTravelFar(content.substring(content.indexOf(" 远行出国") + 5, content.indexOf(" 寻人寻物")));
            System.out.println("远行出国：" + drawings.getTravelFar());

            drawings.setSeek(content.substring(content.indexOf(" 寻人寻物") + 5, content.indexOf(" 官司诉讼")));
            System.out.println("寻人寻物：" + drawings.getSeek());

            drawings.setLawsuit(content.substring(content.indexOf(" 官司诉讼") + 5, content.indexOf(" 求孕求子")));
            System.out.println("官司诉讼：" + drawings.getLawsuit());

            drawings.setPrayForAson(content.substring(content.indexOf(" 求孕求子") + 5, content.indexOf(" 考试竞赛 升迁竞选")));
            System.out.println("求孕求子：" + drawings.getPrayForAson());

            drawings.setExamination(content.substring(content.indexOf(" 考试竞赛 升迁竞选 ") + 11, content.indexOf(" 房地交易")));
            System.out.println("考试竞赛/升迁竞选：" + drawings.getExamination());

            drawings.setTransaction(content.substring(content.indexOf(" 房地交易") + 5, content.indexOf(" 转换变更")));
            System.out.println("房地交易：" + drawings.getTransaction());

            drawings.setChanges(content.substring(content.indexOf("转换变更") + 4, content.indexOf(" 治病健康")));
            System.out.println("转换变更：" + drawings.getChanges());

            drawings.setHealthy(content.substring(content.indexOf(" 治病健康 ") + 6));
            System.out.println("治病健康：" + drawings.getHealthy());

//            File file = new File(PATH + title.replaceAll("<h1 data-v-3b3042fa>", "") + ".txt");  //创建一个地址为PATH加标题的txt文件
//            createFile(file);
//            writeTxtFile(content, file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return drawings;
    }

    //阿拉伯数字转大写数字
    public static String int2chineseNum(int src) {
        final String num[] = {"零", "一", "二", "三", "四", "五", "六", "七", "八", "九"};
        final String unit[] = {"", "十", "百", "千", "万", "十", "百", "千", "亿", "十", "百", "千"};
        String dst = "";
        int count = 0;
        while (src > 0) {
            dst = (num[src % 10] + unit[count]) + dst;
            src = src / 10;
            count++;
        }
        return dst.replaceAll("零[千百十]", "零").replaceAll("零+万", "万")
                .replaceAll("零+亿", "亿").replaceAll("亿万", "亿零")
                .replaceAll("零+", "零").replaceAll("零$", "");
    }
}
