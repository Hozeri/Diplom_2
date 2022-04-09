import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserClient extends RestAssuredClient {

    private static final String USER_PATH = "/auth/";

    @Step
    public Response create(User user) {
        Response response = given()
                .spec(getBaseSpec())
                //.log().all()
                .body(user)
                .when()
                .post(USER_PATH + "register")
                .then()
                //.log().all()
                .extract().response();
        return response;
    }

    @Step
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                //.log().all()
                .post(USER_PATH + "login")
                .then();
    }

    @Step
    public ValidatableResponse changeUserProfileDataAuth(String userProfileData) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .auth().oauth2(Token.getAccessToken().substring(7))
                .body(userProfileData)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step
    public ValidatableResponse changeUserProfileDataNotAuth(String userProfileData) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .body(userProfileData)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step
    public void deleteUser() {
        given()
                .spec(getBaseSpec())
                //.log().all()
                .auth().oauth2(Token.getAccessToken().substring(7))
                .when()
                .delete(USER_PATH + "user")
                .then()
                .statusCode(SC_ACCEPTED);
        //.log().all();
    }

    public static void setAccessTokenFromResponse(Response response) {
        String token = response.path("accessToken").toString();
        Token.setAccessToken(token);
    }

}