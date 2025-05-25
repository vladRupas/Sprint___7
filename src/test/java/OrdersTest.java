import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

public class OrdersTest {

    @Test
    @DisplayName("Getting Orders")
    @Description("Testing if orders can be obtained")

    public void ordersTest() {

        given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .header("Content-type", "application/json")
                .when()
                .get("api/v1/orders")
                .then()
                .assertThat().body("orders", notNullValue());
    }
}
