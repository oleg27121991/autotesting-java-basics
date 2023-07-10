import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogTestScenario {
    private WebDriver driver;
    private WebDriverWait wait;

    // Локаторы
    private By productListLocator = By.cssSelector(".products"); // Список товаров
    private By productLocator = By.cssSelector(".product"); // Товар
    private By productNameLocator = By.cssSelector(".collection_title"); // Название товара
    private By productPriceLocator = By.cssSelector("ins .woocommerce-Price-amount"); // Цена товара
    private By searchInputLocator = By.xpath("//input[@class='search-field']"); // Поле ввода поиска
    private By searchResultsLocator = By.cssSelector(".products"); // Результаты поиска
    private By pageTitleLocator = By.cssSelector(".product_title"); // Заголовок страницы
    private By sortByPriceSelectLocator = By.cssSelector(".orderby"); // Выпадающий список сортировки по цене

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 8);
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
    }

    @After
    public void tearDown() throws IOException {
        var sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        FileUtils.copyFile(sourceFile, new File("desktop\\screenshot"));
        driver.quit();
    }

    // Метод для проверки порядка товаров по цене
    private boolean checkProductSortingByPrice(List<WebElement> productElements) {
        var productPrices = new ArrayList<Double>();

        for (var productElement : productElements) {
            var productPriceElement = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".woocommerce-Price-amount.amount bdi")));
            var productPriceText = productPriceElement.getText().replaceAll("[^\\d.]+", "");
            var productPrice = Double.parseDouble(productPriceText);
            productPrices.add(productPrice);
        }

        var sortedProductPrices = new ArrayList<>(productPrices);
        Collections.sort(sortedProductPrices);

        return productPrices.equals(sortedProductPrices);
    }

    @Test
    public void testProductListDisplay() {
        driver.navigate().refresh();
        var productList = driver.findElement(productListLocator);
        Assert.assertTrue("Список товаров не отображается", productList.isDisplayed());

        // Проверка, что товары отображаются плитками по 12 на странице
        var expectedProductCount = 12; // Ожидаемое количество товаров
        var actualProductCount = productList.findElements(productLocator).size();
        Assert.assertEquals("Неправильное количество товаров в списке", expectedProductCount, actualProductCount);
    }

    @Test
    public void testProductSearch() {
        driver.navigate().refresh();
        var searchInput = driver.findElement(searchInputLocator);

        var keyword = "Apple";
        searchInput.sendKeys(keyword);
        searchInput.sendKeys(Keys.ENTER);

        // Проверка, что результаты поиска отображаются
        var searchResults = driver.findElement(searchResultsLocator);
        Assert.assertTrue("Результаты поиска не отображаются", searchResults.isDisplayed());

        var pageTitleElement = driver.findElement(By.cssSelector(".entry-title"));
        var pageTitle = pageTitleElement.getText();

        // Проверка, что заголовок содержит введенное ключевое слово
        Assert.assertTrue("Заголовок не содержит введенное ключевое слово", pageTitle.toLowerCase().contains(keyword.toLowerCase()));
    }

    @Test
    public void testProductSortingByPrice() {
        driver.navigate().refresh();
        var sortByPriceSelect = driver.findElement(sortByPriceSelectLocator);

        // "По возрастанию цены"
        var option = new Select(sortByPriceSelect);
        option.selectByValue("price");

        var productList = driver.findElement(productListLocator);
        var productElements = productList.findElements(productLocator);

        // Проверка, что список товаров не пустой
        Assert.assertTrue("Список товаров не обновлен", !productElements.isEmpty());

        // Проверка, что товары отображаются в правильном порядке по цене
        var isSortedByPrice = checkProductSortingByPrice(productElements);
        Assert.assertTrue("Товары отображаются в неправильном порядке по цене", isSortedByPrice);

        // Проверка, что отображается корректное количество отфильтрованных товаров
        var expectedProductCount = 12;
        var actualProductCount = productElements.size();
        Assert.assertEquals("Отображается неверное количество отфильтрованных товаров", expectedProductCount, actualProductCount);
    }

    @Test
    public void testProductDetailsPage() {
        driver.navigate().refresh();
        var productElement = driver.findElement(productLocator);
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var selectedProductPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = selectedProductPriceElement.getText();
        productElement.click();

        // Проверка информации о товаре на странице деталей
        var pageTitleElement = driver.findElement(pageTitleLocator);
        String pageTitle = pageTitleElement.getText();
        String expectedTitle = selectedProductName;
        Assert.assertEquals("Заголовок страницы не соответствует ожидаемому", expectedTitle, pageTitle);

        productNameElement = driver.findElement(pageTitleLocator);
        var productPriceElement = driver.findElement(productPriceLocator);

        var productName = productNameElement.getText();
        var productPrice = productPriceElement.getText();

        Assert.assertEquals("Название товара на странице деталей не соответствует выбранному товару", selectedProductName, productName);
        Assert.assertEquals("Цена товара на странице деталей не соответствует выбранному товару", selectedProductPrice, productPrice);
    }

    @Test
    public void testAddToCart() {
        driver.navigate().refresh();
        var addToCartButton = driver.findElement(By.cssSelector(".add_to_cart_button"));

        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание загрузки Loadera
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".add_to_cart_button")));

        // Переход в Корзину
        var cartLink = driver.findElement(By.xpath("//a[contains(text(),'Корзина')]"));
        cartLink.click();

        // Проверка информацию о товаре в корзину
        var productNameInCart = driver.findElement(By.cssSelector(".product-name a")).getText();
        var productPriceInCart = driver.findElement(By.cssSelector(".product-price .woocommerce-Price-amount")).getText();
        var productQuantityInCart = driver.findElement(By.cssSelector(".input-text.qty")).getAttribute("value");

        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);
    }
}

