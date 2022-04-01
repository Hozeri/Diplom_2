import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.*;

public class UserCreationTest {

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

    @Test
    public void createUserWithRequiredDataReturnsCodeOKTest() {
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        assertEquals(SC_OK, response.statusCode());
        assertTrue(response.path("success"));
    }

    @Test
    public void createUserWithTheSameDataReturnsCodeForbiddenTest() {
        Response response = userClient.create(user);
        UserClient.setAccessTokenFromResponse(response);
        Response responseForTheSameUser = userClient.create(user);
        assertEquals(SC_FORBIDDEN, responseForTheSameUser.statusCode());
        assertEquals("User already exists", responseForTheSameUser.path("message"));
    }

}
