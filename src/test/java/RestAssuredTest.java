import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import org.hamcrest.Matchers.*;

import org.json.*;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class RestAssuredTest {
    @Test
    public void GetCallTest(){
        RestAssured.useRelaxedHTTPSValidation();
        boolean test = false;
        Response response = given().
                when().
                get("https://jsonplaceholder.typicode.com/posts").
                then().
                body(matchesJsonSchemaInClasspath("json_schema_json_placeholder.json")).
                extract().response();

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
    public void PutCallTest(){
        RestAssured.useRelaxedHTTPSValidation();
        File jsonFile = new File("src/test/resources/postData.json");
        Response response = given().
                baseUri("https://reqres.in/api").
                body(jsonFile).
                header("Content-Type", "application/json").
                when().
                put("/users").
                then().extract().response();
        JSONObject obj = new JSONObject(response.asString());
        assertThat(obj.get("name"),is(equalTo("Arun")));
        assertThat(obj.get("job"),is(equalTo("Manager")));
        assertThat(response.getStatusCode(),is(equalTo(200)));
    }
}
