import io.restassured.response.Response;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.hamcrest.CoreMatchers.is;

public class UserLoginTest {

    @Test
    public void loginUserWithExistUserReturnsCodeOKTest() {
        User user = User.getRandomUser();
        UserCredentials userCredentials = UserCredentials.getUserCredentials(user);
        UserClient userClient = new UserClient();
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        userClient.login(userCredentials)
                .assertThat()
                .statusCode(SC_OK)
                .and()
                .body("success", is(true));
        userClient.deleteUser();
    }

}
