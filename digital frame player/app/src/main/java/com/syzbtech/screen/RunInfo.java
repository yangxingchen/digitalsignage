package com.syzbtech.screen;

import com.syzbtech.screen.entities.Device;
import com.syzbtech.screen.entities.User;
import com.syzbtech.screen.entities.Version;
import com.syzbtech.screen.service.CopyFileService;
import com.syzbtech.screen.service.WebSocketService;

import lombok.Data;

@Data
public class RunInfo {
    public static String deviceCode = "98777665556777C8776d";
    public static Device device;
    public static User user;
    public static WebSocketService webSocketService;
    public static CopyFileService copyFileService;
    public static Version version;
}
