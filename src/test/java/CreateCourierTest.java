import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class CreateCourierTest {

    private final Gson gson = new Gson();
    private final Faker faker = new Faker();

    private String login;
    private String password;
    private String firstName;
    private Integer createdCourierId = null;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Courier Creation")
    @Description("Testing a new courier creation")
    public void createCourier() {

        generateTestData();

        Courier validCourier = new Courier(login, password, firstName);
        String courierJson = gson.toJson(validCourier);

        createCourierStep(courierJson)
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));

        createDuplicateCourierStep(courierJson)
                .then()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        String noLoginJson = gson.toJson(new Courier(null, password, firstName));
        createCourierWithoutLoginStep(noLoginJson)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

        String noPasswordJson = gson.toJson(new Courier(login, null, firstName));
        createCourierWithoutPasswordStep(noPasswordJson)
                .then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Step("Generating test data")
    private void generateTestData() {
        login = faker.name().username();
        password = faker.internet().password();
        firstName = faker.name().firstName();
    }

    @Step("Creating a courier with valid data")
    private Response createCourierStep(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/v1/courier");
    }

    @Step("Attempting to recreate courier")
    private Response createDuplicateCourierStep(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/v1/courier");
    }

    @Step("Creating a courier without login")
    private Response createCourierWithoutLoginStep(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/v1/courier");
    }

    @Step("Creating a courier without password")
    private Response createCourierWithoutPasswordStep(String json) {
        return given()
                .header("Content-type", "application/json")
                .body(json)
                .post("/api/v1/courier");
    }

    @Step("Log in courier and get ID")
    private CourierId loginCourierAndGetId(Login credentials) {
        return given()
                .header("Content-type", "application/json")
                .body(gson.toJson(credentials))
                .post("/api/v1/courier/login")
                .body()
                .as(CourierId.class);
    }

    @Step("Delete courier with id: {id}")
    private void deleteCourierById(int id) {
        given()
                .delete("/api/v1/courier/{id}", id)
                .then()
                .statusCode(200);
    }

    @After
    public void cleanup() {
        if (createdCourierId != null) {
            deleteCourierById(createdCourierId);
        }
    }

}
