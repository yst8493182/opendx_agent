package com.daxiang.schedule;

import com.daxiang.core.Device;
import com.daxiang.core.DeviceHolder;
import com.daxiang.server.ServerClient;
import com.daxiang.model.devicetesttask.DeviceTestTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Created by jiangyitao.
 */
@Slf4j
@Component
public class ScheduledTaskExecutor {

    @Autowired
    private ServerClient serverClient;

    /**
     * 定时检测device的测试任务
     */
    //默认定时10s去检测任务队列并执行
    @Scheduled(fixedRate = 10000)
    public void commitDeviceTestTask() {
        // 在线闲置的device
        List<Device> devices = DeviceHolder.getAll().stream()
                .filter(Device::isIdle)
                .collect(Collectors.toList());
        //note by yifeng,这里会判断集合是否为空，没有设备则不会执行
        if (CollectionUtils.isEmpty(devices)) {
            return;
        }

        //note by yifeng,看起来是通过遍历已保存device中是否有待运行task来执行认任务
        //所以如何把tv serial device 加到里面去
        devices.stream().parallel().forEach(new Consumer<Device>() {
            @Override
            public void accept(Device device) {
                // 获取最早的一个未开始的任务
                DeviceTestTask deviceTestTask = serverClient.getFirstUnStartDeviceTestTask(device.getId());
                if (deviceTestTask != null) {
                    log.info("[{}]提交测试任务, deviceTestTaskId: {}", device.getId(), deviceTestTask.getId());
                    device.getDeviceTestTaskExecutor().commitTestTask(deviceTestTask);
                }
            }
        });
    }
}
