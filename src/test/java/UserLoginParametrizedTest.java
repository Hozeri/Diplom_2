import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class UserLoginParametrizedTest {

    @After
    public void tearDown() {
        UserClient userClient = new UserClient();
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
    public void loginUserWithIncorrectUserDataReturnsCodeUnauthorizedTest() {
        UserClient userClient = new UserClient();
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response) ;
        UserCredentials userCredentials = new UserCredentials(emailAddress, password);
        userClient.login(userCredentials)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", is("email or password are incorrect"));
    }
}