package com.daxiang.schedule;

import com.daxiang.App;
import com.daxiang.core.Device;
import com.daxiang.core.DeviceHolder;
import com.daxiang.core.tv.TvSerialChangeHandler;
import com.daxiang.utils.SerialTool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ScheduledCheckSerial extends TvSerialChangeHandler {

    public static List<String> saveSerialPort = new ArrayList<>();
    /**
     * 定时检测device的测试任务
     */
    //启动web端程序12s后开始去检测串口，每2s检测一次
    //fixedRate是异步的
    //fixedDelay是同步的
    //initialDelay是第一次被调用前的延迟，默认是初始化就马上会调用，这样的话太快了
    //为了统一识别，使用agent-ip + port 的方式
    @Scheduled(initialDelay = 12000 , fixedDelay = 2000)
    public void checkSerial() {
        String agentIp = App.getProperty("ip");
        List<String> AllSerialPort = SerialTool.getSerialPortList();
        for(String s : AllSerialPort) {
            if (!saveSerialPort.contains(s)) {
                saveSerialPort.add(s);
                log.info("增加串口设备:" + agentIp + "-" + s);
                Device device = DeviceHolder.get(agentIp + "-" + s);
                if (device == null) {
                    mobileConnected(agentIp + "-" + s);
                }
            }
        }
        for(int i=0 ; i < saveSerialPort.size(); i++) {
            if (!AllSerialPort.contains(saveSerialPort.get(i))) {
              Device device = DeviceHolder.get(agentIp + "-" + saveSerialPort.get(i));
              if (device != null) {
                  mobileDisconnected(agentIp + "-" + saveSerialPort.get(i));
              }
              saveSerialPort.remove(i);
              i--;
            }
        }
    }
}
