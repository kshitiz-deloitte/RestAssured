package MiniAssignment2;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.json.JSONArray;
import org.json.JSONObject;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.with;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

public class RestAssuredWithSpecification {
    RequestSpecification requestSpecification;
    RequestSpecBuilder requestSpecBuilder;
    ResponseSpecification responseSpecification;
    ResponseSpecBuilder responseSpecBuilder;
    Response response;
    @BeforeTest
    public void setup(){
        RestAssured.useRelaxedHTTPSValidation();
        responseSpecification = RestAssured.expect().contentType(ContentType.JSON);
        requestSpecBuilder = new RequestSpecBuilder();
        responseSpecBuilder = new ResponseSpecBuilder().
            expectContentType(ContentType.JSON);
        responseSpecification = responseSpecBuilder.build();

    }

    @Test
    public void getCall(){
        boolean test = false;
        requestSpecBuilder.setBaseUri("https://jsonplaceholder.typicode.com/").
            addHeader("ContentType","application/json");
        requestSpecification = RestAssured.with()
                .spec(requestSpecBuilder.build());
        Response response = requestSpecification
                .get("/posts")
                .then()
                .spec(responseSpecification)
                .extract()
                .response();
        requestSpecification.then()
                .body(matchesJsonSchemaInClasspath("json_schema_json_placeholder.json"));
        JSONArray array = new JSONArray(response.asString());
        for (int i=0; i<array.length();i++){
            if (Integer.parseInt(array.getJSONObject(i).get("id").toString())==40){
                if (Integer.parseInt(array.getJSONObject(i).get("userId").toString())==4){
                    test = true;
                }
            }
        }
        Assert.assertTrue(test);
        assertThat(response.getStatusCode(),is(equalTo(200)));
    }

    @Test
    public void putCall(){
        File jsonFile = new File("src/test/resources/postData.json");
        requestSpecBuilder.setBaseUri("https://reqres.in/api");
        requestSpecification = RestAssured.with().spec(requestSpecBuilder.build());
        requestSpecification.header("Content-Type", "application/json");
        requestSpecification.body(jsonFile);
        Response response = given().
                spec(requestSpecification).
                when().
                put("/users").
                then().extract().response();
        JSONObject obj = new JSONObject(response.asString());
        assertThat(obj.get("name"),is(equalTo("Arun")));
        assertThat(obj.get("job"),is(equalTo("Manager")));
        assertThat(response.getStatusCode(),is(equalTo(200)));
    }
}
