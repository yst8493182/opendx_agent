package com.daxiang.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class IperfUtil {

    private static final String IPERF3_PATH = "vendor\\iperf3\\iperf3.exe";
    private static final String IPERF2_PATH = "vendor\\iperf2\\iperf.exe";
    private static final String IPERF3_RESULT_PATH ="out\\iperf3_result\\";
    private static final String IPERF2_RESULT_PATH ="out\\iperf2_result\\";
    private static final String pattern = "MM-dd-HH_mm_ss";

    //考虑需求，是否放开参数
    //增加对输入参数进行判断
    //其实两个函数是可以合在一起写的
    public static String iperf3Client(String ip, String port) throws IOException {
        String savePath = IPERF3_RESULT_PATH + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + ".txt";
        String cmd = IPERF3_PATH + " -c " + ip + " -P 1 -p "+ port + " -i 1 -f m -t 30 --logfile " + savePath;
        Terminal.execute(cmd);
        //解析iperf3总体结果
        return resolvIperf3Result(savePath);
    }

    //如果长时间卡住会怎样，需要有规避措施
    public static String iperf2Client(String ip, String port) throws IOException {
        String savePath = IPERF2_RESULT_PATH + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + ".txt";
        String cmd = IPERF2_PATH + " -c " + ip + " -P 1 -p "+ port + " -i 1 -f m -t 30 >> " + savePath;
        Terminal.execute(cmd);
        return resolvIperf2Result(savePath);
    }

    //作为server端，时间由对端控制，所以要如何判断
    //只管开个线程去执行就行，结束由用例控制
    public static String iperfServer(String method, String port, int iperfTime){
        if(iperfTime <= 0) {
            log.warn("非法输入的iperfTime, 时间需要大于0");
            return null;
        }
        if(!StringUtils.isNumeric(port)){
            log.warn("非法iperf端口输入");
            return null;
        }
        String cmd = "";
        String savePath = "";
        if("iperf2".equals(method)){
            savePath = IPERF2_RESULT_PATH + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + "_server.txt";
            cmd = IPERF2_PATH + " -s -i 1 -p " + port + " -f m >> " + savePath;
        } else if("iperf3".equals(method)) {
            savePath = IPERF3_RESULT_PATH + LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern)) + "_server.txt";
            cmd = IPERF3_PATH + " -s -i 1 -p " + port + " -f m --logfile " + savePath;
        } else {
            log.warn("非法输入的iperf方式，有效值为 iperf2/iperf3");
            return null;
        }
        String finalCmd = cmd;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Terminal.execute(finalCmd);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();

        try{
            //稍微再延迟点时间防止不可预测异常
            Thread.sleep(iperfTime + 3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(NetUtil.killProcessId(port)){
            if ("iperf2".equals(method)) {
                return resolvIperf2Result(savePath);
            } else {
                return resolvIperf3Result(savePath);
            }
        }
        log.warn("iperf server 结果读取异常失败");
        return null;
    }

    private static String resolvIperf3Result(String filePath){
        if(StringUtils.isEmpty(filePath)) {
            return null;
        }
        String fileResult = FileUtil.readFile(filePath);
        if(StringUtils.isEmpty(fileResult)) {
            return null;
        }
        String[] arr = fileResult.split("\\[");
        for(String s : arr) {
            if (s.contains("receiver")) {
                return s.substring(s.indexOf("Bytes")+5, s.indexOf("Mbits/sec")).trim();
            }
        }
        return null;
    }

    private static String resolvIperf2Result(String filePath){
        if(StringUtils.isEmpty(filePath)) {
            return null;
        }
        String fileResult = FileUtil.readFile(filePath);
        if(StringUtils.isEmpty(fileResult)) {
            return null;
        }
        String[] arr = fileResult.split("\\[");
        String line = arr[arr.length - 1];
        if(StringUtils.isEmpty(line)) {
            return null;
        }
        return line.substring(line.indexOf("Bytes")+5, line.indexOf("Mbits/sec")).trim();
    }

}
