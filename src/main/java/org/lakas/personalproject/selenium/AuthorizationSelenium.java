package org.lakas.personalproject.selenium;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthorizationSelenium {
    private final WebDriver driver;

    @Autowired
    public AuthorizationSelenium(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForAuthorization() {
        WebElement enterLine = null;

        while (enterLine == null) {
            enterLine = tryGetEnterLine();
        }
    }

    private WebElement tryGetEnterLine() {
        try {
            Thread.sleep(100);
            By findParameter = By.xpath("//div[contains(@class, 'input-message-input') and @data-peer-id]");
            return driver.findElement(findParameter);
        } catch (NoSuchElementException ignored) {
            return null;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
