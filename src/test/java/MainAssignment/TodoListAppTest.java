package MainAssignment;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;

public class TodoListAppTest {
    RequestSpecBuilder requestSpecBuilder;
    RequestSpecification requestSpecification;
    ResponseSpecBuilder responseSpecBuilder;
    ResponseSpecification responseSpecification;
    protected static Logger log;
    User user;
    @BeforeTest
    public void setup(){
        RestAssured.useRelaxedHTTPSValidation();
        requestSpecBuilder = new RequestSpecBuilder();
        requestSpecBuilder.setBaseUri("https://api-nodejs-todolist.herokuapp.com/");
        requestSpecification = RestAssured.with().
                spec(requestSpecBuilder.build());
        requestSpecification.header("Content-Type", "application/json");
        responseSpecBuilder = new ResponseSpecBuilder().
                expectContentType(ContentType.JSON);
        responseSpecification = responseSpecBuilder.build();
        log = LogManager.getLogger(TodoListAppTest.class);
        user = new User();

    }

    @Test(priority = 1)
    public void registerUser(){
        File jsonFile = new File("src/test/resources/Test.json");
        requestSpecification.body(jsonFile);
        Response response = executePostAndGetResponse("/user/register");
        try{
            JSONObject userDetails = new JSONObject(response.asString());
            System.out.println(userDetails.get("token"));
        }catch (Exception exception){
            log.error(response.asString());
        }
    }

    @Test(priority = 2)
    public void loginUser(){
        File jsonFile = new File("src/test/resources/UserLoginTest.json");
        requestSpecification.body(jsonFile);
        Response response = executePostAndGetResponse("user/login");
        try{
            JSONObject userDetails = new JSONObject(response.asString());
            System.out.println(userDetails.get("token"));
            user.setToken(userDetails.get("token").toString());
        }catch (Exception exception){
            log.error(response.asString());
        }


    }

//    @Test(priority = 3)
//    public void addTasks(){
//        File jsonFile = new File("src/test/resources/taskTest.json");
//        requestSpecification.body(jsonFile);
//        requestSpecification.header("Authorization",
//                "Bearer " + user.getToken());
//        Response response = executePostAndGetResponse("/task");
//        System.out.println(response.asString());
////        JSONArray array = new JSONArray(response.asString());
//    }

    @Test(priority = 4)
    public void getTasks(){
        File jsonFile = new File("src/test/resources/taskTest.json");
        requestSpecification.body(jsonFile);
        requestSpecification.header("Authorization",
                "Bearer " + user.getToken());
        requestSpecBuilder.addQueryParam("limit", "2");
        requestSpecification.spec(requestSpecBuilder.build());
        Response response = executeGetAndGetResponse("/task");
        System.out.println(response.asString());
        requestSpecBuilder.addQueryParam("limit", "5");
        requestSpecification.spec(requestSpecBuilder.build());
        response = executeGetAndGetResponse("/task");
        System.out.println(response.asString());
        requestSpecBuilder.addQueryParam("limit", "10");
        requestSpecification.spec(requestSpecBuilder.build());
        response = executeGetAndGetResponse("/task");
        System.out.println(response.asString());

//        JSONArray array = new JSONArray(response.asString());
    }

    private Response executePostAndGetResponse(String path){
        return given().
                spec(requestSpecification).
                when().
                post(path).
                then().
                spec(responseSpecification).
                extract().
                response();
    }

    private Response executeGetAndGetResponse(String path){
        return given().
                spec(requestSpecification).
                when().
                get(path).
                then().
                spec(responseSpecification).
                extract().
                response();
    }
}
