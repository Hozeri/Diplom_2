import com.github.javafaker.Faker;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

@RunWith(Parameterized.class)
public class UserCanChangeDataParametrizedTest {

    @After
    public void tearDown() {
        UserClient userClient = new UserClient();
        userClient.deleteUser();
    }

    private String userNewProfileData;

    public UserCanChangeDataParametrizedTest(String userNewProfileData) {
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
    public void changeUserProfileDataAuthorizedUserReturnsCodeOKTest() {
        User user = User.getRandomUser();
        UserClient userClient = new UserClient();
        UserCredentials userCredentials = UserCredentials.getUserCredentials(user);
        userClient.create(user);
        String token = userClient.login(userCredentials)
                .statusCode(SC_OK)
                .and()
                .extract().path("accessToken");
        Token.setAccessToken(token);
        userClient.changeUserProfileData(userNewProfileData, true)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
    }
}
