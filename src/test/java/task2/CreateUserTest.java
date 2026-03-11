package task2;

import base.BaseTest;
import io.qameta.allure.Description;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateUserTest extends BaseTest {
    static UserCreateHelpers createUser;
    private boolean userCreated;

    @BeforeAll
    public static void setUp() {
        createUser = new UserCreateHelpers();
    }

    @Description("Создание пользователя. Позитивная проверка")
    @Test
    public void createNewUserTest() {
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(UserData.EMAIL))
                .body("user.name", equalTo(UserData.NAME))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());
        userCreated = true;
    }

    @Description("Попытка создания пользователя с неполными данными")
    @ParameterizedTest
    @MethodSource("provider")
    public void createUserNotAllFieldsTransmittedTest(String email, String password, String login) {
        createUser.createNewUser(email, password, login)
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", equalTo(false),
                        "message", equalTo("Email, password and name are required fields"));
        userCreated = false;
    }

    @Description("Попытка создания юзера уже с существующими данными")
    @Test
    public void creatingUserWithExistingLoginTest() {
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK);
        createUser.createNewUser()
                .then()
                .statusCode(SC_FORBIDDEN)
                .body("success", Matchers.equalTo(false),
                        "message", equalTo("User already exists"));
        userCreated = true;
    }

    private static Stream<Arguments> provider() {
        return Stream.of(
                Arguments.of(UserData.EMAIL, null, UserData.NAME),
                Arguments.of(null, UserData.PASSWORD, UserData.NAME),
                Arguments.of(UserData.EMAIL, UserData.PASSWORD, null));
    }

    @AfterEach
    public void afterEach() {
        if (userCreated) {
            createUser.deleteUser();
        }
    }
}