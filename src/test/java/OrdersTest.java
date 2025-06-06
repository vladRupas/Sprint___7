import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class OrdersTest {

    @Test
    @DisplayName("Getting Orders")
    @Description("Testing if orders can be obtained and is a non-empty list")

    public void ordersTest() {

        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders")
                .then()
                .assertThat()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", instanceOf(java.util.List.class))
                .body("orders.size()", greaterThan(0));
    }

}
