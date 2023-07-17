import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;

public class OrderPlacementTests extends Data {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 8);
        driver.navigate().to("http://intershop5.skillbox.ru/");
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

    private By loginLinkLocator = By.cssSelector(".account"); // Ссылка "Войти" в личный кабинет
    private By loginFormLocator = By.cssSelector(".content-inner"); // Форма для ввода логина и пароля
    private By inputUserName = By.id("username"); // Поле для ввода "Имя пользователя или почта *"
    private By inputUserPassword = By.id("password"); // Поле для ввода "Пароль *"
    private By btnLoginLocator = By.cssSelector("button[name='login']"); // Кнопка "Войти" в личный кабинет
    private By catalogLinkLocator = By.xpath("//a[contains(text(),'Каталог')]"); // Ссылка на каталог товаров
    private By addToCartButtonLocator = By.cssSelector(".add_to_cart_button"); // Кнопка "Добавить в корзину"
    private By checkoutLinkLocator = By.xpath("//a[contains(text(),'Оформление заказа')]"); // Кнопка "Оформление заказа"
    private By inputFirstName = By.id("billing_first_name"); // Поле для ввода имени покупателя
    private By inputLastName = By.id("billing_last_name"); // Поле для ввода фамилии покупателя
    private By inputAddress = By.id("billing_address_1"); // Поле для ввода адреса (улица и номер дома)
    private By inputCity = By.id("billing_city"); // Поле для ввода города
    private By inputState = By.id("billing_state"); // Поле для ввода области
    private By inputPostcode = By.id("billing_postcode"); // Поле для ввода индекса
    private By inputPhone = By.id("billing_phone"); // Поле для ввода телефона
    private By inputMail = By.id("billing_email"); // Поле для ввода email
    private By inputPaymentMethodCOD = By.id("payment_method_cod"); // Чекбокс "оплата при доставке"
    private By pageLoaderLocator = By.cssSelector("div.blockUI.blockOverlay"); // Loader на странице
    private By btnPlaceOrder = By.id("place_order"); // Кнопка "Оформить заказ"
    private By orderSuccessMessageLocator = By.cssSelector(".post-title"); // Сообщение об успешном оформлении заказа


    @Test
    public void testValidOrderPlacement() {
        // Переход в каталог товара и добавление товара в корзину
        driver.findElement(catalogLinkLocator).click();
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        addToCartButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Открытие страницы авторизации и ввод имени и пароля
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Переход на страницу Оформление заказа и заполнение полей
        driver.findElement(checkoutLinkLocator).click();
        driver.findElement(inputFirstName).clear();
        driver.findElement(inputFirstName).sendKeys(firstName);
        driver.findElement(inputLastName).clear();
        driver.findElement(inputLastName).sendKeys(lastName);
        driver.findElement(inputAddress).clear();
        driver.findElement(inputAddress).sendKeys(address);
        driver.findElement(inputCity).clear();
        driver.findElement(inputCity).sendKeys(city);
        driver.findElement(inputState).clear();
        driver.findElement(inputState).sendKeys(state);
        driver.findElement(inputPostcode).clear();
        driver.findElement(inputPostcode).sendKeys(postcode);
        driver.findElement(inputPhone).clear();
        driver.findElement(inputPhone).sendKeys(phone);

        // Выбор способа оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        var paymentMethodInOrderPageElement = driver.findElement(By.cssSelector("label[for='payment_method_bacs']"));
        String paymentMethodInOrderPageText = paymentMethodInOrderPageElement.getText();

        // Scroll к кнопке "Оформить заказ" и нажатие по ней
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder);

        // Ожидание исчезновения элемента Loader
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder).click();

        // Явное ожидание перехода на страницу подтверждения заказа
        wait.until(ExpectedConditions.urlContains("/checkout/order-received/")); // Указываем часть URL, содержащуюся до нужного значения

        // Проверка отображения сообщения об успешном оформлении заказа
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessageLocator));
        var orderSuccessMessageElement = driver.findElement(orderSuccessMessageLocator);
        Assert.assertTrue("Не отображается сообщение об успешном оформлении заказа", orderSuccessMessageElement.isDisplayed());

        // Проверка отображения выбранного метода оплаты
        var paymentMethodInOrderElement = driver.findElement(By.cssSelector(".method strong"));
        String paymentMethodInOrderText = paymentMethodInOrderElement.getText();
        Assert.assertEquals("Отображается не правильный метод оплаты", paymentMethodInOrderText, paymentMethodInOrderPageText);
    }

    @Test
    public void testValidOrderPlacementWithCODPayment() {
        // Переход в каталог товара и добавление товара в корзину
        driver.findElement(catalogLinkLocator).click();
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        addToCartButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Открытие страницы авторизации и ввод имени и пароля
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Переход на страницу Оформление заказа и заполнение полей
        driver.findElement(checkoutLinkLocator).click();
        driver.findElement(inputFirstName).clear();
        driver.findElement(inputFirstName).sendKeys(firstName);
        driver.findElement(inputLastName).clear();
        driver.findElement(inputLastName).sendKeys(lastName);
        driver.findElement(inputAddress).clear();
        driver.findElement(inputAddress).sendKeys(address);
        driver.findElement(inputCity).clear();
        driver.findElement(inputCity).sendKeys(city);
        driver.findElement(inputState).clear();
        driver.findElement(inputState).sendKeys(state);
        driver.findElement(inputPostcode).clear();
        driver.findElement(inputPostcode).sendKeys(postcode);
        driver.findElement(inputPhone).clear();
        driver.findElement(inputPhone).sendKeys(phone);

        // Выбор способа оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        var paymentMethodInOrderPageElement = driver.findElement(By.cssSelector("label[for='payment_method_cod']"));
        String paymentMethodInOrderPageText = paymentMethodInOrderPageElement.getText();
        driver.findElement(inputPaymentMethodCOD).click();

        // Scroll к кнопке "Оформить заказ" и нажатие по ней
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder);

        // Ожидание исчезновения элемента Loader
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder).click();

        // Явное ожидание перехода на страницу подтверждения заказа
        wait.until(ExpectedConditions.urlContains("/checkout/order-received/")); // Указываем часть URL, содержащуюся до нужного значения

        // Проверка отображения сообщения об успешном оформлении заказа
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessageLocator));
        var orderSuccessMessageElement = driver.findElement(orderSuccessMessageLocator);
        Assert.assertTrue("Не отображается сообщение об успешном оформлении заказа", orderSuccessMessageElement.isDisplayed());

        // Проверка отображения выбранного метода оплаты
        var paymentMethodInOrderElement = driver.findElement(By.cssSelector(".method strong"));
        String paymentMethodInOrderText = paymentMethodInOrderElement.getText();
        Assert.assertEquals("Отображается не правильный метод оплаты", paymentMethodInOrderText, paymentMethodInOrderPageText);
    }

    private By errorLocator = By.cssSelector(".woocommerce-error"); // Форма с сообщениями об ошибках
    private By billingFirstNameLocator = By.cssSelector("li[data-id='billing_first_name']"); // Сообщение об ошибке при незаполненном имени покупателя
    private By billingLastNameLocator = By.cssSelector("li[data-id='billing_last_name']"); // Сообщение об ошибке при незаполненном фамилии покупателя
    private By billingAddressLocator = By.cssSelector("li[data-id='billing_address_1']"); // Сообщение об ошибке при незаполненном адресе покупателя
    private By billingCityLocator = By.cssSelector("li[data-id='billing_city']"); // Сообщение об ошибке при незаполненном городе покупателя
    private By billingStateLocator = By.cssSelector("li[data-id='billing_state']"); // Сообщение об ошибке при незаполненном области покупателя
    private By billingPostcodeLocator = By.cssSelector("li[data-id='billing_postcode']"); // Сообщение об ошибке при незаполненном индексе покупателя
    private By billingPhoneLocator = By.cssSelector("li[data-id='billing_phone']"); // Сообщение об ошибке при незаполненном номере телефона покупателя
    private By billingEmailLocator = By.cssSelector("li[data-id='billing_email']"); // Сообщение об ошибке при незаполненном Email покупателя


    @Test
    public void testInvalidOrderPlacementWithEmptyField() {
        // Переход в каталог товара и добавление товара в корзину
        driver.findElement(catalogLinkLocator).click();
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        addToCartButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Открытие страницы авторизации и ввод имени и пароля
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Переход на страницу Оформление заказа и заполнение полей
        driver.findElement(checkoutLinkLocator).click();
        driver.findElement(inputFirstName).clear();
        driver.findElement(inputLastName).clear();
        driver.findElement(inputAddress).clear();
        driver.findElement(inputCity).clear();
        driver.findElement(inputState).clear();
        driver.findElement(inputPostcode).clear();
        driver.findElement(inputMail).clear();
        driver.findElement(inputPhone).clear();

        // Scroll к кнопке "Оформить заказ" и нажатие по ней
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        var placeOrderButton = driver.findElement(btnPlaceOrder);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", placeOrderButton);
        driver.findElement(btnPlaceOrder).click();

        // Проверка отображения ошибок о незаполненных полях
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("window.scrollTo(0, 0);");
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorLocator));

        // Сообщение об ошибке при незаполненном имени покупателя
        var errorFirstName = driver.findElement(billingFirstNameLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле имени", errorFirstName.isDisplayed());
        // Сообщение об ошибке при незаполненном фамилии покупателя
        var errorLastName = driver.findElement(billingLastNameLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле фамилии", errorLastName.isDisplayed());
        // Сообщение об ошибке при незаполненном адресе покупателя
        var errorAddress = driver.findElement(billingAddressLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorAddress.isDisplayed());
        // Сообщение об ошибке при незаполненном городе покупателя
        var errorCity = driver.findElement(billingCityLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorCity.isDisplayed());
        // Сообщение об ошибке при незаполненном области покупателя
        var errorState = driver.findElement(billingStateLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorState.isDisplayed());
        // Сообщение об ошибке при незаполненном индексе покупателя
        var errorPostcode = driver.findElement(billingPostcodeLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorPostcode.isDisplayed());
        // Сообщение об ошибке при незаполненном номере телефона покупателя
        var errorPhone = driver.findElement(billingPhoneLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorPhone.isDisplayed());
        // Сообщение об ошибке при незаполненном Email покупателя
        var errorEmail = driver.findElement(billingEmailLocator);
        Assert.assertTrue("Не отображается ошибка о пустом поле адрес", errorEmail.isDisplayed());
    }

    private By couponInputLocator = By.id("coupon_code"); // Поле для ввода купона
    private By buttonApplyCoupon = By.cssSelector("button[name='apply_coupon']"); // Кнопка "Применить купон"
    private By successMessageCouponLocator = By.cssSelector(".woocommerce-message"); // Сообщение об успешном применении купона
    private By cartDiscountLocator = By.cssSelector(".cart-discount"); // Поле с купоном в таблице "Ваш заказ"
    private By totalAmountLocator = By.cssSelector(".order-total .woocommerce-Price-amount bdi"); // Итоговая цена "К оплате"
    private By subTotalLocator = By.cssSelector(".cart-subtotal .woocommerce-Price-amount bdi"); // Общая стоимость товара

    @Test
    public void testApplyCouponOnCheckoutPage() {
        // Переход в каталог товара и добавление товара в корзину
        driver.findElement(catalogLinkLocator).click();
        var addToCartButton = driver.findElement(addToCartButtonLocator);
        addToCartButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(addToCartButtonLocator));

        // Открытие страницы авторизации и ввод имени и пароля
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Переход на страницу Оформление заказа и заполнение полей
        driver.findElement(checkoutLinkLocator).click();
        driver.findElement(inputFirstName).clear();
        driver.findElement(inputFirstName).sendKeys(firstName);
        driver.findElement(inputLastName).clear();
        driver.findElement(inputLastName).sendKeys(lastName);
        driver.findElement(inputAddress).clear();
        driver.findElement(inputAddress).sendKeys(address);
        driver.findElement(inputCity).clear();
        driver.findElement(inputCity).sendKeys(city);
        driver.findElement(inputState).clear();
        driver.findElement(inputState).sendKeys(state);
        driver.findElement(inputPostcode).clear();
        driver.findElement(inputPostcode).sendKeys(postcode);
        driver.findElement(inputPhone).clear();
        driver.findElement(inputPhone).sendKeys(phone);

        // Выбор способа оплаты "Оплата при доставке"
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        var paymentMethodInOrderPageElement = driver.findElement(By.cssSelector("label[for='payment_method_cod']"));
        String paymentMethodInOrderPageText = paymentMethodInOrderPageElement.getText();
        driver.findElement(inputPaymentMethodCOD).click();

        // Ввод действительного купона
        driver.findElement(By.cssSelector(".showcoupon")).click(); // Ссылка для открытия поля для ввода купона
        var couponInputField = driver.findElement(couponInputLocator);
        wait.until(ExpectedConditions.visibilityOfElementLocated(couponInputLocator));
        couponInputField.sendKeys("sert500");
        var applyCouponButton = driver.findElement(buttonApplyCoupon);
        applyCouponButton.click();

        // Ожидание появления сообщения об успешном применении купона
        var successMessageElement = wait.until(ExpectedConditions.visibilityOfElementLocated(successMessageCouponLocator));
        Assert.assertTrue("Не отображается сообщение об успешном применении купона", successMessageElement.isDisplayed());

        // Проверка отображения примененного купона в таблице "Ваш заказ"
        var cartDiscount = wait.until(ExpectedConditions.visibilityOfElementLocated(cartDiscountLocator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", cartDiscount);
        Assert.assertTrue("Не отображается строка с купоном в таблице 'Ваш заказ'", cartDiscount.isDisplayed());

        // Проверка корректной итоговой цены товара
        var totalAmountElement = driver.findElement(totalAmountLocator);
        String totalAmountText = totalAmountElement.getText();
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", totalAmountElement);
        var subTotalElement = driver.findElement(subTotalLocator);
        String subTotalText = subTotalElement.getText();

        // Извлечение числового значения из строк цен
        double subTotal = Double.parseDouble(subTotalText.replaceAll("[^0-9.,]+", "").replace(",", "."));
        double expectedTotalAmount = subTotal - 500;
        String expectedTotalAmountText = String.format("%.2f₽", expectedTotalAmount);
        Assert.assertEquals("Итоговая цена товара не соответствует ожидаемой", expectedTotalAmountText, totalAmountText);

        // Scroll к кнопке "Оформить заказ" и нажатие по ней
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder);

        // Ожидание исчезновения элемента Loader
        wait.until(ExpectedConditions.invisibilityOfElementLocated(pageLoaderLocator));
        driver.findElement(btnPlaceOrder).click();

        // Явное ожидание перехода на страницу подтверждения заказа
        wait.until(ExpectedConditions.urlContains("/checkout/order-received/")); // Указываем часть URL, содержащуюся до нужного значения

        // Проверка отображения сообщения об успешном оформлении заказа
        wait.until(ExpectedConditions.visibilityOfElementLocated(orderSuccessMessageLocator));
        var orderSuccessMessageElement = driver.findElement(orderSuccessMessageLocator);
        Assert.assertTrue("Не отображается сообщение об успешном оформлении заказа", orderSuccessMessageElement.isDisplayed());
    }
}
