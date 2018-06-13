package au.com.finder.api.coffee.dataaccess;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.data.Order;
import au.com.finder.api.coffee.data.jooq.enums.CoffeesType;
import au.com.finder.api.coffee.data.jooq.enums.OrdersStatus;
import au.com.finder.api.coffee.data.jooq.tables.Coffees;
import au.com.finder.api.coffee.data.jooq.tables.Orders;
import au.com.finder.api.coffee.data.jooq.tables.records.OrdersRecord;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MySQLDataAccess implements DataAccess {
    @Override
    public List<Coffee> getCoffees() {
        try (DSLContext context = getContext()) {
            return context.selectFrom(Coffees.COFFEES)
                    .fetch()
                    .stream()
                    .map(record -> new Coffee(record.getId(), record.getType().name(), record.getCreated(), record.getUpdated()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Coffee getCoffeeById(int id) {
        try (DSLContext context = getContext()) {
            return context.selectFrom(Coffees.COFFEES)
                    .where(Coffees.COFFEES.ID.eq(id))
                    .fetchAny()
                    .map(record -> new Coffee(record.get(Coffees.COFFEES.ID), record.get(Coffees.COFFEES.TYPE).name(), record.get(Coffees.COFFEES.CREATED), record.get(Coffees.COFFEES.UPDATED)));
        }
    }

    @Override
    public List<Order> getOrders() {
        try (DSLContext context = getContext()) {
            return context.select()
                    .from(Coffees.COFFEES)
                    .join(Orders.ORDERS)
                    .on(Orders.ORDERS.ID.eq(Coffees.COFFEES.ORDERID))
                    .fetch()
                    .intoGroups(Orders.ORDERS, record -> new Coffee(record.get(Coffees.COFFEES.ID), record.get(Coffees.COFFEES.TYPE).name(), record.get(Coffees.COFFEES.CREATED), record.get(Coffees.COFFEES.UPDATED)))
                    .entrySet()
                    .stream()
                    .map(entry -> new Order(entry.getKey().getId(), entry.getValue(), entry.getKey().getStatus().name(), entry.getKey().getCreated(), entry.getKey().getUpdated()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Order getOrderById(int id) {
        try (DSLContext context = getContext()) {
            return context.select()
                    .from(Coffees.COFFEES)
                    .join(Orders.ORDERS)
                    .on(Orders.ORDERS.ID.eq(Coffees.COFFEES.ORDERID))
                    .where(Orders.ORDERS.ID.eq(id))
                    .fetch()
                    .intoGroups(Orders.ORDERS, record -> new Coffee(record.get(Coffees.COFFEES.ID), record.get(Coffees.COFFEES.TYPE).name(), record.get(Coffees.COFFEES.CREATED), record.get(Coffees.COFFEES.UPDATED)))
                    .entrySet()
                    .stream()
                    .map(entry -> new Order(entry.getKey().getId(), entry.getValue(), entry.getKey().getStatus().name(), entry.getKey().getCreated(), entry.getKey().getUpdated()))
                    .findAny()
                    .orElse(null);
        }
    }

    @Override
    public Order saveOrder(Order order) {
        try (DSLContext context = getContext()) {
            context.transaction(configuration -> {
                if (order.getId() == 0) {
                    order.setId(insertOrder(context, order));
                } else {
                    updateOrder(context, order);
                    updateCoffees(context, order);
                }
                insertCoffees(context, order, order.getId());
            });
        }

        return getOrderById(order.getId());
    }

    private int insertOrder(DSLContext context, Order order) {
        return context.insertInto(Orders.ORDERS, Orders.ORDERS.STATUS)
                .values(OrdersStatus.valueOf(order.getStatus()))
                .returning(Orders.ORDERS.ID)
                .fetchOptional()
                .map(OrdersRecord::getId)
                .orElse(0);
    }

    private void updateOrder(DSLContext context, Order order) {
        context.update(Orders.ORDERS)
                .set(Orders.ORDERS.STATUS, OrdersStatus.valueOf(order.getStatus()))
                .set(Orders.ORDERS.UPDATED, new Timestamp(new Date().toInstant().toEpochMilli()))
                .where(Orders.ORDERS.ID.eq(order.getId()))
        .execute();
    }

    private void insertCoffees(DSLContext context, Order order, int id) {
        if (order.getCoffees() != null && order.getCoffees().size() > 0) {
            context.batch(order.getCoffees().stream()
                    .filter(coffee -> coffee.getId() == 0)
                    .map(coffee -> context.insertInto(Coffees.COFFEES, Coffees.COFFEES.TYPE, Coffees.COFFEES.ORDERID).values(CoffeesType.valueOf(coffee.getType()), id))
                    .collect(Collectors.toList()))
            .execute();
        }
    }

    private void updateCoffees(DSLContext context, Order order) {
        if (order.getCoffees() != null && order.getCoffees().size() > 0) {
            context.batch(order.getCoffees().stream()
                    .filter(coffee -> coffee.getId() > 0)
                    .map(coffee -> context.update(Coffees.COFFEES)
                            .set(Coffees.COFFEES.TYPE, CoffeesType.valueOf(coffee.getType()))
                            .set(Coffees.COFFEES.UPDATED, new Timestamp(new Date().toInstant().toEpochMilli()))
                            .where(Coffees.COFFEES.ID.eq(coffee.getId())))
                    .collect(Collectors.toList()))
            .execute();
        }
    }

    protected DSLContext getContext() {
        return DSL.using(System.getenv("URL"), System.getenv("USERNAME"), System.getenv("PASSWORD"));
    }
}
