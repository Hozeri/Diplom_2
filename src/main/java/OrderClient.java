import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private static final String ORDER_PATH = "/orders";

    @Step("Создание заказа авторизованным пользователем")
    public ValidatableResponse createOrderAuth(Ingredients ingredients) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .auth().oauth2(Token.getAccessToken().substring(7))
                .body(ingredients)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Создание заказа неавторизованным пользователем")
    public ValidatableResponse createOrderNotAuth(Ingredients ingredients) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .body(ingredients)
                .when()
                .post(ORDER_PATH)
                .then();
    }

    @Step("Получение данных об ингредиентах")
    public ValidatableResponse getIngredients() {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .when()
                .get("/ingredients")
                .then();
    }

    @Step("Получение заказов авторизованного пользователя")
    public ValidatableResponse getUserOrdersAuth() {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .auth().oauth2(Token.getAccessToken().substring(7))
                .when()
                .get(ORDER_PATH)
                .then();
    }

    @Step("Получение заказов неавторизованного пользователя")
    public ValidatableResponse getUserOrdersNotAuth() {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .when()
                .get(ORDER_PATH)
                .then();
    }

}
