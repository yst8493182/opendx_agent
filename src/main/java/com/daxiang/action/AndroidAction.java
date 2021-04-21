package com.daxiang.action;

import com.daxiang.core.action.annotation.Action;
import com.daxiang.core.action.annotation.Param;
import com.daxiang.core.mobile.android.AndroidDevice;
import com.daxiang.core.mobile.android.AndroidUtil;
import com.daxiang.core.mobile.android.IDeviceExecuteShellCommandException;
import com.daxiang.core.tv.TvDevice;
import com.daxiang.utils.CRCUtils;
import com.daxiang.utils.SerialTool;
import com.daxiang.utils.Terminal;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;

import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * Created by jiangyitao.
 * id 2000 - 2999
 * platforms = [1]
 */
@Slf4j
public class AndroidAction extends MobileAction {

    private AndroidDevice androidDevice;
    private TvDevice tvDevice;
    //node by yifeng
    //由于在前端界面通过id固定了某些action的input组件,为了兼容，把这些用例id固定化，不更改
    public static final int EXECUTE_ADB_SHELL_ID = 2002;
    public static final int EXECUTE_SERIAL_SHELL_ID = 2009;
    public static final int EXECUTE_SERIAL_SHELL_NOBACK_ID = 2010;

    public AndroidAction(AndroidDevice androidDevice) {
        super(androidDevice);
        this.androidDevice = androidDevice;
    }

    //and by yifeng,串口设备构造函数
    public AndroidAction(TvDevice tvDevice) {
        super(tvDevice);
        this.tvDevice = tvDevice;
    }

    @Action(id = 2000, name = "[android]清除apk数据", platforms = 1)
    public void clearApkData(@Param(description = "包名") String packageName) throws IDeviceExecuteShellCommandException {
        AndroidUtil.clearApkData(androidDevice.getIDevice(), packageName);
    }

    @Action(id = 2001, name = "[android]启动/重启apk", platforms = 1)
    public void restartApk(@Param(description = "包名") String packageName, @Param(description = "启动Activity名") String launchActivity) throws IDeviceExecuteShellCommandException {
        AndroidUtil.restartApk(androidDevice.getIDevice(), packageName, launchActivity);
    }

    @Action(id = EXECUTE_ADB_SHELL_ID, name = "[android]执行adb shell命令", returnValueDesc = "命令返回信息", platforms = 1)
    public String executeAdbShellCommand(@Param(description = "命令") String cmd) throws IDeviceExecuteShellCommandException {
            return AndroidUtil.executeShellCommand(androidDevice.getIDevice(), cmd);
    }

    //add by yifeng, 使用java方式执行串口命令
    @Action(id = EXECUTE_SERIAL_SHELL_ID, name = "[serial]执行串口命令(有返回值)", returnValueDesc = "返回结果json格式字符串")
    public String execSerialCmdMode(@NotNull @Param(description = "输入串口命令") String cmd, @Param(description = "结果读取延迟时间/ms") int delayTime) {
        //拿设备的COM口
        String port = tvDevice.getId().substring(tvDevice.getId().indexOf("-") + 1);
        //起新的命令行保证不被之前影响
        cmd = "\n" + cmd + "\n";
        return SerialTool.execSerialCmd(port, cmd , delayTime);
    }

    //搞个无返回值接口
    @Action(id = EXECUTE_SERIAL_SHELL_NOBACK_ID, name = "[serial]执行串口命令(无返回值)")
    public void execSerialCmdModeNoReturn(@NotNull @Param(description = "输入串口命令") String cmd) {
        String port = tvDevice.getId().substring(tvDevice.getId().indexOf("-") + 1);
        cmd = "\n" + cmd + "\n";
        SerialTool.execSerialCmdNoBack(port, cmd);
    }

    //串口结果正确性校验
    //需要限定返回值的格式，目前限定为json格式
    //必须要有字段{“Result”:String,"Command":String,"Auth":String};
    //可以携带额外字段，但字符总长度不能超过3000字符
    //校验的时候一同判断命令的结果
    @Action(id = 2011, name = "[serial]串口结果校验", returnValueDesc = "串口读取是否有效")
    public Boolean checkSerialResult(@Param(description = "串口输出信息") String result) {
        //校验的格式确定
        return CRCUtils.checkResult(result);

    }

    //获取json某个key 的值
    @Action(id = 2012, name = "[serial]获取json key的value", returnValueDesc = "value")
    public String getValueFromJson(@NotNull @Param(description = "json格式的String对象") String input,  @NotNull @Param(description = "Key") String key) {
        //json格式检查

        try {
            JSONObject jsonObject = new JSONObject(input);
            return String.valueOf(jsonObject.get(key));
        } catch ( JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}
