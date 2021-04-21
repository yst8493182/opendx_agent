package com.daxiang.action;

import com.daxiang.core.action.annotation.Action;
import com.daxiang.core.pc.web.BrowserDevice;
import org.openqa.selenium.WebElement;
import org.springframework.util.Assert;

/**
 * Created by jiangyitao.
 * id: 4000 - 4999
 * platform = [3]
 */
public class PCWebAction extends BaseAction {

    public PCWebAction(BrowserDevice browserDevice) {
        super(browserDevice);
    }

    @Action(id = 4000, name = "[web]窗口最大化", platforms = 3)
    public void windowMaximize() {
        device.getDriver().manage().window().maximize();
    }

    @Action(id = 4001, name = "[web]光标移动到元素上", platforms = 3)
    public void mouseOver(WebElement element) {
        Assert.notNull(element, "element不能为空");

        String js = "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initEvent(\"mouseover\",true, false, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);" +
                "arguments[0].dispatchEvent(evObj);";
        device.getDriver().executeScript(js, element);
    }

    @Action(id = 4002, name = "[web]切换frame", platforms = 3)
    public void switchToFrameByIndex(int index) {
        device.getDriver().switchTo().frame(index);
    }

    @Action(id = 4003, name = "[web]切换frame", platforms = 3)
    public void switchToFrameByNameOrId(String nameOrId) {
        device.getDriver().switchTo().frame(nameOrId);
    }

    @Action(id = 4004, name = "[web]切换到parentFrame", platforms = 3)
    public void switchToParentFrame() {
        device.getDriver().switchTo().parentFrame();
    }

    @Action(id = 4005, name = "[web]切换到defaultContent", platforms = 3)
    public void switchToDefaultContent() {
        device.getDriver().switchTo().defaultContent();
    }
}
