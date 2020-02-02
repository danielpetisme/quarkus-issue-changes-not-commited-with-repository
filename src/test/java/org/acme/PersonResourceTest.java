package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static javax.ws.rs.core.Response.Status.CREATED;
import static javax.ws.rs.core.Response.Status.OK;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonResourceTest {

    @Inject
    Flyway flyway;

    //https://github.com/quarkusio/quarkus-quickstarts/issues/205
    @BeforeAll
    static void giveMeAMapper() {
        final Jsonb jsonb = JsonbBuilder.create();
        ObjectMapper mapper = new ObjectMapper() {
            @Override
            public Object deserialize(ObjectMapperDeserializationContext context) {
                return jsonb.fromJson(context.getDataToDeserialize().asString(), context.getType());
            }

            @Override
            public Object serialize(ObjectMapperSerializationContext context) {
                return jsonb.toJson(context.getObjectToSerialize());
            }
        };
        RestAssured.objectMapper(mapper);
    }

    @BeforeEach
    public void databaseFixture() {
        flyway.clean();
        flyway.migrate();
    }

    @Test
    @Order(1)
    public void testCreatePerson() {
        Person person = new Person();
        person.name = "John";
        Person created = given()
                .body(person)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/persons")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().body().as(Person.class);

        Person testPerson = get("/api/persons/{id}", created.id).then().extract().body().as(Person.class);
        assertThat(testPerson.name, is("John"));
    }

    @Test
    @Order(2)
    public void testUpdatePerson() throws Exception {

        Person person = new Person();
        person.name = "John";
        Person created = given()
                .body(person)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .post("/api/persons")
                .then()
                .statusCode(CREATED.getStatusCode())
                .extract().body().as(Person.class);

        Person updatedPerson = get("/api/persons/{id}", created.id).then().extract().body().as(Person.class);
        updatedPerson.name = "Bob";

        given()
                .body(updatedPerson)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON)
                .when()
                .put("/api/persons")
                .then()
                .statusCode(OK.getStatusCode());

        Person testPerson = get("/api/persons/{id}", created.id).then().extract().body().as(Person.class);
        assertThat(testPerson.name, is("Bob"));
    }


}