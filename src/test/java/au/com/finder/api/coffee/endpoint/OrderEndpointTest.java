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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static au.com.finder.api.coffee.endpoint.BaseEndpointTest.*;

public class OrderEndpointTest {
    private static final SaveOrderEndpoint saveOrderEndpoint = new SaveOrderEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    private static final OrderEndpoint orderEndpoint = new OrderEndpoint() {
        @Override
        protected Injector getInjector() {
            return INJECTOR;
        }
    };

    @Test
    public void testFetchOrderById() throws IOException {
        String id;
        Order order = new Order();
        order.setStatus("preparing");
        order.setCoffees(IntStream.range(0, 3).mapToObj(i -> {
            Coffee coffee = new Coffee();
            coffee.setType("flatwhite");
            return coffee;
        }).collect(Collectors.toList()));
        String body = StringEscapeUtils.escapeJava(MAPPER.writeValueAsString(order));
        try (ByteArrayInputStream input = new ByteArrayInputStream(("{\"body\": \"" + body + "\"}").getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            saveOrderEndpoint.handleRequest(input, output, null);
            id = Integer.toString(MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Order.class).getId());
        }

        try (ByteArrayInputStream input = new ByteArrayInputStream(String.format("{\"pathParameters\": {\"id\": \"%s\"}}", id).getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            orderEndpoint.handleRequest(input, output, null);
            order = MAPPER.readValue(MAPPER.readValue(output.toByteArray(), Response.class).getBody(), Order.class);
            assertNotNull("Order should not be null", order);
            assertEquals("Id should be equal to what was saved", id, Integer.toString(order.getId()));
            assertNotNull("Coffees should not be null", order.getCoffees());
            assertEquals("There should be three coffees", 3, order.getCoffees().size());
            assertNotNull("Order should have created date", order.getCreated());
        }
    }

    @Test
    public void testFetchOrderByInvalidId() throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(String.format("{\"pathParameters\": {\"id\": \"%s\"}}", -1).getBytes());
             ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            orderEndpoint.handleRequest(input, output, null);
            assertNotNull("Response should be null", MAPPER.readValue(output.toByteArray(), Response.class).getBody());
        }
    }
}
