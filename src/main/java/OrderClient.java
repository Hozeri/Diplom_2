import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class OrderClient extends RestAssuredClient {

    private static final String ORDER_PATH = "/orders";

    @Step
    public ValidatableResponse createOrder(Ingredients ingredients, boolean isAuthorized) {
        if (isAuthorized){
            return given()
                    .spec(getBaseSpec())
                    //.log().all()
                    .auth().oauth2(Token.getAccessToken().substring(7))
                    .body(ingredients)
                    .when()
                    .post(ORDER_PATH)
                    .then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    //.log().all()
                    .body(ingredients)
                    .when()
                    .post(ORDER_PATH)
                    .then();
        }
    }

    @Step
    public Response getIngredients() {
        Response response = given()
                .spec(getBaseSpec())
                //.log().all()
                .when()
                .get("/ingredients")
                .then()
                .extract().response();
        return response;
    }

    @Step
    public ValidatableResponse getUserOrders(boolean isAuthorized) {
        if (isAuthorized) {
            return given()
                    .spec(getBaseSpec())
                    //.log().all()
                    .auth().oauth2(Token.getAccessToken().substring(7))
                    .when()
                    .get(ORDER_PATH)
                    .then();
        } else {
            return given()
                    .spec(getBaseSpec())
                    //.log().all()
                    .when()
                    .get(ORDER_PATH)
                    .then();
        }
    }

}
