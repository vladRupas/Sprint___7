import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertNotNull;

@RunWith(Parameterized.class)
public class OrderCreationTest {

    private final String[] color;

    public OrderCreationTest(String[] color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Test with color = {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {new String[]{"BLACK"}},
                {new String[]{"GREY"}},
                {new String[]{"BLACK", "GREY"}},
                {new String[]{}}
        };
    }

    @Test
    @DisplayName("Order Creation")
    @Description("Testing a new order creation")

    public void testCreateOrderWithDifferentColors() {
        Order order = new Order(
                "Иван", "Иванов", "ул. Тестовая, 1", "4",
                "+7 800 355 35 35", 5, "2025-06-06", "JUnit 4 заказ", color
        );

        Response response = RestAssured
                .given()
                .baseUri("https://qa-scooter.praktikum-services.ru")
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .post("/api/v1/orders");

        response.then().statusCode(201);
        Integer track = response.jsonPath().getInt("track");
        assertNotNull("Тело ответа содержит 'track'", track);
    }

}
