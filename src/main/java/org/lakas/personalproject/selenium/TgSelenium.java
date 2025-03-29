package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TgSelenium {

    private final WebDriver driver;

    @Autowired
    public TgSelenium(WebDriver driver) {
        this.driver = driver;
    }

    public List<String> readMessages() {
        return null;
    }

    public void writeMessage(String message) {
        WebElement enterLine = getEnterLine();
        enterLine.click();
        enterLine.sendKeys(message, Keys.ENTER);
    }

    private WebElement getEnterLine() {
        By findParameter = By.xpath("//div[contains(@class, 'input-message-input') and @data-peer-id]");
        return driver.findElement(findParameter);
    }
}
