package task2;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class OrderHelpers {

    @Step("Получение данных о доступных ингридиентов")
    public Response getIngredients() {
        Response response =
                given()
                        .contentType("application/json")
                        .when()
                        .get("/api/ingredients");
        return response;
    }

    @Step("Создание заказа")
    public Response createOrder(String ingredients, String accessToken) {
        OrderParameter orderParameter = new OrderParameter(ingredients);
        RequestSpecification req =
                given()
                        .contentType("application/json");
        if (accessToken != null) {
            req.header("Authorization", accessToken);
        }
        return req.body(orderParameter)
                .when()
                .post("api/orders");
    }

    @Step("Получение списка заказов пользователя")
    public Response getOrdersUser(String accessToken) {
        RequestSpecification req =
                given()
                        .contentType("application/json");
        if (accessToken != null) {
            req.header("Authorization", accessToken);
        }
        return req
                .when()
                .get("api/orders");
    }

}