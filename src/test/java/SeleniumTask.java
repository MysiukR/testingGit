import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

public class SeleniumTask {
    private static final String PATH_CHROME_DRIVER = "src/main/resources/chromedriver.exe";
    private static final String DRIVER = "webdriver.chrome.driver";
    private static final String PATH_LOGGER_PROPERTIES="src/log4j.properties";
    private static final String LOGIN_GIT = "MysiukR";
    private static final String PASSWORD_GIT = "****";
    private static final String TEST_SITE = "https://github.com/";
    private WebDriver webDriver;
    private static final Logger logger = Logger.getLogger(SeleniumTask.class.getName());

    @BeforeClass
    public void initialize() {
        PropertyConfigurator.configure(PATH_LOGGER_PROPERTIES);
        System.setProperty(DRIVER, PATH_CHROME_DRIVER);
        webDriver = new ChromeDriver();
        webDriver.get("https://github.com");
        Assert.assertEquals(webDriver.getCurrentUrl(), TEST_SITE, "Unsuccessful authorization");
    }


    @Test
    public void testRegistrationForm() {
        webDriver.findElement(By.linkText("Sign in")).click();
        webDriver.findElement(By.id("login_field")).sendKeys(LOGIN_GIT);
        webDriver.findElement(By.id("password")).sendKeys(PASSWORD_GIT);
        webDriver.findElement(By.name("commit")).click();
        webDriver.get(TEST_SITE);
    }

    @Test(dependsOnMethods = {"testRegistrationForm"})
    public void testSearchSelenium() {
        checkSearch("selenium java");
        getTotalResult();
        outputTitle();
    }


    @Test(dependsOnMethods = {"testSearchSelenium"})
    public void testDropDownItem() {
        checkTags();
        webDriver.findElement(By.cssSelector("button.btn.btn-sm.select-menu-button.js-menu-target")).click();
        webDriver.findElement(By.xpath("//div[contains(@class,'select-menu-list')]")).findElement(By.linkText("Fewest stars")).click();
        outputTitle();
    }

    @Test(dependsOnMethods = {"testDropDownItem"})
    public void testSearchJava() {
        checkSearch("java");
        getTotalResult();
        outputTitle();
    }

    @AfterClass
    public void closeBrowser() {
        webDriver.quit();
    }

    private void checkSearch(String searchText) {
        webDriver.get(TEST_SITE);
        webDriver.findElement(By.name("q")).sendKeys(searchText);
        webDriver.findElement(By.name("q")).sendKeys(Keys.ENTER);
    }

    private void outputTitle() {
        logger.info(String.format("There are %d repositories on the page.\n", getTotalCount()));
        logger.info("Name of repositories: ");
        List<WebElement> elements = webDriver.findElements(By.className("v-align-middle"));
        elements.stream().forEach(element -> logger.info("\t" + element.getText()));
    }

    private void getTotalResult() {
        String totalSum = webDriver.findElement(By.cssSelector("div.d-flex.flex-justify-between.border-bottom.pb-3 > h3")).getText();
        logger.info(String.format("Total count are %s", totalSum));
    }

    private int getTotalCount() {
        int actualTitles = webDriver.findElements(By.className("v-align-middle")).size();
        return actualTitles;
    }

    private void checkTags() {
        List<WebElement> elements = webDriver.findElements(By.cssSelector("a.topic-tag.topic-tag-link.f6.my-1"));
        elements.stream()
                .filter(user -> user.getText().contentEquals("selenium"))
                .forEach(element -> logger.info("Tag 'selenium' contain: " + element.getText()));
        int countTag = ((int) elements.stream()
                .filter(user -> user.getText().contentEquals("selenium")).count());
        if (countTag != getTotalCount()) {
            logger.error(String.format("Not all tags contain selenium. %d from %d", countTag, getTotalCount()));
        } else {
            logger.info("All tags contain selenium!");
        }
    }
}
