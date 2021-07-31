package com.syzbtech.screen.http;

public final class Api {
    //public static final String serverIp = "192.168.0.104";
    //public static final String port = "8080";

    //public static final String serverIp = "creative-screen.syzbtech.cn";
    //public static final String port = "80";

    public static final String serverIp = "39.107.85.251";
    public static final String port = "28080";
    public static final String host = "http://"+serverIp+":"+port+"/";
    public static final String ws = "ws://"+serverIp+":"+port+"/crtscr/";

    //public static final long CHUNK_SIZE = 5242880L;//5M
    public static final long CHUNK_SIZE = 102400L;// 100k，和服务器保持一致，请不要随意改动。否则文件上传会乱

    public static final String LIST_DEVICE_MEDIA = "tv/api/device/media/list";
    public static final String GET_DEVICE= "tv/api/device/get";
    public static final String GET_UPGRADE_INFO = "version/info";
}
