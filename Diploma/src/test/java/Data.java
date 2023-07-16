public class Data {

    // Данные для авторизации
    protected String email = "test3@example.com";
    protected String username = "testuser4";
    protected String password = "Test1234!";
    protected String existingUsername = "testuser";

    // Оформление заказа
    protected String firstName = "Иван";
    protected String lastName = "Иванов";
    protected String address = "Тестовая д.1 кв.75";
    protected String city = "Жабинка";
    protected String state = "Московская";
    protected String postcode = "123321";
    protected String phone = "12312121212";

    // Регистрация нового покупателя
    long currentTime = System.currentTimeMillis();
    String timestamp = String.valueOf(currentTime);
    String shortenedTimestamp = timestamp.substring(timestamp.length() - 4);
    protected String regUsername = "testuser" + shortenedTimestamp;
    protected String regEmail = "test" + shortenedTimestamp + "@example.com";
}
