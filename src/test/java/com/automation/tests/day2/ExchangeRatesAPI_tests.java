package com.automation.tests.day2;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class ExchangeRatesAPI_tests {

    private String baseURI = "https://api.openrates.io/";

    @Test
    public void test1(){
        Response response = given().baseUri(baseURI+"latest").get();
        // we can also do it this way: given().get(baseURI+"latest");
        assertEquals(200, response.getStatusCode());
        response.prettyPrint(); // no need to use system.out.print() method, prettyPrint() is print method itself
    }

    @Test // verify that content is json
    public void test2(){
        Response response = given().get(baseURI+"latest");
        assertEquals(200,response.getStatusCode());
        assertEquals("application/json", response.getHeader("content-type"));
        // or this way
        assertEquals("application/json", response.getContentType());
    }

    @Test // get currency exchange rate for dollar. By default it's euro.
    public void test3(){
        Response response = given().get(baseURI+"latest?base=USD");
        //GET https://api.exchangeratesapi.io/latest?base=USD HTTP/1.1
        //base it's a query parameter that will ask web service to change currency from euro
        //to something else
        // or this way
        /*Response response1 = given().
                               baseUri(baseURI).
                               basePath("latest").
                               queryParam("base","USD").get();
          or
                               given().
                               baseUri(baseURI+"latest").
                               queryParam("base","USD").get();

         */
        assertEquals(200,response.getStatusCode());
        System.out.println(response.prettyPrint());
    }

    @Test // verify that response body for latest currency rates contains today's date
    public void test4(){                                   // (2020-01-23 | yyyy-MM-dd)
        Response response = given().
                baseUri(baseURI+"latest").
                queryParam("base","GBP").get();
        assertEquals(200, response.getStatusCode());

        String todaysDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(todaysDate);
        assertTrue(response.getBody().asString().contains(todaysDate));
    }

    // Task: let's get currency exchange rate for year 2000
    //GET https://api.exchangeratesapi.io/history?start_at=2018-01-01&end_at=2018-09-01&base=USD&symbols=ILS,JPY
    @Test
    public void test5(){
        // get from 2000-01-01 to 2000-12-31, based on USD currency only EUR,GBP,JPY currency
        Response response = given().baseUri(baseURI+"history").
                queryParam("start_at","2000-01-01").
                queryParam("end_at", "2000-12-31").
                queryParam("base", "USD").
                queryParam("symbols","EUR,GBP,JPY").
                get();                         // or "symbols", "EUR","GBP","JPY"
        System.out.println(response.prettyPrint());
    }

    /* Task:
     * Given request parameter "base" is "USD"
     * When user sends request to "api.openrates.io"
     * Then response code should be 200
     * And response body must contain ""base": "USD""
     */
    @Test
    public void test6(){
        Response response = given().baseUri(baseURI+"latest").
                queryParam("base","USD").get();

        String body = response.getBody().asString();
        System.out.println(body);
        assertEquals(200, response.getStatusCode());
        assertTrue(body.contains("\"base\":\"USD\""));
    }


}
