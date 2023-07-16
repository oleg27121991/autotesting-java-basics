import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;


public class BasketTest {
    private WebDriver driver;
    private WebDriverWait wait;

    // Локаторы
    private By emptyCartMessage = By.cssSelector(".cart-empty"); // Сообщение о пустой корзине
    private By addToCartButtonLocator = By.cssSelector(".add_to_cart_button"); // Кнопка "Добавить в корзину"
    private By productNameLocator = By.cssSelector(".collection_title"); // Название товара
    private By productPriceLocator = By.cssSelector("ins .woocommerce-Price-amount"); // Цена товара
    private By productNameInCartLocator = By.cssSelector(".product-name a"); // Название товара в корзине
    private By productPriceInCartLocator = By.cssSelector(".product-price bdi"); // Цена товара в корзине
    private By productQuantityInCartLocator = By.cssSelector(".product-quantity input"); // Количество товара в корзине
    private By couponFieldLocator = By.cssSelector("#coupon_code"); // Поле для ввода купона скидки
    private By applyCouponButtonLocator = By.cssSelector(".button[name='apply_coupon']"); // Кнопка "Применить" для купона скидки
    private By errorLocator = By.cssSelector(".woocommerce-error"); // Сообщение об ошибке
    private By totalPriceLocator = By.cssSelector(".product-subtotal bdi"); // Общая стоимость товара
    private By discountedProduct = By.xpath("//div[contains(@class, 'price-cart')][count(.//span[contains(@class, 'woocommerce-Price-amount')]) = 2]//a[contains(@class, 'add_to_cart_button')]");

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 8);
        driver.navigate().to("http://intershop5.skillbox.ru/cart/");
    }

    @Rule
    public TestName testName = new TestName();

    @After
    public void tearDown() {
        try {
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String methodName = testName.getMethodName();
            String fileName = methodName + ".png";
            FileHandler.copy(screenshot, new File("screenshots/" + fileName));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }

    @Test
    public void testAddToCart() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addToCartButton = driver.findElement(addToCartButtonLocator);

        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание загрузки Loadera
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Переход в Корзину
        var cartLink = driver.findElement(By.xpath("//a[contains(text(),'Корзина')]"));
        cartLink.click();

        // Проверка информацию о товаре в корзину
        var productNameInCart = driver.findElement(productNameInCartLocator).getText();
        var productPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        var productQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");

        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);
    }

    @Test
    public void testApplyCouponToCart() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание исчезновения загрузчика
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Переход на страницу корзины
        driver.get("http://intershop5.skillbox.ru/cart/");

        // Проверка, что добавленный товар отображается в корзине с правильной ценой и количеством
        var productNameInCart = driver.findElement(productNameInCartLocator).getText();
        var productPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        var productQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");
        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);

        // Найти поле для применения купона скидки на странице "Корзина"
        var couponField = driver.findElement(couponFieldLocator);

        // Ввести значение sert500 в поле для применения купона скидки
        couponField.sendKeys("sert500");

        // Нажать кнопку "Применить"
        var applyCouponButton = driver.findElement(applyCouponButtonLocator);
        applyCouponButton.click();

        // Проверить, что скидка в размере 500 рублей применена к общей стоимости заказа
        var totalDiscountElement = driver.findElement(By.cssSelector(".product-subtotal bdi"));
        var totalDiscountText = totalDiscountElement.getText();
        var decimalFormat = new DecimalFormat("#,##0.00");
        decimalFormat.setParseBigDecimal(true);
        var totalDiscountValue = (BigDecimal) decimalFormat.parse(totalDiscountText, new ParsePosition(0));
        var expectedDiscountValue = totalDiscountValue.subtract(BigDecimal.valueOf(500));
        var expectedDiscountText = decimalFormat.format(expectedDiscountValue) + "₽";
        Assert.assertEquals("Скидка не применена или неверное значение", expectedDiscountText.replace("\u00A0", ""), totalDiscountText.replace("\u00A0", ""));
    }

    @Test
    public void testInvalidCoupon() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание исчезновения загрузчика
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Переход на страницу корзины
        driver.get("http://intershop5.skillbox.ru/cart/");

        // Проверка, что добавленный товар отображается в корзине с правильной ценой и количеством
        var productNameInCart = driver.findElement(productNameInCartLocator).getText();
        var productPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        var productQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");
        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);

        // Найти поле для применения купона скидки на странице "Корзина"
        var couponField = driver.findElement(couponFieldLocator);

        // Ввести недействительное значение купона в поле для применения купона скидки
        couponField.sendKeys("invalidcoupon");

        // Нажать кнопку "Применить"
        var applyCouponButton = driver.findElement(applyCouponButtonLocator);
        applyCouponButton.click();

        // Ожидание появления сообщения об ошибке при попытке применения недействительного купона скидки
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));

        // Проверить, что система отображает сообщение об ошибке при попытке применения недействительного купона скидки
        var errorMessage = driver.findElement(errorLocator);
        Assert.assertEquals("Система не отображает сообщение об ошибке при попытке применения недействительного купона скидки", "Неверный купон.", errorMessage.getText());
    }

    @Test
    public void testRemoveFromCart() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание исчезновения загрузчика
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Переход на страницу корзины
        driver.get("http://intershop5.skillbox.ru/cart/");

        // Проверка, что добавленный товар отображается в корзине с правильной ценой и количеством
        var productNameInCart = driver.findElement(productNameInCartLocator).getText();
        var productPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        var productQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");
        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);
    }

    private String calculateTotalPrice(String price, String quantity) {
        double priceValue = Double.parseDouble(price.replace("₽", "").replace(",", ".").trim());
        int quantityValue = Integer.parseInt(quantity.trim());
        double totalPriceValue = priceValue * quantityValue;
        DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
        decimalFormat.setDecimalSeparatorAlwaysShown(true);
        return decimalFormat.format(totalPriceValue) + "₽";
    }

    @Test
    public void testUpdateCartItemQuantity() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Добавление товара в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        // Получение информации о товаре из карточки
        var productElement = addToCartButton.findElement(By.xpath("./ancestor::li[contains(@class, 'product')]"));
        var productNameElement = productElement.findElement(productNameLocator);
        var selectedProductName = productNameElement.getText();
        var productPriceElement = productElement.findElement(productPriceLocator);
        var selectedProductPrice = productPriceElement.getText();
        addToCartButton.click();

        // Ожидание исчезновения загрузчика
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Перейти на страницу "Корзина"
        driver.get("http://intershop5.skillbox.ru/cart/");

        // Проверка, что добавленный товар отображается в корзине с правильной ценой и количеством
        var productNameInCart = driver.findElement(productNameInCartLocator).getText();
        var productPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        var productQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");
        Assert.assertEquals("Название товара в корзине не соответствует выбранному товару", selectedProductName, productNameInCart);
        Assert.assertEquals("Цена товара в корзине не соответствует выбранному товару", selectedProductPrice, productPriceInCart);
        Assert.assertEquals("Количество товара в корзине не соответствует ожидаемому", "1", productQuantityInCart);

        // Найти поле или опцию для изменения количества товара в корзине
        var quantityInput = driver.findElement(productQuantityInCartLocator);

        // Изменить количество товара на другое значение
        quantityInput.clear();
        quantityInput.sendKeys("2");
        quantityInput.sendKeys(Keys.ENTER);

        // Явное ожидание обновления цены
        var expectedTotalPrice = calculateTotalPrice(selectedProductPrice, "2").replace("\u00A0", "");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(totalPriceLocator, expectedTotalPrice));

        // Проверить, что общая стоимость заказа и количество товара в корзине были корректно обновлены после изменения количества
        var updatedProductQuantityInCart = driver.findElement(productQuantityInCartLocator).getAttribute("value");
        var totalPriceInCart = driver.findElement(totalPriceLocator).getText();

        Assert.assertEquals("Цена товара в корзине не обновлена после изменения", calculateTotalPrice(selectedProductPrice, "2").replace("\u00A0", ""), totalPriceInCart);
        Assert.assertEquals("Количество товара в корзине не обновлено после изменения", "2", updatedProductQuantityInCart);
        Assert.assertEquals("Общая стоимость заказа в корзине не обновлена после изменения", expectedTotalPrice, totalPriceInCart);
    }

    @Test
    public void testDiscountedProductPriceInCart() {
        driver.navigate().refresh();

        // Проверка, что корзина пуста
        driver.findElement(emptyCartMessage);
        Assert.assertTrue("Корзина не пуста", driver.findElement(emptyCartMessage).isDisplayed());

        // Найти товар на сайте с акцией и добавить его в корзину
        driver.navigate().to("http://intershop5.skillbox.ru/product-category/catalog/");
        var firstDiscountProduct =  driver.findElement(discountedProduct);
        firstDiscountProduct.click();

        // Ожидание загрузки Loadera
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Перейти на страницу "Корзина"
        driver.get("http://intershop5.skillbox.ru/cart/");

        // Получить информацию о товаре с акцией из корзины
        var productPriceInCart = driver.findElement(By.cssSelector(".product-price bdi")).getText();

        // Применить скидочный купон
        var couponField = driver.findElement(couponFieldLocator);
        couponField.sendKeys("sert500");
        var applyCouponButton = driver.findElement(applyCouponButtonLocator);
        applyCouponButton.click();

        // Явное ожидание обновления цены
        wait.until(ExpectedConditions.textToBePresentInElementLocated(productPriceInCartLocator, productPriceInCart));

        // Проверить, что цена для товара с акцией не сбрасывается на доакционную
        var updatedProductPriceInCart = driver.findElement(productPriceInCartLocator).getText();
        Assert.assertEquals("Цена для товара с акцией сбрасывается на доакционную после применения скидочного купона", productPriceInCart, updatedProductPriceInCart);
    }
}

