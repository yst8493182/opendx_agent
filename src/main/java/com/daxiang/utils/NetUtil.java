package com.daxiang.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;

import java.util.regex.Pattern;

/**
 * Created by jiangyitao.
 */
@Slf4j
public class NetUtil {

    public static final String LOCALHOST = "127.0.0.1";

    public static final String ANYHOST = "0.0.0.0";

    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    /**
     *
     * 检测本地端口是否可用
     *
     * @param port 端口号
     * @return
     */
    public static boolean isPortAvailable(int port) {
        try {
            bindPort("127.0.0.1", port);
            bindPort("0.0.0.0", port);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    //kill 占用某个端口的进程
    public static Boolean killProcessId(String port){
        try{
            if (isPortAvailable(Integer.parseInt(port))) {
                return true;
            }
            String pid = getProcessId(port);
            if(!StringUtils.isEmpty(pid)){
                Terminal.execute("taskkill -PID " + pid + " -F");
            }
            //休眠保证端口kill成功
            Thread.sleep(1000);
            if (isPortAvailable(Integer.parseInt(port))) {
                return true;
            }
        } catch (NumberFormatException | IOException | InterruptedException e){
            log.warn("the port is invalid, is not a number");
            return false;
        }
        log.warn("kill pid by port 异常失败");
        return false;
    }

    //获取某个占用端口的进程号
    private static String getProcessId(String port) {
        try {
            if (isPortAvailable(Integer.parseInt(port))) {
                return null;
            }
            String res = Terminal.execute("netstat -ano | findstr " + port, false);
            if(!StringUtils.isEmpty(res)){
                for(String s : res.split("\\s+")) {
                    if(StringUtils.isNumeric(s)){
                        return s;
                    }
                }
            }
        }catch(NumberFormatException | IOException e){
            log.warn("the port is invalid, is not a number");
        }
        return null;
    }

    private static void bindPort(String ip, int port) throws IOException {
        try (Socket socket = new Socket()) {
            socket.bind(new InetSocketAddress(ip, port));
        }
    }

    public static int getAvailablePort(int startPort, int endPort, int port) {
        if (startPort >= endPort) {
            throw new IllegalArgumentException();
        }

        while (true) {
            if (port > endPort || port < startPort) {
                port = startPort;
            }
            if (isPortAvailable(port)) {
                return port;
            } else {
                port++;
            }
        }
    }

    public static String getLocalWlanIpAddress() {
        HashMap<String,String> map = new HashMap<>();
        map = getLocalIpAddress();
        if(map.containsKey("wlan")){
            return map.get("wlan");
        }
        log.warn("the wlan is not connected, get the ip is null");
        return null;
    }

    public static String getLocalEthIpAddress() {
        HashMap<String,String> map = new HashMap<>();
        map = getLocalIpAddress();
        if(map.containsKey("eth")){
            return map.get("eth");
        }
        log.warn("the eth is not connected, get the ip is null");
        return null;
    }

    //add by yifeng,获取系统ip,返回一个map，包含wlan 和 eth
    private static HashMap<String, String> getLocalIpAddress() {
        HashMap<String,String> map = new HashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface network = interfaces.nextElement();
                    Enumeration<InetAddress> addresses = network.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = addresses.nextElement();
                        if (isValidAddress(address)) {
                            //不考虑有多网卡wlan连接的情况
                            map.put(network.getName().replaceAll("\\d", ""),
                                    address.getHostAddress());
                        }
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Failed to retriving ip address, " + e.getMessage());
        }
        return map;
    }

    //add by yifeng, 改变window pc连接的热点，只能用于已保存热点
    public static Boolean changeWinWifi(String ssid) {
        try {
            String res = Terminal.execute("netsh wlan connect " + ssid);
            //这个判断条件如果遇到非中文系统就会出问题
            if(!StringUtils.isEmpty(res) && res.contains("已成功")) {
                return true;
            }
            String serachRes = Terminal.execute("netsh wlan show interfaces");
            //这个判断条件如歌遇到有SSID相同子段且带空格，也会失效，概率较低。
            if(!StringUtils.isEmpty(serachRes) && serachRes.contains(" "+ ssid + " ")){
                return true;
            }
        } catch (IOException e) {
            log.warn("execute the cmd to changeWinWifi Fail " + e.getMessage());
            return false;
        }
        log.warn("fail to change the windows connected wifi, maybe the wifi is not saved");
        return false;
    }

    //只拿ipv4地址
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress())
            return false;

        String name = address.getHostAddress();
        return (name != null
                && ! ANYHOST.equals(name)
                && ! LOCALHOST.equals(name)
                && IP_PATTERN.matcher(name).matches());
    }
}
