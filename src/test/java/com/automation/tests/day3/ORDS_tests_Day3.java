package com.automation.tests.day3;

import com.automation.utilities.ConfigurationReader;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*; //hamcrest makes assertions easier to write

public class ORDS_tests_Day3 {

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI = ConfigurationReader.getProperty("ords.uri");
        // or just like this, because of static import
        // baseURI = ConfigurationReader.getProperty("ords.uri");
    }

    @Test
    public void test1(){
        //accept("application/json") is shortcut for header("Accept", "application/json")
        //we are asking for json as a response
        given().accept("application/json").
                get("/employees").
         then().
                assertThat().statusCode(200).
                and().assertThat().contentType("application/json").
                log().all(true);

        //log().ifError() in case of error will print expected and actual result
        //log().all() will print like PrettyPrint()
    }

    //path parameter - to point on specific resource /employee/:id/ - id it's a path parameter
    //query parameter - to filter results, or describe new resource :
    //POST /users?name=James&age=60&job-title=SDET
    //or to filter GET /employee?name=Jamal get all employees with name Jamal
    @Test
    public void test2(){
        given().
                accept("application/json"). // we want to get response as json file
                pathParam("id", 100).
        when().
                get("/employees/{id}"). //{id} will be replaced with 100 from pathParam value
        then().
                assertThat().statusCode(200).and(). // you can use 'and' or not, doesn't matter
                assertThat().body("employee_id", is(100),// 'and' is just syntax connector
                "department_id",is(90), // "is" comes from hamcrest
                                       "last_name", is("King")). // "is" shortcut for equalTo()
                // we can put multiple parameters in 1 assertThat()
        log().all();
        //body ("phone_number") --> 515.123.4567
        //'is' is coming from ---> import static org.hamcrest.Matchers.*;
        //log().all  Logs everything in the response, including e.g. headers,
        //cookies, body with the option to pretty-print the body if the content-type is
    }

    /* Task:
     * given path parameter is "/regions/{id}"
     * when user makes get request
     * and region id is equals to 1
     * then assert that status code is 200
     * and assert that region name is Europe
     */
    @Test
    public void test3(){
        given().
                accept("application/json").
                pathParam("id",1).
        when().
                get("/regions/{id}").
        then().
                assertThat().statusCode(200).and().
                assertThat().body("region_name", is("Europe")).
                time(lessThan(1L), TimeUnit.SECONDS).//verifies that response time is less than 1 sec
                extract().response().prettyPrint(); //prettyPrint() with system.out. prints body twice
                                                    //prettyPrint() prints only body
                // or log().all()                   //log().all() prints header, status code, body
                // or log().body                    //prints body only
    }

    @Test
    public void test4(){
        JsonPath json = given().
                               accept("application/json").
                        when().
                               get("/employees").
                        thenReturn().
                               jsonPath();
        // groovy is child of Java, behind all groovy methods there is java code
        // we can put groovy and java together,           // groovy syntax
        // items[0] is object, first_name is this object's property
        String nameOfFirstEmployee = json.getString("items[0].first_name");//[0] is 1st index
        String nameOfLastEmployee = json.getString("items[-1].first_name");//[-1] is last index

        System.out.println("First employee name: "+nameOfFirstEmployee);
        System.out.println("Last employee name: "+nameOfLastEmployee);
        System.out.println("==========================================");

        // to get all parameters using Map(because json is key=value,Map is more suitable)
        // if we don't want to specify data type of value we can put "?" -->Map<String,?>
        // or if values are different data types we must put "?", f.e string,integer etc.
        //in JSON, employee looks like object that consists of params and their values
        //we can parse that json object and store in the map.
        Map<String, ?> firstEmployee = json.get("items[0]");
        // or Map<String, String> firstEmployee = json.get("items[0]");
        System.out.println(firstEmployee);
        System.out.println("============================================");

        // we can print it with for each loop for better looking
        // since firstEmployee it's a map (key-value pair, we can iterate through it by using
        // Entry. entry represent one key=value pair)
        // put ? as a value (Map<String, ?>), because there are values of different data
        // type: string, integer, etc..
        // if you put String as value, you might get some casting exception that cannot
        // convert from integer(or something else) to string
        for (Map.Entry<String, ?> entry : firstEmployee.entrySet()) {
            System.out.println("key: " + entry.getKey() + ", value: " + entry.getValue());
        }
        System.out.println("===================================");

        // get and print all last names (we can use List)
        // items it's an object. whenever you need to read some property from the object,
        // you put object.property but, if response has multiple objects,
        // we can get property from every object
        List<String> lastNames = json.get("items.last_name");
        System.out.println("Last names: "+lastNames);

        // we can print it with for each loop for better looking
        for (String str: lastNames){
            System.out.println("Last names: "+str);
        }
    }

    // if we have 1 json object (key and value) we can store it as a Map
    // if we have 1 property (single variable) we can store it as String
    // if we have collection of properties (multiple variables) we can store it as List<String>
    // if we have collection of objects (collection of keys and values) we can store it as
    // List<Map< .. >>, Map represents objects, and List is collection of that objects

    // Task: write a code to get info from /countries as List<Map<String, ?>>
    @Test
    public void test5(){
        JsonPath json = given().
                                accept("application/json").
                         when().
                                get("/countries").prettyPeek().jsonPath();

        List<Map<String , ?>> allCountries = json.get("items");
        System.out.println(allCountries);

        // when we read data from json response, values are not only strings
        // so if we are not sure that all values will have same data type we can put ?
        for (Map<String , ?> map: allCountries){
            System.out.println(map);
        }

        // prettyPrint() returns String
        // prettyPeek() returns response, from that response we can get information about response
        // response object has info about response's body,header,status code,type of schema
        // with prettyPeek() we can get print and continue chaining methods with that response object
        // prettyPrint() - print json/xml/html in nice format and returns string, thus we cannot
        // retrieve jsonPath without extraction...
        // prettyPeek() does same job, but return Response object, and from that object we can
        // get json path.
    }

    // Task:
    // get collection of employee's salaries, then sort it and print
    @Test
    public void test6(){
         List<Integer> salaries = given().
                                accept("application/json").
                         when().
                                get("/employees").
                         thenReturn().jsonPath().get("items.salary");

        Collections.sort(salaries); // sorts in ascending order
        Collections.reverse(salaries); // reverses given collection
        System.out.println(salaries);
    }

    // Task:
    // get collection of phone numbers from employees and replace all dots (.) in every number with dash (-)
    @Test
    public void test7(){
        // phoneNumbers is actually Integer, but we are getting it as String so we can use replaceAll(".","-")
        // List<String> phoneNumbers = ... and p->p.replace()
        // or we can do it this way List<Object> phoneNumbers = .. and use p.toString() method then replace()
        List<Object > phoneNumbers = given().
                                     accept("application/json").
                              when().
                                     get("/employees").
                              thenReturn().
                                      jsonPath().get("items.phone_number");
        // "items.phone_number" calls Gpath (GroovyPath), like Xpath(XMLpath),

        // shortcut for loop, it goes thru collection, p is temporary variable that represents 1 instance from
        // the collection (Predicate Interface), 1st p is iterator, 2nd p is condition
        // replaceAll() replaces each element of this list with the result of applying the operator to that element.
        phoneNumbers.replaceAll(p->p.toString().replace(".","-"));
        System.out.println(phoneNumbers);
    }

    // Task:
    /*  Given accept type as JSON
     *  And path parameter is id with value 1700
     *  When user sends get request to /locations
     *  Then user verifies that status code is 200
            *  And user verifies following json path information:
            *      |location_id|postal_code|city   |state_province|
            *      |1700       |98199      |Seattle|Washington    |
            */
    @Test
    public void test8(){
        Response response = given().
                                    accept(ContentType.JSON).
                                    pathParam("id",1700).
                              when().
                                    get("/locations/{id}");

        response.
                then().
                      assertThat().body("location_id",is(1700)).
                      assertThat().body("postal_code", is("98199")).
                      assertThat().body("city", is("Seattle")).
                      assertThat().body("state_province", is("Washington")).
                      log().body();
    }

}
