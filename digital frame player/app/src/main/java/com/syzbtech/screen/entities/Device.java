package com.syzbtech.screen.entities;

import java.util.Date;

import lombok.Data;

/**
 * @author coowalt@sina.com
 * @Date 2021/1/24 18:19
 */
@Data

public class Device {
    private Long id;

    private String title;

    private String code;

    private Integer status;

    private Integer online;

    private Date createTime;

    private Date updateTime;
    //当前绑定的用户

    private Long userId;

    private User user;
}
