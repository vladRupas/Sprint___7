import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Courier Login")
    @Description("Testing if a courier can login")

    public void loginCourier() {

        File json = new File("src/test/resources/loginCourier.json");

        CourierId courierId =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .post("/api/v1/courier/login")
                        .body()
                        .as(CourierId.class);

        Response response =
                given()
                        .header("Content-type", "application/json")
                        .and()
                        .body(json)
                        .when()
                        .post("/api/v1/courier/login");
        response.then()
                .statusCode(200)
                .and()
                .body("id", equalTo(courierId.getId()));

        String wrongLogin = "{" +
                "  \"login\": \"Rupasova\", " +
                "  \"password\": \"123\" " +
                "}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(wrongLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        String wrongPassword = "{" +
                "  \"login\": \"Rupasov\", " +
                "  \"password\": \"124\" " +
                "}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(wrongPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        String wrongCourier = "{" +
                "  \"login\": \"vosapuR\", " +
                "  \"password\": \"321\" " +
                "}";

        given()
                .header("Content-type", "application/json")
                .and()
                .body(wrongCourier)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .and()
                .body("message", equalTo("Учетная запись не найдена"));

        given()
                .delete("/api/v1/courier/{courierId.getId()}", courierId.getId())
                .then().assertThat().statusCode(200);
    }

}
