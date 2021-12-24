package org.example.api.pet;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;;
import io.restassured.specification.RequestSpecification;
import org.example.model.order.Order;
import org.example.model.order.Status;


import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Wait;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;


import static io.restassured.RestAssured.given;


public class StoreApiTest {

    private Order order;

    private final static String TIME = "2013-09-18T20:40:00.000+0000";


    public StoreApiTest() {
        this.order = getOrder();
    }

    private Order getOrder() {
        Order order = new Order();
        order.setId(new Random().nextInt(100));
        order.setPetId(new Random().nextInt(100));
        order.setComplete(new Random().nextBoolean());
        order.setShipDate(TIME);
        order.setQuantity(new Random().nextInt(10));
        int randomValue = new Random().nextInt(3);
        Enum[] statuses = {Status.approved, Status.delivered, Status.placed};
        order.setStatus((Status) statuses[randomValue]);
        return order;
    }


    @BeforeClass
    public void prepare() throws IOException {
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/")
                .addHeader("api_key", System.getProperty("api.key"))
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void checkObjectSave() throws InterruptedException {
        given()
                .body(order)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200);

        Thread.sleep(10000);


        Order actual = getRequestSpec()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .extract().body()
                .as(Order.class);

        Assert.assertEquals(actual.getPetId(), order.getPetId());


    }

    @Test
    public void testDeleteOrder() throws InterruptedException {


        getRequestSpec()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);

        Thread.sleep(10000);
        getRequestSpec()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404);
    }

    @Test
    public void checkStoreInventory() {


        Map inventory = given()

                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .extract()
                .body()
                .as(Map.class);


        Assert.assertTrue(inventory.containsKey("sold"));

    }

    public RequestSpecification getRequestSpec() {
        return given().pathParam("orderId", order.getId()).when();
    }


}



