package task2;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class ChangeUserDataHelpers {
    @Step("Изменение данных для авторизованного пользователя")
    public Response changeUserData(String email, String password, String name, String accessToken) {
        UserParameter userParameter = new UserParameter(email, password, name);
        RequestSpecification req = given().contentType("application/json");

        if (accessToken != null) {
            req.header("Authorization", accessToken);
        }

        return req.body(userParameter)
                .when()
                .patch("/api/auth/user");
    }
}