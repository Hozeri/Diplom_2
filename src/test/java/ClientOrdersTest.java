import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ClientOrdersTest {
    private User user;
    private UserCredentials userCredentials;
    private UserClient userClient;
    private OrderClient orderClient;

    @Before
    public void setUp() {
        user = User.getRandomUser();
        userCredentials = UserCredentials.getUserCredentials(user);
        userClient = new UserClient();
        orderClient = new OrderClient();
    }

    @After
    public void tearDown() {
            userClient.deleteUser();
    }

    @Test
    @DisplayName("Авторизованный пользователь может получить свои заказы")
    public void getClientOrdersAuthorizedUserReturnsCodeOKTest() {
        userClient.create(user);
        Response response = userClient.login(userCredentials).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response userOrdersResponse = orderClient.getUserOrdersAuth().extract().response();
        assertEquals("Status code isn't OK", SC_OK, userOrdersResponse.statusCode());
        assertTrue(userOrdersResponse.path("success"));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить свои заказы")
    public void getClientOrdersUnauthorizedUserReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response userOrdersResponse = orderClient.getUserOrdersNotAuth().extract().response();
        assertEquals("Status code isn't UNAUTHORIZED", SC_UNAUTHORIZED, userOrdersResponse.statusCode());
        assertEquals("Value of the 'message' doesn't match with expected one","You should be authorised", userOrdersResponse.path("message"));
    }

}
