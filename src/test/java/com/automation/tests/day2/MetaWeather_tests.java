package com.automation.tests.day2;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class MetaWeather_tests {

    private String baseURI = "https://www.metaweather.com/api/";

    /*
    /api/location/search/?query=san
    /api/location/search/?query=london
    /api/location/search/?lattlong=36.96,-122.02
    /api/location/search/?lattlong=50.068,-5.316 //anything after ? is query
    /api/location/(woeid)/                       //anything after / is path
     * /api/location/44418/ }/
     * "title": "San Francisco",
     * "location_type": "City",
     * "woeid": 2487956,
     * "latt_long": "37.777119, -122.41964"
     * },
     */
    @Test
    public void test1(){
        Response response = given().
                baseUri(baseURI+"location/search").
                queryParam("query","denver").
                get();

        assertEquals(200, response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test
    public void test2(){
        // /users/100/ - 100 it's a path parameter
        // /users/255/ - 155 it's a path parameter
        // /users/255?name=James | name - query parameter key=value , key it's a query parameter
        // "woeid": 2514815, this woeid stands for 	Where On Earth ID, based on this value
        //  we can get weather info in specific place, woeid is shown when you run test1()
        Response response = given().pathParam("woeid","2391279").
                get(baseURI+"location/{woeid}");

        System.out.println(response.prettyPrint());
    }
}
