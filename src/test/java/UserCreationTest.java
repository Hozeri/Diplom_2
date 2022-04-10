import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.*;

public class UserCreationTest {

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
    @DisplayName("Регистрация уникального пользователя")
    public void createUserWithRequiredDataReturnsCodeOKTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        assertEquals("Status code isn't OK", SC_OK, response.statusCode());
        assertTrue(response.path("success"));
    }

    @Test
    @DisplayName("Регистрация пользователя с данными уже зарегистрированного")
    public void createUserWithTheSameDataReturnsCodeForbiddenTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response responseForTheSameUser = userClient.create(user).extract().response();
        assertEquals("Status code isn't FORBIDDEN", SC_FORBIDDEN, responseForTheSameUser.statusCode());
        assertEquals("Value of the 'message' doesn't match with expected one","User already exists", responseForTheSameUser.path("message"));
    }

}
