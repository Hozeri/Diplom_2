import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@RunWith(Parameterized.class)
public class UserCanNotChangeDataParametrizedTest {

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

    private String userNewProfileData;

    public UserCanNotChangeDataParametrizedTest(String userNewProfileData) {
        this.userNewProfileData = userNewProfileData;
    }

    public static Faker faker = new Faker();

    @Parameterized.Parameters(name = "Данные пользователя: {0}")
    public static Object[][] getParamsForNewUserProfile() {
        return new Object[][]{
                {"{ \"email\" : \"" + faker.internet().emailAddress() + "\"}"},
                {"{ \"password\" : \"" + faker.internet().password() + "\"}"},
                {"{ \"name\" : \"" + faker.name().name() + "\"}"}
        };
    }

    @Test
    @DisplayName("Неавторизованный пользователь не может изменить данные в своём профиле")
    public void changeUserProfileDataNotAuthorizedUserReturnsCodeUnauthorizedTest() {
        Response response = userClient.create(user).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response userProfileResponse = userClient.changeUserProfileDataNotAuth(userNewProfileData).extract().response();
        assertEquals("Status code isn't UNAUTHORIZED", SC_UNAUTHORIZED, userProfileResponse.statusCode());
        assertFalse(userProfileResponse.path("success"));
    }

}
