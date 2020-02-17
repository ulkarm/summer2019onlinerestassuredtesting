package com.automation.tests.Homework;

import com.automation.utilities.ConfigurationReader;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.List;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Homework1 {

    @BeforeAll
    public static void setup(){
        baseURI = ConfigurationReader.getProperty("uinames.uri");
    }
    @Test
    @DisplayName("no param test")
    public void test1(){
        given().
                get(baseURI).
        then().
               assertThat().statusCode(200).
                            contentType(ContentType.JSON). // didn't understand charset=utf-8 part
                            body("name", not(empty())).
                            body("surname", not(empty())).
                            body("gender", not(empty())).
                            body("region", not(empty())).
                log().all(true);
        // I have tried body ("name", hasSize(greaterThan(0)), it didn't work
    }

    @Test
    @DisplayName("Gender test")
    public void test2(){

        given().
                queryParam("gender", "female").
        when().
                get().prettyPeek().
        then().
                assertThat().statusCode(200).
                             contentType(ContentType.JSON).
                             body("gender", is("female"));
    }
    @Test
    @DisplayName("2 params")
    public void test3(){
        given().
                queryParam("region","azerbaijan").
                queryParam("gender","male").
        when().
               get().prettyPeek().
        then().
               assertThat().statusCode(200).
                            contentType(ContentType.JSON).
                            body("gender",is("male")).
                            body("region", is("Azerbaijan"));
    }

    @Test
    @DisplayName("invalid gender")
    public void test4(){
        given().
                queryParam("gender", "alien").
        when().
                get().prettyPeek().
        then().
                assertThat().statusCode(400).
                body("error",is("Invalid gender"));
    }

    @Test
    @DisplayName("invalid region test")
    public void test5(){
        given().
                queryParam("region","Mars").
        when().
                get().prettyPeek().
        then().
                assertThat().statusCode(400).
                             body("error",is("Region or language not found"));
    }

    @Test // couldn't finish this one on my own
    @DisplayName("Amount and regions")
    public void test6(){
        Response response = given().
                queryParam("region","morocco").
                queryParam("amount", 5).
        when().
                get().prettyPeek();

        response.then().
                assertThat().statusCode(200).
                contentType(ContentType.JSON);


        JsonPath jsonPath = response.jsonPath();

        List<String> names = jsonPath.get("name");

        List<String> surnames = jsonPath.getList("surname");

    }

    @Test
    @DisplayName("3 params")
    public void test7(){
        given().
                queryParam("region","greece").
                queryParam("gender","male").
                queryParam("amount",5).
        when().
                get().prettyPeek().
        then().
                assertThat().statusCode(200).
                             contentType(ContentType.JSON).
                             body("region", everyItem(is("Greece"))).
                             body("gender", everyItem(is("male")));
    }

    @Test
    @DisplayName("amount count")
    public void test8(){

        Response response = given().
                queryParam("amount", 3).
        when().
                get().prettyPeek();

        response.then().
               assertThat().statusCode(200).
               contentType(ContentType.JSON);

        List<?> amount = response.jsonPath().getList("");

        assertEquals(3,amount.size());

    }

}
