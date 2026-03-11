package task2;

import base.BaseTest;
import io.qameta.allure.Description;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_UNAUTHORIZED;
import static org.hamcrest.Matchers.equalTo;

public class ChangeDataUserTest extends BaseTest {
    static UserCreateHelpers createUser;
    static ChangeUserDataHelpers changeUser;
    String accessToken;

    @BeforeEach
    public  void setUp() {
        changeUser = new ChangeUserDataHelpers();
        createUser = new UserCreateHelpers();
    }

    @Description("Изменение данных пользователя с авторизацией. Позитивная проверка")
    @Test
    public void changeParameterUserTest() {
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        accessToken = createUser.authUser()
                .jsonPath()
                .getString("accessToken");
        changeUser.changeUserData(UserData.CH_EMAIL, UserData.CH_PASSWORD, UserData.CH_NAME, accessToken)
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("user.email", equalTo(UserData.CH_EMAIL))
                .body("user.name", equalTo(UserData.CH_NAME));
    }

    @Description("Изменение данных пользователя без авторизации.")
    @Test
    public void changeParameterNoAuthUserTest() {
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        accessToken = createUser.authUser()
                .jsonPath()
                .getString("accessToken");
        changeUser.changeUserData(UserData.CH_EMAIL, UserData.CH_PASSWORD, UserData.CH_NAME, null)
                .then()
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @AfterEach
    public void afterEach() {
        createUser.deleteUserNoToken(accessToken);
    }
}
