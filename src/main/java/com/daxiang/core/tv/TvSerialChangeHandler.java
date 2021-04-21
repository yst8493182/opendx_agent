package com.daxiang.core.tv;

import com.android.ddmlib.IDevice;
import com.daxiang.App;
import com.daxiang.core.Device;
import com.daxiang.core.DeviceHolder;
import com.daxiang.core.mobile.Mobile;
import com.daxiang.core.mobile.MobileDevice;
import com.daxiang.server.ServerClient;
import com.daxiang.websocket.WebSocketSessionPool;
import lombok.extern.slf4j.Slf4j;
import java.util.Date;

import javax.validation.constraints.NotNull;

@Slf4j
public class TvSerialChangeHandler {

    protected void mobileConnected(@NotNull String mobileId) {

        //将逻辑提前
        //查询是否有这个设备
        //Device device = DeviceHolder.get(mobileId);
        //我觉得没有必要维护一个曾经连接过的COM列表，只需要维护显示当前已连接的设备列表
        //所以只要设备接入，设备就是唯一的，命名就是agentip-com
        Device device = initMobile(mobileId);
        DeviceHolder.put(mobileId, device);

        //这里把设备更新上传到server端
        ((TvDevice) device).onlineToServer();
        log.info("[{}]SerialConnected done", mobileId);
    }

    protected void mobileDisconnected(String mobileId) {
        //这里要考虑怎么删除设备更新UI
        log.info("[{}] disconnected", mobileId);
        Device device = DeviceHolder.get(mobileId);
        if (device == null) {
            return;
        }
        ((TvDevice)device).offlineToServer();
        log.info("[{}]SerialDisconnected done", mobileId);
    }

    protected TvDevice initMobile(@NotNull String mobileId){
        Mobile mobile = new Mobile();
        //这里后续看能不能独立出设备3
        mobile.setPlatform(1);
        mobile.setCreateTime(new Date());
        mobile.setId(mobileId);
        mobile.setName("TCL_TV");
        mobile.setImgPath("upload/img/tcl.jpg");
        mobile.setEmulator(0);
        mobile.setCpuInfo("unkown");
        mobile.setMemSize("unkown");

        TvDevice tvDevice = new TvDevice(mobile);

        return tvDevice;
    }

    protected TvDevice newMobile(){
        return null;
    }
}

