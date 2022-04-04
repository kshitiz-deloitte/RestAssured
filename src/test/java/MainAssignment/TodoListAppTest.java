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
import java.util.ArrayList;

import static io.restassured.RestAssured.given;

public class TodoListAppTest {
    RequestSpecBuilder requestSpecBuilder;
    RequestSpecification requestSpecification;
    ResponseSpecBuilder responseSpecBuilder;
    ResponseSpecification responseSpecification;
    protected static Logger log;
    User user;
    ExcelParser excelParser;
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
        excelParser = new ExcelParser();

    }

    @Test(priority = 1)
    public void registerUser(){
        ArrayList<String> details = new ArrayList<>();
//        File jsonFile = new File("src/test/resources/Test.json");
        details = excelParser.createJSONAndTextFileFromExcel("src/test/resources/Data.xls", "Register");
        for (String detail: details){
            requestSpecification.body(detail);
            Response response = executePostAndGetResponse("/user/register");
            try{
                JSONObject userDetails = new JSONObject(response.asString());
                System.out.println(userDetails.get("token"));
            }catch (Exception exception){
                log.error(response.asString());
            }
        }
    }

    @Test(priority = 2)
    public void loginUser(){
        ArrayList<String> loginDetails = new ArrayList<>();
        loginDetails = excelParser.createJSONAndTextFileFromExcel("src/test/resources/Data.xls", "Register");
        for (String detail: loginDetails){
            requestSpecification.body(detail);
            Response response = executePostAndGetResponse("user/login");
            try{
                JSONObject userDetails = new JSONObject(response.asString());
                user.setToken(userDetails.get("token").toString());
            }catch (Exception exception){
                log.error(response.asString());
            }
        }
    }

    @Test(priority = 3)
    public void addTasks(){
        ArrayList<String> taskDetails = new ArrayList<>();
        taskDetails = excelParser.createJSONAndTextFileFromExcel("src/test/resources/Data.xls", "Tasks");
        requestSpecification.header("Authorization",
                "Bearer " + user.getToken());
        for (String detail: taskDetails){
            requestSpecification.body(detail);
            Response response = executePostAndGetResponse("/task");
            System.out.println(response.asString());
        }
    }

    @Test(priority = 4)
    public void getTasks(){
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
                queryParam("").
                when().
                get(path).
                then().
                spec(responseSpecification).
                extract().
                response();
    }
}
