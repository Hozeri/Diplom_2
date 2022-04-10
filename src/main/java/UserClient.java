import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;
import static org.apache.http.HttpStatus.SC_ACCEPTED;

public class UserClient extends RestAssuredClient {

    private static final String USER_PATH = "/auth/";

    @Step("Создание пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .body(user)
                .when()
                .post(USER_PATH + "register")
                .then();
    }

    @Step("Авторизация пользователя")
    public ValidatableResponse login(UserCredentials userCredentials) {
        return given()
                .spec(getBaseSpec())
                .body(userCredentials)
                .when()
                //.log().all()
                .post(USER_PATH + "login")
                .then();
    }

    @Step("Изменение данных авторизованного пользователя")
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

    @Step("Изменение данных неавторизованного пользователя")
    public ValidatableResponse changeUserProfileDataNotAuth(String userProfileData) {
        return given()
                .spec(getBaseSpec())
                //.log().all()
                .body(userProfileData)
                .when()
                .patch(USER_PATH + "user")
                .then();
    }

    @Step("Удаление пользователя")
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

}