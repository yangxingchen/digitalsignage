package com.syzbtech.screen.entities;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author jobob
 * @since 2021-03-02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Version implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private String path;

    private Integer apkId;

    private String target;

    private String packageName;

    private String versionName;

    private String versionCode;

    private String content;

    private Date createTime;

    private Date updateTime;

}
