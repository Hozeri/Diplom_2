import com.github.javafaker.Faker;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class UserCreationParametrizedTest {

    private final User user;

    public UserCreationParametrizedTest(User user) {
        this.user = user;
    }

    public static Faker faker = new Faker();

    @Parameterized.Parameters
    public static Object[][] getParamsForUser() {
        return new Object[][]{
                {new User(null, faker.internet().password(), faker.name().name())},
                {new User(faker.internet().emailAddress(), null, faker.name().name())},
                {new User(faker.internet().emailAddress(), faker.internet().password(), null)}
        };
    }

    @Test
    public void userWithPartialDataCodeForbiddenTest() {
        UserClient userClient = new UserClient();
        Response response = userClient.create(user);
        assertEquals(SC_FORBIDDEN, response.statusCode());
        assertEquals("Email, password and name are required fields", response.path("message"));
    }
}
