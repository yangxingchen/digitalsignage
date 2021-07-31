package com.syzbtech.screen.entities;

import java.io.Serializable;

import lombok.Data;

@Data
public class Media implements Serializable {

    /**
     * cover :
     * createTime :
     * id : 0
     * md5 :
     * meta :
     * path :
     * size : 0
     * title :
     * type : 0
     * updateTime :
     * userId : 0
     */

    private String cover;
    private String createTime;
    private int id;
    private String md5;
    private String meta;
    private String path;
    private int size;
    private String title;
    private int type;
    private String updateTime;
    private int userId;
}
