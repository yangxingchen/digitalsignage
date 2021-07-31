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
 * @since 2021-02-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PlayBkMusic implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long sort;

    private Long userId;

    private Long mediaId;

    private Long deviceId;

    private Integer mediaType;

    private Date createTime;

    private Media media;
}
