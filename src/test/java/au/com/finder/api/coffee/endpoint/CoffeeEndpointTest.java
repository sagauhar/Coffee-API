package au.com.finder.api.coffee.endpoint;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.data.Response;
import com.google.inject.Injector;
import org.apache.commons.lang3.StringEscapeUtils;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.INJECTOR;
import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.MAPPER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CoffeeEndpointTest {
    private static final SaveOrderEndpoint saveOrderEndpoint = new SaveOrderEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    private static final CoffeeEndpoint coffeeEndpoint = new CoffeeEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    @Test
    public void testFetchCoffeeById() throws IOException {
        Coffee coffee;
        Order order = new Order();
        order.setStatus("preparing");
        order.setCoffees(IntStream.range(0, 3).mapToObj(i -> {
            Coffee coffee1 = new Coffee();
            coffee1.setType("flatwhite");
            return coffee1;
        }).collect(Collectors.toList()));
        String body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(order));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
            order = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Order.class);
        }

        coffee = order.getCoffees().get(0);

        try (ByteArrayInputStream input = new ByteArrayInputStream(String.format("{\"pathParameters\": {\"id\": \"%s\"}}", coffee.getId()).getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            coffeeEndpoint.handleRequest(input, output, null);
            Coffee returnedCoffee = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Coffee.class);
            assertNotNull("Coffee should not be null", returnedCoffee);
            assertEquals("Id should be equal to what was saved", coffee.getId(), returnedCoffee.getId());
            assertNotNull("Coffees should not be null", returnedCoffee.getCreated());
            assertEquals("Coffee type should be flatwhite", "flatwhite", coffee.getType());
        }
    }

    @Test
    public void testFetchCoffeeByInvalidId() throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(String.format("{\"pathParameters\": {\"id\": \"%s\"}}", -1).getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            coffeeEndpoint.handleRequest(input, output, null);
            assertNotNull("Response should be null", MAPPER.readValue(output.toByteArray(), Response.class).getBody());
        }
    }
}
