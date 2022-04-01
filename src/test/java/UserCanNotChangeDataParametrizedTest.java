import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class UserCanNotChangeDataParametrizedTest {

    @After
    public void tearDown() {
        UserClient userClient = new UserClient();
        userClient.deleteUser();
    }

    private String userNewProfileData;

    public UserCanNotChangeDataParametrizedTest(String userNewProfileData) {
        this.userNewProfileData = userNewProfileData;
    }

    public static Faker faker = new Faker();

    @Parameterized.Parameters
    public static Object[][] getParamsForNewUserProfile() {
        return new Object[][]{
                {"{ \"email\" : \"" + faker.internet().emailAddress() + "\"}"},
                {"{ \"password\" : \"" + faker.internet().password() + "\"}"},
                {"{ \"name\" : \"" + faker.name().name() + "\"}"}
        };
    }

    @Test
    public void changeUserProfileDataNotAuthorizedUserReturnsCodeUnauthorizedTest() {
        User user = User.getRandomUser();
        UserClient userClient = new UserClient();
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        userClient.changeUserProfileData(userNewProfileData, false)
                .assertThat()
                .statusCode(SC_UNAUTHORIZED)
                .and()
                .body("success", is(false));
    }

}
