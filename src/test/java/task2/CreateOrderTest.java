package task2;

import base.BaseTest;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CreateOrderTest extends BaseTest {
    static OrderHelpers newOrder;
    static UserCreateHelpers createUser;
    private boolean userCreated;

    @BeforeAll
    public static void setUp() {
        newOrder = new OrderHelpers();
    }

    @Description("Создание заказа с ингридиентами неавторизованным пользователем")
    @Test
    public void createOrderWithIngTest() {
        String ingredient = getFirstIngredient();
        Response responseNewOrder= newOrder.createOrder(ingredient,null);
        responseNewOrder.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order.number", notNullValue());
        userCreated=false;
    }

    @Description("Создание заказа без ингридиентов авторизованным пользователем")
    @Test
    public void createOrderWithoutIngAuthUserTest() {
        createUser = new UserCreateHelpers();
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        String accessToken = createUser.authUser()
                .jsonPath()
                .getString("accessToken");

        Response responseNewOrder= newOrder.createOrder(null,accessToken);
        responseNewOrder.then()
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false))
                .body("message", equalTo("Ingredient ids must be provided"));
        userCreated=true;
    }

    @Description("Создание заказа с ингридиентом авторизованным пользователем")
    @Test
    public void createOrderWithIngAuthUserTest() {
        createUser = new UserCreateHelpers();
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        String accessToken = createUser.authUser()
                .jsonPath()
                .getString("accessToken");

        String ingredient = getFirstIngredient();
        Response responseNewOrder= newOrder.createOrder(ingredient,accessToken);
        responseNewOrder.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("name", notNullValue())
                .body("order", notNullValue());
        userCreated=true;
    }

    @Description("Создание заказа с неверным хэшом ингридиента авторизованным пользователем")
    @Test
    public void createOrderNoValidHashIngAuthUserTest() {
        createUser = new UserCreateHelpers();
        createUser.createNewUser()
                .then()
                .statusCode(SC_OK)
                .body("success", equalTo(true));
        String accessToken = createUser.authUser()
                .jsonPath()
                .getString("accessToken");
        Response responseNewOrder= newOrder.createOrder("firstIngredient",accessToken);
        responseNewOrder.then()
                .statusCode(SC_INTERNAL_SERVER_ERROR);
        userCreated=true;
    }

    private String getFirstIngredient() {
        Response response = newOrder.getIngredients();

        response.then()
                .statusCode(SC_OK)
                .body("success", equalTo(true))
                .body("data", notNullValue());

        return response.jsonPath().getString("data[0]._id");
    }

    @AfterEach
    public void afterEach() {
        if (userCreated) {
            createUser.deleteUser();
        }
    }
}
