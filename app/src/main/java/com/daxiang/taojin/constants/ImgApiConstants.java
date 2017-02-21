package com.daxiang.taojin.constants;

/**
 * 存放网络图片的API；
 * Created by daxiang on 2017/2/21.
 */
public class ImgApiConstants {

    public static final String[] IMG_APIS = {"http://tnfs.tngou.net/img/ext/161223/12773d01e3304405ea299699ef62fcff.jpg",
            ""};

    public static final String API_IMG_LIST = "http://www.tngou.net/tnfs/api/list";

    public static final String IMG_URI_PREFIX = "http://tnfs.tngou.net/img/";

    //通过www.tngou.net/tnfs/api/list  获取图片列表的json，解析出 img 属性
    // img属性的值前面拼上 http://tnfs.tngou.net/img  获取完整的图片URL；
}
