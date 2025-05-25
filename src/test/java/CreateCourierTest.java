import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Courier Creation")
    @Description("Testing a new courier creation")

    public void createCourier() {
        File json = new File("src/test/resources/newCourier.json");
        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier");
        response.then()
                .statusCode(201)
                .and()
                .body("ok", equalTo(true));

        given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409)
                .and()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        String noLogin = "{" +
                "  \"password\": \"123\", " +
                "  \"firstName\": \"Vlad\" " +
                "}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(noLogin)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        String noPassword = "{" +
                "  \"login\": \"Rupasov\", " +
                "  \"firstName\": \"Vlad\" " +
                "}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(noPassword)
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400)
                .and()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

}
