package org.example.api.pet;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.example.model.order.Order;
import org.example.model.order.Status;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;
import java.util.Random;

import static io.restassured.RestAssured.given;


public class StoreApiTest {
    public Order order;

    public StoreApiTest() {
        this.order = getOrder();
    }

    private Order getOrder() {
        Order order = new Order();
        order.setId(new Random().nextInt(100));
        order.setPetId(new Random().nextInt(100));
        order.setComplete(new Random().nextBoolean());
        order.setShipDate("2013-09-18T20:40:00.000+0000");
        order.setQuantity(new Random().nextInt(10));
        int randomValue = new Random().nextInt(3);
        Enum[] statuses = {Status.approved, Status.delivered, Status.placed};
        order.setStatus((Status) statuses[randomValue]);
        return order;
    }

    @BeforeClass
    public void prepare() throws IOException {

        // Читаем конфигурационный файл в System.properties -- простейшее HashMap хранилище
        System.getProperties().load(ClassLoader.getSystemResourceAsStream("my.properties"));

        // Здесь мы задаём глобальные преднастройки для каждого запроса. Аналогично можно задавать их
        // перед каждым запросом отдельно, либо создать поле RequestSpecification и задавать весь пакет настроек
        // в конкретных запросах. Подробнее тут: https://habr.com/ru/post/421005/
        RestAssured.requestSpecification = new RequestSpecBuilder()
                .setBaseUri("https://petstore.swagger.io/v2/") // задаём базовый адрес каждого ресурса
                .addHeader("api_key", System.getProperty("api.key")) // задаём заголовок с токеном для авторизации
                // обязательно учитывайте, что любые приватные данные необходимо хранить в отдельных файлах, которые НЕ публикуютя
                // в открытых репозиториях (в закрытых тоже лучше не публиковать)
                .setAccept(ContentType.JSON) // задаём заголовок accept
                .setContentType(ContentType.JSON) // задаём заголовок content-type
                .log(LogDetail.ALL) // дополнительная инструкция полного логгирования для RestAssured
                .build(); // после этой команды происходит формирование стандартной "шапки" запроса.
        // Подробнее о билдерах можно почитать https://refactoring.guru/ru/design-patterns/builder
        // но интереснее в книжке Effective Java

        //Здесь задаётся фильтр, позволяющий выводить содержание ответа,
        // также к нему можно задать условия в параметрах конструктора, которм должен удовлетворять ответ (например код ответа)
        RestAssured.filters(new ResponseLoggingFilter());
    }

    @Test
    public void checkObjectSave() throws InterruptedException {
        given()  // часть стандартного синтаксиса BDD. Означает предварительные данные. Иначе говоря ДАНО:
                .body(order) // указываем что  помещаем в тело запроса. Поскольку у нас подключен Gson, он преобразуется в JSON
                .when()   // КОГДА:
                .post("/store/order") // выполняем запрос методом POST к ресурсу /pet, при этом используется ранее
                // созданная "шапка". Именно в этом методе создаётся "текстовый файл" запроса, он отправляется
                // посредством HTTP к серверу. Затем в этом же методе получается ответ. После этого метода мы
                // работаем с ОТВЕТОМ
                .then() // ТОГДА: (указывает, что после этой части будут выполняться проверки-утверждения)
                .statusCode(200); // например проверка кода ответа.он просто выдёргивается из текста ответа

        Thread.sleep(20000);


        Order actual = given()
                .pathParam("orderId", order.getId()) // заранее задаём переменную petId
                .when()
                .get("/store/order/{orderId}") // которая подставится в путь ресурса перед выполнением запроса.
                // после этого метода мы так же будем иметь уже ОТВЕТ от сервера.
                .then()
                .statusCode(200)
                .extract().body() // у полученного ответа мы можем взять тело
                .as(Order.class); // и распарсить его как объект Pet. Всё это получается автоматически, так как
        // у нас подключена библиотека для работы с JSON и мы дополнительно указали в общей "шапке"
        // что хотим получать и отправлять объекты в формате JSON
        // Здесь сравниваем только имя, поскольку многие поля у наших объектов не совпадают: поскольку
        // мы не задали список тэгов животного, в объекте pet он будет null, тогда как в объекте actual Gson присвоит
        // ему пустой список. Это происходит потому что в ответ всегда приходит полный JSON модели
        // (как описано в Swagger.io), даже если мы отправляли не полную модель.
        // TODO можно переопределить методы equals у объектов Pet и других, чтобы происходило корректное сравнение
        // не заданных полей с пустыми
        Assert.assertEquals(actual.getPetId(), order.getPetId());

    }

    @Test
    public void testDeleteOrder() throws InterruptedException {
        given()
                .pathParam("orderId", order.getId())
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200);

        Thread.sleep(20000);
        given()
                .pathParam("orderId", order.getId())
                .when()
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
}



