package com.daxiang.core.tv;

import com.daxiang.core.Device;
import com.daxiang.core.mobile.Mobile;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Date;

public class TvDevice extends Device {

    protected Mobile tvSerial;

    public TvDevice(Mobile mobile){
        super();
        this.tvSerial = mobile;
    }

    public Mobile getMobile() {
        return tvSerial;
    }

    @Override
    public RemoteWebDriver getDriver() {
        return null;
    }

    @Override
    protected Capabilities newCaps(Capabilities capsToMerge){
        return null;
    }

    @Override
    public String getId() {
        return tvSerial.getId();
    }

    @Override
    public Integer getStatus() {
        return tvSerial.getStatus();
    }

    @Override
    public void onlineToServer() {
        tvSerial.setAgentIp(agentIp);
        tvSerial.setAgentPort(agentPort);
        tvSerial.setLastOnlineTime(new Date());
        idleToServer();
    }

    @Override
    public void usingToServer(String username) {
        tvSerial.setUsername(username);
        tvSerial.setStatus(Device.USING_STATUS);
        serverClient.saveMobile(tvSerial);
    }

    @Override
    public void idleToServer() {
        tvSerial.setStatus(Device.IDLE_STATUS);
        serverClient.saveMobile(tvSerial);
    }

    @Override
    public void offlineToServer() {
        tvSerial.setStatus(Device.OFFLINE_STATUS);
        serverClient.deleteMobile(tvSerial.getId());
    }


}
