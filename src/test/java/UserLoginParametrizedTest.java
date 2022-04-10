import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserLoginParametrizedTest {

    private UserClient userClient;

    @Before
    public void setUp() {
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteUser();
    }

    private static final User user = User.getRandomUser();
    private final String emailAddress;
    private final String password;

    public UserLoginParametrizedTest(String emailAddress, String password) {
        this.emailAddress = emailAddress;
        this.password = password;
    }

    @Parameterized.Parameters(name = "Данные пользователя: {0} {1}")
    public static Object[][] getParamsForUserCredentials() {
        return new Object[][]{
                {user.getEmail() + "1", user.getPassword()},
                {user.getEmail(), user.getPassword() + "1"},
                {user.getEmail() + "1", user.getPassword() + "1"},
                {user.getEmail(), null},
                {null, user.getPassword()},
                {null, null}
        };
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректными логином и паролем")
    public void loginUserWithIncorrectUserDataReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        UserCredentials userCredentials = new UserCredentials(emailAddress, password);
        Response loginResponse = userClient.login(userCredentials).extract().response();
        assertEquals("Status code isn't UNAUTHORIZED", SC_UNAUTHORIZED, loginResponse.statusCode());
        assertEquals("Value of the 'message' doesn't match with expected one", "email or password are incorrect", loginResponse.path("message"));
    }
}