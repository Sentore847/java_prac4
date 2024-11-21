package order.processing;

import java.util.Queue;

public class OrderProcessor<T> {
    private final Queue<T> storage;

    public OrderProcessor(Queue<T> storage) {
        this.storage = storage;
    }

    public void processOrders(java.util.function.Consumer<T> consumer) {
        while (!storage.isEmpty()) {
            T order = storage.poll();
            if (order != null) {
                consumer.accept(order);
                System.out.println("Order processed: " + order);
            }
        }
    }
}