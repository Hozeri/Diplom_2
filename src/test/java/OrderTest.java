import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

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
    @DisplayName("Авторизованный пользователь может сделать заказ")
    public void makeOrderAuthorizedUserReturnsCodeOKTest() {
        userClient.create(user);
        Response response = userClient.login(userCredentials).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response orderResponse = orderClient.createOrderAuth(ingredients).extract().response();
        assertEquals("Status code isn't OK", SC_OK, orderResponse.statusCode());
        assertThat("The order's number is null", orderResponse.path("order.number"), is(notNullValue()));
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может сделать заказ")
    public void makeOrderUnauthorizedUserReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response orderResponse = orderClient.createOrderNotAuth(ingredients).extract().response();
        assertEquals("Status code isn't UNAUTHORIZED", SC_UNAUTHORIZED, orderResponse.statusCode());
        assertFalse(orderResponse.path("success"));
    }

    @Test
    @DisplayName("Невозможно создать заказ без ингредиентов")
    public void makeOrderNoIngredientsReturnsCodeBadRequestTest() {
        userClient.create(user);
        Response response = userClient.login(userCredentials).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response orderResponse = orderClient.createOrderAuth(Ingredients.getBurgerWithoutIngredients()).extract().response();
        assertEquals("Status code isn't BAD_REQUEST", SC_BAD_REQUEST, orderResponse.statusCode());
        assertEquals("Value of the 'message' doesn't match with expected one","Ingredient ids must be provided", orderResponse.path("message"));
    }

    @Test
    @DisplayName("Невозможно создать заказ, отправив некорректный hash ингредиентов")
    public void makeOrderIngredientsWithWrongHashReturnsCodeBadRequestTest() {
        userClient.create(user);
        Response response = userClient.login(userCredentials).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response orderResponse = orderClient.createOrderAuth(Ingredients.getIngredientsWithWrongHash()).extract().response();
        assertEquals("Status code isn't INTERNAL_SERVER_ERROR", SC_INTERNAL_SERVER_ERROR, orderResponse.statusCode());
        assertThat("HTML title doesn't contain 'Error' value", orderResponse.xmlPath(XmlPath.CompatibilityMode.HTML).getString("html.head.title"), is("Error"));
    }

}
