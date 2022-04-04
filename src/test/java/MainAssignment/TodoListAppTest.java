package MainAssignment;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class TodoListAppTest {
    RequestSpecBuilder requestSpecBuilder;
    RequestSpecification requestSpecification;
    ResponseSpecBuilder responseSpecBuilder;
    ResponseSpecification responseSpecification;
    protected static Logger log;
    PrintStream log1;
    User user;
    ExcelParser excelParser;
    @BeforeTest
    public void setup() throws FileNotFoundException {
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

        log1 = new PrintStream(new File("logFile.log"));

    }

    @Test(priority = 1)
    public void registerUser(){
        ArrayList<String> details = new ArrayList<>();
//        File jsonFile = new File("src/test/resources/Test.json");
        details = excelParser.createJSONAndTextFileFromExcel("src/test/resources/Data.xls", "Register");
        for (String detail: details){
            requestSpecification.body(detail);
            Response response = executePostAndGetResponse("/user/register");
            JSONObject userRequestDetails = new JSONObject(detail);
            try{
                JSONObject userResponseDetails = new JSONObject(response.asString());
                JSONObject userObj = (JSONObject) userResponseDetails.get("user");
                assertThat(userObj.get("email"),is(equalTo(userRequestDetails.get("email"))));
                assertThat(userObj.get("name"),is(equalTo(userRequestDetails.get("name"))));
                assertThat(userObj.get("age"),is(equalTo(userRequestDetails.get("age"))));
                assertThat(response.getStatusCode(),is(equalTo(200)));
            }catch (Exception exception){
                log.error(response.asString());
        assertThat(response.getStatusCode(),is(equalTo(201)));
            }
        }
    }

    @Test(priority = 2)
    public void loginUser(){
        ArrayList<String> loginDetails = new ArrayList<>();
        loginDetails = excelParser.createJSONAndTextFileFromExcel("src/test/resources/Data.xls", "Register");
        for (String detail: loginDetails){
            requestSpecification.body(detail);
            JSONObject userRequestDetails = new JSONObject(detail);
            Response response = executePostAndGetResponse("user/login");
            try{
                JSONObject userResponseDetails = new JSONObject(response.asString());
                user.setToken(userResponseDetails.get("token").toString());
                JSONObject userObj = (JSONObject) userResponseDetails.get("user");
                assertThat(userObj.get("email"),is(equalTo(userRequestDetails.get("email"))));
                assertThat(response.getStatusCode(),is(equalTo(200)));
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
            JSONObject taskRequestDetails = new JSONObject(detail);
            requestSpecification.body(detail);
            Response response = executePostAndGetResponse("/task");
            JSONObject taskResponseDetails = new JSONObject(response.asString());
            JSONObject taskObj = (JSONObject) taskResponseDetails.get("data");
            assertThat(taskObj.get("description").toString().trim(),is(equalTo(taskRequestDetails.get("description"))));
            assertThat(response.getStatusCode(),is(equalTo(201)));
            break;
        }
    }

    @Test(priority = 4)
    public void getTasks(){
        requestSpecification.header("Authorization",
                "Bearer " + user.getToken());
        Response response = executeGetAndGetResponse("/task","limit", "2");
        System.out.println(response.asString());
        response = executeGetAndGetResponse("/task", "limit", "5");
        System.out.println(response.asString());
        response = executeGetAndGetResponse("/task", "limit", "10");
        System.out.println(response.asString());
        assertThat(response.getStatusCode(),is(equalTo(200)));

//        JSONArray array = new JSONArray(response.asString());
    }

    private Response executePostAndGetResponse(String path){
        return given().
                spec(requestSpecification).
                when().
                filter(ResponseLoggingFilter.logResponseTo(log1)).
                post(path).
                then().
                spec(responseSpecification).
                extract().
                response();
    }

    private Response executeGetAndGetResponse(String path, String query, String value){
        return given().
                spec(requestSpecification).
                queryParam(query, value).
                filter(ResponseLoggingFilter.logResponseTo(log1)).
                when().
                get(path).
                then().
                spec(responseSpecification).
                extract().
                response();
    }
}
