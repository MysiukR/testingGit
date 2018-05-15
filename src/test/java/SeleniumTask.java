import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.List;

public class SeleniumTask {
    private WebDriver webDriver;
    private static final Logger logger = Logger.getLogger(SeleniumTask.class.getName());

    @Test
    public void testsGit() {
        PropertyConfigurator.configure("src/log4j.properties");
        System.setProperty("webdriver.chrome.driver", "src/main/resources/chromedriver.exe");
        webDriver = new ChromeDriver();
        webDriver.get("https://github.com");
        testLogin();
        testSearch("selenium java");
        changeItem();
        testSearch("java");
    }

    private void testLogin() {
        webDriver.findElement(By.linkText("Sign in")).click();
        webDriver.findElement(By.id("login_field")).sendKeys("MysiukR");
        webDriver.findElement(By.id("password")).sendKeys("****");
        webDriver.findElement(By.name("commit")).click();
    }

    public void testSearch(String searchText) {
        webDriver.get("https://github.com");
        webDriver.findElement(By.name("q")).sendKeys(searchText);
        webDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);
        getTotalResult();
        outputTitle();
    }

    public void changeItem() {
        checkTags();
        webDriver.findElement(By.cssSelector("button.btn.btn-sm.select-menu-button.js-menu-target")).click();
        webDriver.findElement(By.cssSelector(".select-menu-list")).findElement(By.linkText("Fewest stars")).click();
        outputTitle();

    }

    public void outputTitle() {
        logger.info(String.format("There are %d repositories on the page.\n", getTotalCount()));
        logger.info("Name of repositories: ");
        List<WebElement> elements = webDriver.findElements(By.className("v-align-middle"));
        elements.stream().forEach(element -> logger.info("\t"+element.getText()));
    }

    private void getTotalResult() {
        String totalSum = webDriver.findElement(By.cssSelector("div.d-flex.flex-justify-between.border-bottom.pb-3 > h3")).getText();
        logger.info(String.format("Total count are %s", totalSum));
    }

    private int getTotalCount() {
        int actualTitles = webDriver.findElements(By.className("v-align-middle")).size();
        return actualTitles;
    }

    public void checkTags() {
        List<WebElement> elements = webDriver.findElements(By.className("v-align-middle"));
        elements.stream()
                .filter(user -> user.getText().contains("selenium") || user.getText().contains("Selenium"))
                .forEach(element -> logger.info("Tag 'selenium' contain the title: " + element.getText()));
        int countTag = ((int) elements.stream()
                .filter(user -> user.getText().contains("selenium") || user.getText().contains("Selenium")).count());
        if (countTag != getTotalCount()) {
            logger.error(String.format("Not all tags contain selenium. %d from %d", countTag, getTotalCount()));
        } else {
            logger.info("All tags contain selenium!");
        }
    }

    @AfterClass
    public void closeBrowser() {
        webDriver.quit();
    }
}
