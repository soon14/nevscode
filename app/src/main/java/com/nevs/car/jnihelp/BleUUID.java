package com.nevs.car.jnihelp;

import java.util.UUID;

/**
 * 相关的UUID
 */
public class BleUUID {
    //对应服务的UUID 车机
    public final static UUID UUID_SERVICE = UUID
            .fromString("0000fee1-0000-1000-8000-00805ffff000");

    //手机发送数据给T   写特性
    public final static UUID UUID_WRITE = UUID
            .fromString("0000fee3-0000-1000-8000-00805ffff000");

    //T发送数据给手机     通知特性
    public final static UUID UUID_READ = UUID
            .fromString("0000fee2-0000-1000-8000-00805ffff000");

    //用来与T通信  更新消息的UUid
    public final static UUID UUID_NOTIFY = UUID
            .fromString("00002902-0000-1000-8000-00805f9b34fb");

}