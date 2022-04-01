import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;

public class OrderTest {

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
    public void makeOrderAuthorizedUserReturnsCodeOKTest() {
        userClient.create(user);
        String token = userClient.login(userCredentials)
                .extract().path("accessToken");
        Token.setAccessToken(token);
        orderClient.createOrder(ingredients, true)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("order.number", is(notNullValue()));
    }

    @Test
    public void makeOrderUnauthorizedUserReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        orderClient.createOrder(ingredients, false)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", is(false));
    }

    @Test
    public void makeOrderNoIngredientsReturnsCodeBadRequestTest() {
        userClient.create(user);
        String token = userClient.login(userCredentials)
                .statusCode(SC_OK)
                .and()
                .extract().path("accessToken");
        Token.setAccessToken(token);
        orderClient.createOrder(Ingredients.getBurgerWithoutIngredients(), true)
                .assertThat()
                .statusCode(SC_BAD_REQUEST)
                .and()
                .body("message", is("Ingredient ids must be provided"));
    }

    @Test
    public void makeOrderIngredientsWithWrongHashReturnsCodeBadRequestTest() {
        userClient.create(user);
        String token = userClient.login(userCredentials)
                .statusCode(SC_OK)
                .and()
                .extract().path("accessToken");
        Token.setAccessToken(token);
        orderClient.createOrder(Ingredients.getIngredientsWithWrongHash(), true)
                .assertThat()
                .statusCode(SC_INTERNAL_SERVER_ERROR)
                .and()
                .body("html.head.title", is("Error"));
    }

}
