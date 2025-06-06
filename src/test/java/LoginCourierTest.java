import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class LoginCourierTest {

    private final Gson gson = new Gson();
    private final Faker faker = new Faker();

    private String login;
    private String password;
    private String firstName;
    private Integer courierId;

    @Before
    public void setup() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Courier Login")
    @Description("Testing if a courier can login and negative cases fail")
    public void loginCourier() {

        generateTestData();
        createTestCourier();

        Login correctLogin = new Login(login, password);
        courierId = loginCourierAndGetId(correctLogin).getId();

        assertSuccessfulLogin(correctLogin, courierId);

        assertLoginFails(new Login("WrongUser", password));
        assertLoginFails(new Login(login, "WrongPass"));
        assertLoginFails(new Login("invalidUser", "invalidPass"));
    }

    @Step("Generating test data")
    private void generateTestData() {
        login = faker.name().username();
        password = faker.internet().password();
        firstName = faker.name().firstName();
    }

    @Step("Creating test courier")
    private void createTestCourier() {
        Courier courier = new Courier(login, password, firstName);
        String json = gson.toJson(courier);

        given()
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

    @Step("Assert successful login for courier with id: {id}")
    private void assertSuccessfulLogin(Login credentials, int id) {
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(credentials))
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", equalTo(id));
    }

    @Step("Assert failed login for login: {credentials.login}")
    private void assertLoginFails(Login credentials) {
        given()
                .header("Content-type", "application/json")
                .body(gson.toJson(credentials))
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404)
                .body("message", equalTo("Учетная запись не найдена"));
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
        if (courierId != null) {
            deleteCourierById(courierId);
        }
    }

}
