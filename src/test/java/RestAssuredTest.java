import io.restassured.RestAssured;
import io.restassured.RestAssured.*;
import io.restassured.matcher.RestAssuredMatchers.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
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
        given().
                when().
                get("https://jsonplaceholder.typicode.com/posts").
                then().
                statusCode(200);
        given().
                when().
                get("https://jsonplaceholder.typicode.com/posts").
                then().body("userId", hasItem(4),
                        "id", hasItem(40));
        Response response = given().
                when().
                get("https://jsonplaceholder.typicode.com/posts").
                then().extract().response();

        JSONArray array = new JSONArray();
        for (int i=0; i<array.length();i++){
            Boolean test = array.getJSONObject(i).has("title");
            assertThat(String.valueOf(test), true);
            array.getJSONObject(i).get("title");
        }
    }

    @Test
    public void PutCallTest(){
        RestAssured.useRelaxedHTTPSValidation();
        File jsonFile = new File("src/test/resources/postData.json");
        given().
            baseUri("https://reqres.in/api").
            body(jsonFile).
            header("Content-Type", "application/json").
        when().
            put("/users").
        then().
            statusCode(200);

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
    }
}
