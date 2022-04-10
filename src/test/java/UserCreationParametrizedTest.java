import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserCreationParametrizedTest {

    public String email;
    public String password;
    public String name;

    public UserCreationParametrizedTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static Faker faker = new Faker();

    @Parameterized.Parameters(name = "Данные пользователя: {0} {1} {2}")
    public static Object[][] getParamsForUser() {
        return new Object[][]{
                {null, faker.internet().password(), faker.name().name()},
                {faker.internet().emailAddress(), null, faker.name().name()},
                {faker.internet().emailAddress(), faker.internet().password(), null}
        };
    }

    @Test
    @DisplayName("Регистрация пользователя с отсутствием одного из обязательных полей")
    public void userWithPartialDataCodeForbiddenTest() {
        UserClient userClient = new UserClient();
        User user = new User(email, password, name);
        Response response = userClient.create(user).extract().response();
        assertEquals("Status code isn't FORBIDDEN", SC_FORBIDDEN, response.statusCode());
        assertEquals("Value of the 'message' doesn't match with expected one","Email, password and name are required fields", response.path("message"));
    }
}
