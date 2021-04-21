package com.daxiang.utils;

import gnu.io.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

@Slf4j
public class SerialTool {

    private static final String testResultPath = "/data/test_result/";

    //获得系统可用的端口
    @SuppressWarnings("unchecked")
    public static List<String> getSerialPortList() {
        List<String> systemPorts = new ArrayList<>();
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while (portList.hasMoreElements()) {
            //获得端口的名字
            String portName = portList.nextElement().getName();
            systemPorts.add(portName);
        }
        return systemPorts;
    }

    //检测输入端口是否有效
    public static Boolean checkSerialPortExist(String port) {
        if (port == null) {
            System.out.println("输入端口为null");
            return false;
        }
        List<String> AllSerialPort = SerialTool.getSerialPortList();
        if (AllSerialPort.contains(port)) {
            return true;
        }
        System.out.println("端口 " + port + " 不存在");
        return false;
    }

    // 打开串口系列函数
    public static SerialPort openSerialPort(String serialPortName) {
        if(!checkSerialPortExist(serialPortName))
            return null;
        SerialParameter parameter = new SerialParameter(serialPortName);
        return openSerialPort(parameter, 2000);
    }

    public static SerialPort openSerialPort(SerialParameter parameter, int timeout) {
        try {
            //通过端口名称得到端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(parameter.getSerialPortName());
            //打开端口，（自定义名字，打开超时时间）
            CommPort commPort = portIdentifier.open(parameter.getSerialPortName(), timeout);
            //判断是不是串口
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                //设置串口参数（波特率，数据位8，停止位1，校验位无）
                serialPort.setSerialPortParams(parameter.getBaudRate(), parameter.getDataBits(), parameter.getStopBits(), parameter.getParity());
                System.out.println("开启串口成功，串口名称：" + parameter.getSerialPortName());
                return serialPort;
            } else {
                //其他类型的端口
                throw new NoSuchPortException();
            }
        } catch (UnsupportedCommOperationException | NoSuchPortException | PortInUseException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 关闭串口
    public static void closeSerialPort(SerialPort serialPort) {
        if (serialPort != null) {
            serialPort.close();
            System.out.println("关闭了串口：" + serialPort.getName());
        }
    }

    // 向串口发送数据
    public static void sendData(SerialPort serialPort, byte[] data) {
        OutputStream os = null;
        try {
            //获得串口的输出流
            os = serialPort.getOutputStream();
            os.write(data);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 从串口读取数据
    public static byte[] readData(SerialPort serialPort) {
        InputStream is = null;
        byte[] bytes = null;
        try {
            //获得串口的输入流
            is = serialPort.getInputStream();
            //获得数据长度
            int bufflenth = is.available();
            while (bufflenth != 0) {
                //初始化byte数组
                bytes = new byte[bufflenth];
                is.read(bytes);
                bufflenth = is.available();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    //因为数据读取需要一定时间延迟读取数据
    //小数据量读取可以使用这个函数，4000字符以内
    public static String readData(SerialPort port, int timeout) {
        try{
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        byte[] bytes = SerialTool.readData(port);
        if (bytes == null)
        {
            System.out.println("yifeng,output is null");
            return null;
        } else {
            String res = new String(bytes);
            //去掉输入命令后的回显和BeyondTv
            //return DeleteFeedback(res);
            if(StringUtils.isEmpty(res)) {
                return null;
            }
            return SerialOutGetJson(res);
            //return res;
        }
    }

    // 给串口设置监听
    public static void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) throws TooManyListenersException {
        //给串口添加事件监听
        serialPort.addEventListener(listener);
        //串口有数据监听
        serialPort.notifyOnDataAvailable(true);
        //中断事件监听
        serialPort.notifyOnBreakInterrupt(true);
    }

    public static String execSerialCmd(String port, String cmd, int timeout) {
        if(!checkSerialPortExist(port)) {
            System.out.println("port is error");
            return null;
        }
        if(cmd == null || cmd.length() <= 0) {
            System.out.println("cmd is null");
            return null;
        }
        final SerialPort serialPort = openSerialPort(port);
        assert serialPort != null;
        sendData(serialPort, cmd.getBytes());
        String res = readData(serialPort, timeout);
        closeSerialPort(serialPort);
        return res;
    }

    public static void execSerialCmdNoBack(String port, String cmd) {
        if(!checkSerialPortExist(port)) {
            System.out.println("port is error");
            return;
        }
        if(cmd == null || cmd.length() <= 0) {
            System.out.println("cmd is null");
            return;
        }
        final SerialPort serialPort = openSerialPort(port);
        assert serialPort != null;
        sendData(serialPort, cmd.getBytes());
        closeSerialPort(serialPort);
        //主动延迟，防止串口没关闭又开始调用
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //返回后台执行的pid,
    //判断字符串，要想判断是否空对象，在判断是否是空字符串
    public static String execCmdBackground(String port, String cmd, String testName) {
        if(cmd != null && testName != null && cmd.length() > 0 && testName.length() > 0) {
            String execCmd = cmd + " > " + testResultPath + testName + "&\n";
            String result = execSerialCmd(port, execCmd, 100);
            if (result == null) {
                return null;
            }
            //处理返回值提取进程名
            String pid = result.substring(result.indexOf(" ")+1, result.indexOf("\n")-1);
            return pid;
        }
        return null;
    }

    //检查输入进程是否存在
    public static Boolean checkProcessExist(String port, String pid) {

        //检查输入的pid是否合法
        try {
            Integer.parseInt(pid);
        }catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("错误的进程id: " + pid);
            return false;
        }
        String execCmd = "ps -p " + pid + "\n";
        String result = execSerialCmd(port, execCmd, 100);
        if (result == null) {
            System.out.println("exec Serial Cmd Error");
            return false;
        }
        return result.contains(pid);
    }

    //查看文件包含的字符数
    //计算读取文件所需时间,小于100ms,默认100ms
    public static int checkLengthOfFile(String port, String testName) {
        if(testName == null || testName.length() <= 0)
            return -1;
        String execCmd = "wc -m " + testResultPath + testName + "\n";
        //这里有个隐患，因为如果文件很大会等久点
        String res = execSerialCmd(port, execCmd, 1000);
        assert res != null;
        res = res.substring(0, res.indexOf(" "));
        try {
            double num = Integer.parseInt(res);
            int time = (int) Math.ceil(num/10);
            return Math.max(time, 100);
        }catch (NumberFormatException e) {
            e.printStackTrace();
            System.out.println("错误的字符数: " + res);
            return -1;
        }

    }

    //读取test_result里的结果文件
    //这里考虑结果是在tv解析还是在pc解析
    public static String readFileData(String port, String testName){
        if(testName == null || testName.length() <= 0 )
            return null;
        int timeout = checkLengthOfFile(port, testName);
        if (timeout <= 0) {
            System.out.println("获取文件读取时间错误");
            return null;
        }
        String execCmd = "cat " + testResultPath + testName + "\n";
        String res = ListenAndOutput(port, execCmd, timeout);
        return res;
    }

    //实时监听串口数据
    //适用于读取大数据
    //再处理一下回显问题就可以了
    public static String ListenAndOutput(String port, String cmd, int timeout) {
        if(timeout <= 0)
            return null;
        ArrayUtils array = new ArrayUtils();

        try {
            final SerialPort serialPort = openSerialPort(port);
            assert serialPort != null;
            sendData(serialPort,cmd.getBytes());
            setListenerToSerialPort(serialPort, serialPortEvent -> {
                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                    byte[] bytes = readData(serialPort);
                    if (bytes == null) {
                        System.out.println("yifeng,output is null");
                    } else {
                        String res = new String(bytes);
                        array.add(res);
                    }
                }
            });
            Thread.sleep(timeout);
            closeSerialPort(serialPort);
        }catch (TooManyListenersException | InterruptedException e) {
            e.printStackTrace();
        }
        return DeleteFeedback(array.toString());
    }

    //去掉本地回显
    public static String DeleteFeedback(@NotNull String input) {
        return input.substring(input.indexOf("\n")+1);
    }

    //获取串口的json信息
    //要保证没有空格，
    public static String SerialOutGetJson(@NotNull String input) {
        //String[] arr = input.split("\\s+");
//        for(String s : arr) {
//            if (s.contains("{") && s.charAt(0) == '{' && s.charAt(s.length()-1) == '}') {
//                return s;
//            }
//        }
        log.info(input);
        try{
            return input.substring(input.indexOf("{"),input.indexOf("}")+1);
        } catch (Exception e) {
            log.warn("there have error in split json, return null");
            return null;
        }
    }
    //初始化设备的串口，如设置打印等级等
    //1、检测并尝试打开su/sitadebug
    //2、检测并尝试创建结果保存的文件夹
    //3、设置串口打印，log打印等
    public void initDeviceSerial(){}
}