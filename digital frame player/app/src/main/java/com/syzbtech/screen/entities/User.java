package com.syzbtech.screen.entities;

import java.util.Date;

import lombok.Data;

@Data

public class User {
    private Long id;
    private String username;
    private String mobile;
    private String account;
    private Long avatar;
    private String avatarPath;
    private Integer status;
    private Date createTime;
    private Date updateTime;
}
