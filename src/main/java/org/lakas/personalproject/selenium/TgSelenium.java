package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TgSelenium {

    private final WebDriver driver;

    private WebElement enterLine;

    @Autowired
    public TgSelenium(WebDriver driver) {
        this.driver = driver;
    }

    public void readMessages() {
        waitForAuthorization();
        log.info("Found element");
        enterLine.click();
        enterLine.sendKeys("TEST", Keys.ENTER);
    }

    private void waitForAuthorization() {
        By findParameter = By.className("input-message-input");
        By findParameter1 = By.xpath("//div[contains(@class, 'input-message-input') and @data-peer-id]");


        log.info("Waiting for authorization...");
        while (enterLine == null) {
            try {
                enterLine = driver.findElement(findParameter1);
            } catch (NoSuchElementException e) {}
        }
    }
}
