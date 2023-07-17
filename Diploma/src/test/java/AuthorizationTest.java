import org.apache.commons.io.FileUtils;
import org.junit.*;
import org.junit.rules.TestName;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AuthorizationTest extends Data {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "drivers//chromedriver");
        driver = new ChromeDriver();
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
    private By usernameLocator = By.cssSelector("div.woocommerce-MyAccount-content strong"); // Имя пользователя в форме приветствия
    private By usernameHeaderLocator = By.cssSelector(".user-name"); // Имя пользователя в header сайта
    private By errorMessageLocator = By.cssSelector(".woocommerce-error li"); // Сообщение о неправильном имени или пароле
    private By passwordRecoveryLinkLocator = By.cssSelector(".woocommerce-error a"); // Ссылка на восстановление пароля

    @Test
    public void testSuccessfulAuthentication() {
        // Открытие страницы авторизации и ввод имени и пароля
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Проверка заголовка страницы
        String expectedTitle = "Мой аккаунт — Skillbox";
        String actualTitle = driver.getTitle();
        Assert.assertEquals("Не совпадает заголовок страницы", expectedTitle, actualTitle);

        // Проверка успешной авторизации
        var usernameElement = driver.findElement(usernameLocator);
        String extractedUsername = usernameElement.getText();
        Assert.assertEquals("Не совпадает имя пользователя", username, extractedUsername);

        // Проверка правильного отображения имени пользователя в Header
        var usernameHeaderElement = driver.findElement(usernameHeaderLocator);
        String extractedUsernameHeader = usernameHeaderElement.getText();
        Assert.assertEquals("Не совпадает имя пользователя в Header", username, extractedUsernameHeader);
    }

    @Test
    public void testInvalidUsernameAuthentication() {
        // Открытие страницы авторизации и ввод неправильных имени
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys("incorusername");
        driver.findElement(inputUserPassword).sendKeys(password);
        driver.findElement(btnLoginLocator).click();

        // Проверка отображения сообщения об ошибке
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));
        var errorMessageElement = driver.findElement(errorMessageLocator);
        String errorMessage = errorMessageElement.getText();
        Assert.assertTrue("Сообщение об ошибке не отображается", !errorMessage.isEmpty());
    }

    @Test
    public void testInvalidPasswordAuthentication() {
        // Открытие страницы авторизации и ввод неправильных имени
        driver.findElement(loginLinkLocator).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(loginFormLocator));
        driver.findElement(inputUserName).sendKeys(username);
        driver.findElement(inputUserPassword).sendKeys("incorpassword");
        driver.findElement(btnLoginLocator).click();

        // Проверка отображения сообщения об ошибке
        wait.until(ExpectedConditions.visibilityOfElementLocated(errorMessageLocator));
        var errorMessageElement = driver.findElement(errorMessageLocator);
        String errorMessage = errorMessageElement.getText();
        Assert.assertTrue("Сообщение об ошибке не отображается", !errorMessage.isEmpty());

        // Проверка отображения ссылки на восстановления пароля
        wait.until(ExpectedConditions.visibilityOfElementLocated(passwordRecoveryLinkLocator));
        boolean isPasswordRecoveryLinkDisplayed = driver.findElement(passwordRecoveryLinkLocator).isDisplayed();
        Assert.assertTrue("Ссылка на восстановление пароля не отображается", isPasswordRecoveryLinkDisplayed);
    }


}
