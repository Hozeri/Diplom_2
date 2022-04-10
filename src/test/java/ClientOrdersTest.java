import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

public class ClientOrdersTest {
    private User user;
    private UserCredentials userCredentials;
    private UserClient userClient;
    private OrderClient orderClient;
    private Ingredients ingredients;

    @Before
    public void setUp() {
        user = User.getRandomUser();
        userCredentials = UserCredentials.getUserCredentials(user);
        userClient = new UserClient();
        orderClient = new OrderClient();
        ingredients = Ingredients.getRandomIngredientsForBurger();
    }

    @After
    public void tearDown() {
            userClient.deleteUser();
    }

    @Test
    @DisplayName("Авторизованный пользователь может получить свои заказы")
    public void getClientOrdersAuthorizedUserReturnsCodeOKTest() {
        userClient.create(user);
        String token = userClient.login(userCredentials)
                .extract().path("accessToken");
        Token.setAccessToken(token);
        orderClient.getUserOrders(true)
                //.log().all()
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может получить свои заказы")
    public void getClientOrdersUnauthorizedUserReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        orderClient.getUserOrders(false)
                //.log().all()
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("message", is("You should be authorised"));
    }

}
