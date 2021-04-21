package com.daxiang.core.mobile;

import com.android.ddmlib.IDevice;
import com.daxiang.core.Device;
import com.daxiang.core.DeviceHolder;
import com.daxiang.core.mobile.appium.AppiumServer;
import com.daxiang.server.ServerClient;
import com.daxiang.websocket.WebSocketSessionPool;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jiangyitao.
 */
@Slf4j
public abstract class MobileChangeHandler {

    protected void mobileConnected(IDevice iDevice) {
        String mobileId = iDevice.getSerialNumber();

        //维持一个已连接列表
        Device device = DeviceHolder.get(mobileId);
        //yifeng,从这里可以看出，一个appium server端对应于一个设备
        if (device == null) {
            log.info("[{}]首次接入agent", mobileId);

            Mobile mobile = ServerClient.getInstance().getMobileById(mobileId);

            log.info("[{}]启动appium server...", mobileId);
            AppiumServer appiumServer = new AppiumServer();
            appiumServer.start();
            log.info("[{}]启动appium server完成, url: {}", mobileId, appiumServer.getUrl());

            //整个逻辑有两重判断，一个是是不是首次接入到agent ,一个是是不是首次接入到server
            if (mobile == null) {
                try {
                    log.info("[{}]首次接入server，开始初始化...", mobileId);
                    device = initMobile(iDevice, appiumServer);
                } catch (Exception e) {
                    log.info("[{}]停止appium server", mobileId);
                    appiumServer.stop();
                    throw new RuntimeException(String.format("[%s]初始化失败", mobileId), e);
                }
            } else {
                log.info("[{}]已接入过server", mobileId);
                device = newMobile(iDevice, mobile, appiumServer);
            }

            beforePutDeviceToHolder(device);
            //yifeng,保存设备列表
            DeviceHolder.put(mobileId, device);
        } else {
            log.info("[{}]重新接入agent", mobileId);
            reconnectToAgent(device, iDevice);
        }

        //这里把设备更新上传到server端
        device.onlineToServer();
        log.info("[{}]MobileConnected处理完成", mobileId);
    }

    protected void reconnectToAgent(Device device, IDevice iDevice) {
    }

    protected void beforePutDeviceToHolder(Device device) {
    }

    protected void mobileDisconnected(String mobileId) {
        log.info("[{}]断开连接", mobileId);
        Device device = DeviceHolder.get(mobileId);
        if (device == null) {
            return;
        }

        device.offlineToServer();
        WebSocketSessionPool.closeOpeningSession(mobileId);
        log.info("[{}]MobileDisconnected处理完成", mobileId);
    }

    protected abstract MobileDevice initMobile(IDevice iDevice, AppiumServer appiumServer) throws Exception;

    protected abstract MobileDevice newMobile(IDevice iDevice, Mobile mobile, AppiumServer appiumServer);
}
