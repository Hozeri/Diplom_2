import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class UserCanChangeDataParametrizedTest {

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

    private final String userNewProfileData;

    public UserCanChangeDataParametrizedTest(String userNewProfileData) {
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
    @DisplayName("Авторизованный пользователь может изменить данные в своём профиле")
    public void changeUserProfileDataAuthorizedUserReturnsCodeOKTest() {
        UserCredentials userCredentials = UserCredentials.getUserCredentials(user);
        userClient.create(user);
        Response response = userClient.login(userCredentials).extract().response();
        Token.setAccessToken(response.path("accessToken"));
        Response userProfileResponse = userClient.changeUserProfileDataAuth(userNewProfileData).extract().response();
        assertEquals("Status code isn't OK", SC_OK, userProfileResponse.statusCode());
        assertTrue(userProfileResponse.path("success"));
    }
}
