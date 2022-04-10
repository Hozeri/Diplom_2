import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class UserLoginTest {

    private User user;
    private UserClient userClient;

    @Before
    public void setUp() {
        user = User.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteUser();
    }

    @Test
    @DisplayName("Авторизация под существующим пользователем")
    public void loginUserWithExistUserReturnsCodeOKTest() {
        UserCredentials userCredentials = UserCredentials.getUserCredentials(user);
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response loginResponse = userClient.login(userCredentials).extract().response();
        assertEquals("Status code isn't OK", SC_OK, loginResponse.statusCode());
        assertTrue(loginResponse.path("success"));
    }

}
