package com.daxiang.core.mobile.android;

import com.daxiang.action.AndroidAction;
import com.daxiang.core.testng.TestNGCodeConverter;
import com.daxiang.core.tv.TvDevice;
import io.appium.java_client.android.AndroidDriver;

import java.util.Set;

public class TvTestNGCodeConverter extends TestNGCodeConverter {
    @Override
    protected Class getDriverClass() {
        return AndroidDriver.class;
    }

    @Override
    protected Class getActionClass() {
        return AndroidAction.class;
    }

    @Override
    protected Class getDeviceClass() {
        return TvDevice.class;
    }

    @Override
    protected void addJavaImports(Set<String> javaImports) {
        javaImports.add("import com.daxiang.core.tv.TvDevice");
        javaImports.add("import com.daxiang.action.AndroidAction");
        javaImports.add("import io.appium.java_client.android.AndroidDriver");
        javaImports.add("import com.daxiang.core.pc.web.BrowserDevice");
        javaImports.add("import org.openqa.selenium.remote.RemoteWebDriver");
        javaImports.add("import com.daxiang.action.PCWebAction");
        javaImports.add("import com.daxiang.core.mobile.android.AndroidDevice");
    }

}
