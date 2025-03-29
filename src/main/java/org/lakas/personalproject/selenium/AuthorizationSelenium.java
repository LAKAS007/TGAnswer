package org.lakas.personalproject.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AuthorizationSelenium {

    private final WebDriver driver;

    private final String TG_URL;

    private final TgSelenium tgSelenium;

    @Autowired
    public AuthorizationSelenium(WebDriver driver, @Value("${tg.url}") String tgUrl, TgSelenium tgSelenium) {
        this.driver = driver;
        TG_URL = tgUrl;
        this.tgSelenium = tgSelenium;
    }

    public void startAuthorization(String login) {
        driver.get(TG_URL + login);
        tgSelenium.readMessages();
    }
}
