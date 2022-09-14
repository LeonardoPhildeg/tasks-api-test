package leonardophildeg.tasks.apitest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.LocalDate;

public class APITest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://localhost:8002/tasks-backend";
    }

    @Test
    public void shouldReturnTasks() {
        RestAssured.given()
            .when()
                .get("/todo")
            .then()
                .statusCode(200);
    }

    @Test
    public void shouldAddTask() {
        LocalDate now = LocalDate.now();
        String json = String.format("{\"task\": \"API test\", \"dueDate\": \"%s\"}", now);

        RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
            .when()
                .post("/todo")
            .then()
                .statusCode(201);
    }

    @Test
    public void shouldNotAddTaskDayBefore() {
        LocalDate now = LocalDate.now().minusDays(1L);
        String json = String.format("{\"task\": \"API test\", \"dueDate\": \"%s\"}", now);

        RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
            .when()
                .post("/todo")
            .then()
                .statusCode(400)
                .body("message", Matchers.is("Due date must not be in past"));
    }

    @Test
    public void shouldNotAddTaskWithoutDescription() {
        LocalDate now = LocalDate.now();
        String json = String.format("{\"dueDate\": \"%s\"}", now);

        RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
            .when()
                .post("/todo")
            .then()
                .statusCode(400)
                .body("message", Matchers.is("Fill the task description"));
    }

    @Test
    public void shouldNotAddTaskWithoutDueDate() {
        RestAssured.given()
                .body("{\"task\": \"API test\"}")
                .contentType(ContentType.JSON)
            .when()
                .post("/todo")
            .then()
                .statusCode(400)
                .body("message", Matchers.is("Fill the due date"));
    }

}
