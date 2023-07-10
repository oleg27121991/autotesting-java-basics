import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

public class MainTestScenario {
    private WebDriver driver;
    private WebDriverWait wait;

    // Локаторы
    private By logoLocator = By.className("site-logo"); // Логотип сайта
    private By searchInputLocator = By.className("search-field"); // Поле "Поиск товара"
    private By searchResultsLocator = By.id("title_bread_wrap"); // Результаты поиска
    private By myAccountLinkLocator = By.className("account"); // Ссылка на страницу Регистрации и авторизации
    private By saleSectionLocator = By.xpath("(//div[@class='slick-track'])[2]"); // Секция "Распродажи"
    private By firstCategoryElementLocator = By.xpath("(//div[@class='promo-widget-wrap'])[1]"); // Элемент первой категории
    private By titleFirstCategoryElementLocator = By.xpath("(//div[contains(@class, 'caption')])[1]//h4"); // Заголовок первой категории
    private By secondCategoryElementLocator = By.xpath("(//div[@class='promo-widget-wrap'])[2]"); // Элемент второй категории
    private By titleSecondCategoryElementLocator = By.xpath("(//div[contains(@class, 'caption')])[2]//h4"); // Заголовок второй категории
    private By newSaleSectionLocator = By.xpath("(//div[@class='slick-track'])[3]"); // Секция "Новые поступления"
    private By activeSlideLocator = By.cssSelector(".slick-slide.slick-active"); // Активные слайды
    private By titleElementLocator = By.tagName("h3"); // Заголовок элемента

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 8);
        driver.navigate().to("http://intershop5.skillbox.ru/");
    }

    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("desktop\\screenshot"));
        driver.quit();
    }

    @Test
    public void verifyMainPageTitleTest() {
        driver.navigate().to("http://intershop5.skillbox.ru/");
        var actualMainTitle = driver.getTitle();
        var expectedMainTitle = "Skillbox — Интернет магазин";
        Assert.assertEquals("Неверный текст в заголовке страницы", actualMainTitle, expectedMainTitle);
    }

    @Test
    public void verifyLogoDisplayedTest() {
        driver.navigate().refresh();
        var logo = driver.findElement(logoLocator);
        Assert.assertTrue("Логотип не отображается", logo.isDisplayed());
    }

    @Test
    public void verifySearchFunctionality() {
        driver.navigate().refresh();
        var searchTerm = "Телефон";
        driver.findElement(searchInputLocator).sendKeys(searchTerm);
        driver.findElement(searchInputLocator).sendKeys(Keys.ENTER);
        var searchResults = driver.findElement(searchResultsLocator);
        Assert.assertTrue("Результаты поиска не отображаются", searchResults.isDisplayed());
        var actualSearchTerm = searchResults.getText();
        Assert.assertTrue("Результаты поиска не содержат заданный термин", actualSearchTerm.contains(searchTerm));
    }

    @Test
    public void verifyEmptySearchResults() {
        driver.navigate().refresh();
        driver.findElement(searchInputLocator).sendKeys(Keys.ENTER);
        var searchResults = driver.findElement(searchResultsLocator);
        Assert.assertTrue("Результаты поиска не отображаются", searchResults.isDisplayed());
    }

    @Test
    public void selectFirstItemFromSaleSection() {
        driver.navigate().refresh();
        var saleSection = driver.findElement(saleSectionLocator);
        var items = saleSection.findElements(activeSlideLocator);
        Assert.assertTrue("Блок Распродажа не содержит элементов", items.size() > 0);
        var firstItem = items.get(0);
        var titleElement = firstItem.findElement(titleElementLocator);
        var title = titleElement.getText();
        wait.until(ExpectedConditions.elementToBeClickable(firstItem));
        firstItem.click();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertTrue("Переход на страницу с деталями товара не выполнен", actualTitle.equalsIgnoreCase(expectedTitle));
    }

    @Test
    public void verifyRegisterNavigationLinkWork() {
        driver.navigate().refresh();
        driver.findElement(myAccountLinkLocator).click();
        var expectedTitle = "Мой аккаунт — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Неверныйтекст в заголовке страницы", expectedTitle, actualTitle);
        var loginForm = driver.findElement(By.className("woocommerce-form-login"));
        Assert.assertTrue("Не отображается форма регистрации и авторизации", loginForm.isDisplayed());
    }

    @Test
    public void verifyCategoryFirstPromoPageNavigation() {
        driver.navigate().refresh();
        var firstCategoryElement = driver.findElement(firstCategoryElementLocator);
        var titleFirstCategoryElement = driver.findElement(titleFirstCategoryElementLocator);

        wait.until(ExpectedConditions.visibilityOf(titleFirstCategoryElement));
        var title = titleFirstCategoryElement.getText();

        firstCategoryElement.click();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу с категорией товара не выполнен", expectedTitle.toLowerCase(), actualTitle.toLowerCase());
    }

    @Test
    public void verifyCategorySecondPromoPageNavigation() {
        driver.navigate().refresh();
        var firstCategoryElement = driver.findElement(secondCategoryElementLocator);
        var titleFirstCategoryElement = driver.findElement(titleSecondCategoryElementLocator);

        wait.until(ExpectedConditions.visibilityOf(titleFirstCategoryElement));
        var title = titleFirstCategoryElement.getText();

        firstCategoryElement.click();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу с категорией товара не выполнен", expectedTitle.toLowerCase(), actualTitle.toLowerCase());
    }

    @Test
    public void selectFirstItemNewSaleSection() {
        driver.navigate().refresh();
        var saleSection = driver.findElement(newSaleSectionLocator);
        var items = saleSection.findElements(activeSlideLocator);
        Assert.assertTrue("Блок Новые поступления не содержит элементов", items.size() > 0);
        var firstItem = items.get(0);
        var titleElement = firstItem.findElement(titleElementLocator);
        var title = titleElement.getText();
        wait.until(ExpectedConditions.elementToBeClickable(firstItem));
        firstItem.click();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertTrue("Переход на страницу с деталями товара не выполнен", actualTitle.equalsIgnoreCase(expectedTitle));
    }
}

