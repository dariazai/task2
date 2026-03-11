package task2;

import base.BaseTest;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginUserTest extends BaseTest {
        static UserCreateHelpers createUser;

        @BeforeAll
        public static void setUp() {
            createUser = new UserCreateHelpers();
        }

        @Description("Логин пользователя. Позитивная проверка")
        @Test
        public void loginTest() {
            createUser.createNewUser()
                    .then()
                    .statusCode(SC_OK)
                    .body("success", equalTo(true));

            createUser.loginUser()
                    .then()
                    .statusCode(SC_OK)
                    .body("success", equalTo(true))
                    .body("user.email", equalTo(UserData.EMAIL))
                    .body("user.name", equalTo(UserData.NAME))
                    .body("accessToken", notNullValue())
                    .body("refreshToken", notNullValue());;
        }

    @Description("Логин пользователя с неверным логином и паролем")
    @Test
    public void loginIncorrectDataTest() {
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        createUser.loginUser("reet@ruut.com", "125478","Masha")
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @AfterEach
    public void afterEach() {
        createUser.deleteUser();
    }
}