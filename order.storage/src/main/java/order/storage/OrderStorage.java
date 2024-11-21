package order.storage;

import lombok.Getter;

import java.util.LinkedList;
import java.util.Queue;

@Getter
public class OrderStorage<T> {
    private final Queue<T> storage = new LinkedList<>();

    public synchronized void addOrder(T order) throws InterruptedException {
        storage.add(order);
        System.out.println("Order added to storage: " + order);
        notifyAll();
    }

    public synchronized T getOrder() throws InterruptedException {
        while (storage.isEmpty()) {
            wait();
        }
        return storage.poll();
    }

}