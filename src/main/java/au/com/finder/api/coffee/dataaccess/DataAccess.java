package au.com.finder.api.coffee.dataaccess;

import au.com.finder.api.coffee.data.Coffee;
import au.com.finder.api.coffee.data.Order;
import com.google.inject.ImplementedBy;

import java.util.List;

@ImplementedBy(MySQLDataAccess.class)
public interface DataAccess {
    List<Coffee> getCoffees();
    Coffee getCoffeeById(int id);
    List<Order> getOrders();
    Order getOrderById(int id);
    Order saveOrder(Order order);
}
