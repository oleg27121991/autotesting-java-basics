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

public class RegisterTestScenario extends Data {
    private WebDriver driver;
    private WebDriverWait wait;

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

    public By loginButtonLocator = By.cssSelector(".account");
    public By pageTitleLocator = By.cssSelector(".post-title");
    public By registerButtonLocator = By.cssSelector(".custom-register-button");
    public By usernameInputLocator = By.id("reg_username");
    public By emailInputLocator = By.id("reg_email");
    public By passwordInputLocator = By.id("reg_password");
    public By registerNewUserButtonLocator = By.cssSelector(".woocommerce-form-register__submit");
    public By confirmationMessageElementLocator = By.cssSelector(".content-page");
    public By errorMessageLocator = By.xpath("//ul[@class='woocommerce-error']//li");

    @Test
    public void testUserRegistration() {
        driver.navigate().refresh();

        // Переход на страницу авторизации
        driver.findElement(loginButtonLocator).click();
        var titleAccountPage = driver.findElement(pageTitleLocator);
        var title = titleAccountPage.getText();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу авторизации не выполнен", expectedTitle.toLowerCase(), actualTitle.toLowerCase());

        // Переход на страницу регистрации
        driver.findElement(registerButtonLocator).click();
        var titleRegisterPage = driver.findElement(pageTitleLocator);
        var titleRegister = titleRegisterPage.getText();
        var expectedRegisterTitle = titleRegister + " — Skillbox";
        var actualRegisterTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу регистрации не выполнен", expectedRegisterTitle.toLowerCase(), actualRegisterTitle.toLowerCase());

        // Ввести уникальное имя пользователя
        var usernameInput = driver.findElement(usernameInputLocator);
        usernameInput.sendKeys(username);

        // Ввести допустимый электронный адрес
        var emailInput = driver.findElement(emailInputLocator);
        emailInput.sendKeys(email);

        // Ввести безопасный пароль
        var passwordInput = driver.findElement(passwordInputLocator);
        passwordInput.sendKeys(password);

        // Нажать кнопку "Зарегистрироваться"
        var registerButton = driver.findElement(registerNewUserButtonLocator);
        registerButton.click();

        // Проверить, что пользователь успешно зарегистрирован и перенаправлен на страницу подтверждения
        wait.until(ExpectedConditions.visibilityOfElementLocated(confirmationMessageElementLocator));
        String confirmationMessage = "Регистрация завершена";
        var confirmationMessageElement = driver.findElement(confirmationMessageElementLocator);
        String actualMessage = confirmationMessageElement.getText();
        Assert.assertEquals("Пользователь не зарегистрирован или неправильное сообщение об успешной регистрации", confirmationMessage.toLowerCase(), actualMessage.toLowerCase());
    }

    @Test
    public void testExistingEmailRegistration() {
        driver.navigate().refresh();

        // Переход на страницу авторизации
        driver.findElement(loginButtonLocator).click();
        var titleAccountPage = driver.findElement(pageTitleLocator);
        var title = titleAccountPage.getText();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу авторизации не выполнен", expectedTitle.toLowerCase(), actualTitle.toLowerCase());

        // Переход на страницу регистрации
        driver.findElement(registerButtonLocator).click();
        var titleRegisterPage = driver.findElement(pageTitleLocator);
        var titleRegister = titleRegisterPage.getText();
        var expectedRegisterTitle = titleRegister + " — Skillbox";
        var actualRegisterTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу регистрации не выполнен", expectedRegisterTitle.toLowerCase(), actualRegisterTitle.toLowerCase());

        // Ввести существующий адрес электронной почты
        var emailInput = driver.findElement(emailInputLocator);
        emailInput.sendKeys(email);

        // Ввести имя пользователя
        var usernameInput = driver.findElement(usernameInputLocator);
        usernameInput.sendKeys(username);

        // Ввести безопасный пароль
        var passwordInput = driver.findElement(passwordInputLocator);
        passwordInput.sendKeys(password);

        // Нажать кнопку "Зарегистрироваться"
        var registerButton = driver.findElement(registerNewUserButtonLocator);
        registerButton.click();

        // Проверить, что система отображает сообщение об ошибке
        WebElement errorMessageElement = driver.findElement(errorMessageLocator);
        var actualErrorMessage = errorMessageElement.getText();
        var expectedErrorMessage = "Error: Учетная запись с такой почтой уже зарегистировавана. Пожалуйста авторизуйтесь.";
        Assert.assertEquals("Система не отображает сообщение об ошибке с уже существующим именем пользователя", expectedErrorMessage, actualErrorMessage);
    }

    @Test
    public void testRegistrationWithEmptyFields() {
        driver.navigate().refresh();

        // Переход на страницу авторизации
        driver.findElement(loginButtonLocator).click();
        var titleAccountPage = driver.findElement(pageTitleLocator);
        var title = titleAccountPage.getText();
        var expectedTitle = title + " — Skillbox";
        var actualTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу авторизации не выполнен", expectedTitle.toLowerCase(), actualTitle.toLowerCase());

        // Переход на страницу регистрации
        driver.findElement(registerButtonLocator).click();
        var titleRegisterPage = driver.findElement(pageTitleLocator);
        var titleRegister = titleRegisterPage.getText();
        var expectedRegisterTitle = titleRegister + " — Skillbox";
        var actualRegisterTitle = driver.getTitle();
        Assert.assertEquals("Переход на страницу регистрации не выполнен", expectedRegisterTitle.toLowerCase(), actualRegisterTitle.toLowerCase());

        // Оставить адрес электронной почты пустым
        var emailInput = driver.findElement(emailInputLocator);
        emailInput.sendKeys("");

        // Оставить поле имя пользователя пустым
        var usernameInput = driver.findElement(usernameInputLocator);
        usernameInput.sendKeys("");

        // Оставить поле пароль пустым
        var passwordInput = driver.findElement(passwordInputLocator);
        passwordInput.sendKeys("");

        // Нажать кнопку "Зарегистрироваться"
        var registerButton = driver.findElement(registerNewUserButtonLocator);
        registerButton.click();

        // Проверить, что система отображает сообщение об ошибке
        WebElement errorMessageElement = driver.findElement(errorMessageLocator);
        var actualErrorMessage = errorMessageElement.getText();
        var expectedErrorMessage = "Error: Пожалуйста, введите корректный email.";
        Assert.assertEquals("Система не отображает сообщение об ошибке с уже существующим именем пользователя", expectedErrorMessage, actualErrorMessage);
    }
}