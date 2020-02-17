package com.automation.tests.day2;

import io.restassured.http.Header;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;

public class ORDS_Tests {

    //address of ORDS web service, that is running no AWS ec2.
    //data is coming from SQL Oracle data base to this web service
    //during back-end testing with SQL developer and JDBC API
    //we were accessing data base directly
    //now, we gonna access web service

    //according to OOP conventions, all instance variable should be private
    //but, if we will make it public, it will not make any difference for us
    //it's just good practice, so later we will not hesitate which keyword to use when it's gonna important

    private String baseURI = "http://ec2-18-212-8-14.compute-1.amazonaws.com:1000/ords/hr";

    @Test // verify that status code is 200
    public void test1(){
        //response is object that stores information of response to our request
        //given() comes from RestAssured, we did static import,that's why
        //we can call it without typing class name
        //we start from given()
        //then we can specify type of request like: get(), put(), delete(), post()
        //and as parameter, we enter resource location (URI)
        //then we are getting response back. that response we can put into Response object
        //from response object, we can retrieve: body, header, status code
        //it works without RestAssured.given() because of static import
        Response response = given().
                get(baseURI+"/employees");
        // will print the body as 1 long line
        System.out.println(response.getBody().asString());

        assertEquals(200, response.getStatusCode());
        // will print the body in user friendly format (like json,key=value)
        System.out.println(response.prettyPrint());

    }

    // Task: get employee with id 100 and verify that response returns status code is 200
    // include header into request, and print body
    @Test
    public void test2(){

        //header stands for meta data
        //usually it's used to include cookies
        //in this example, we are specifying what kind of response type we need
        //because web service can return let's say json or xml
        //when we put header info "Accept", "application/json",we are saying that we need only json as response
        // we are telling server to return us request as json file
        // in this case it doesn't make difference,it returns json anyway,we just did it for practice
        Response response = given().
                header("Accept","application/json").
                get(baseURI+"/employees/100");
        System.out.println(response.prettyPrint());
        int actualStatusCode = response.getStatusCode();
        assertEquals(200,actualStatusCode);

        //get information about response content type, you can retrieve from response object
        System.out.println("what kind of content server sends to you in this response: "+response.getHeader("Content-type"));
    }

    // Task: perform get request to /regions, print body and all headers
    @Test
    public void test3(){
        Response response = given().get(baseURI+"/regions");
        //to make sure that everything good to move forward
        assertEquals(200,response.getStatusCode());

        //there is Header class. to get specific header
        Header header = response.getHeaders().get("content-type");
        // to print all headers one by one
        for(Header h: response.getHeaders()){
            System.out.println(h);
        }
        System.out.println(response.prettyPrint());
    }


}
