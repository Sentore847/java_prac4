package order.threads;

import com.github.javafaker.Faker;
import order.processing.*;
import order.storage.OrderStorage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) {
        OrderStorage<Product> storage = new OrderStorage<>();
        OrderProcessor<Product> processor = new OrderProcessor<>(storage.getStorage());
        final boolean[] ordersCompleted = {false};

        ExecutorService executor = Executors.newFixedThreadPool(2);

        try {
            Runnable orderProducer = () -> {
                System.out.println("Producer task started.");
                Faker faker = new Faker();

                try {
                    for (int i = 0; i < 20; i++) {
                        String productName = faker.commerce().productName();
                        String brand = faker.company().name();
                        String size = faker.options().option("S", "M", "L", "XL");

                        Product product;
                        if (i % 2 == 0) {
                            product = Electronics.builder()
                                    .name(productName)
                                    .brand(brand)
                                    .build();
                        } else {
                            product = Clothing.builder()
                                    .name(productName)
                                    .size(size)
                                    .build();
                        }

                        storage.addOrder(product);
                        System.out.println("Order added: " + product.getName());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    synchronized (ordersCompleted) {
                        ordersCompleted[0] = true;
                        ordersCompleted.notifyAll();
                    }
                }
                System.out.println("Producer task finished.");
            };

            Runnable orderConsumer = () -> {
                System.out.println("Consumer task started.");
                while (true) {
                    synchronized (ordersCompleted) {
                        if (ordersCompleted[0] && storage.getStorage().isEmpty()) {
                            break;
                        }
                    }

                    processor.processOrders(order ->
                            System.out.println("Processed order: " + order.getName())
                    );

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                System.out.println("Consumer task finished.");
            };

            System.out.println("Submitting tasks...");
            executor.submit(orderProducer);
            executor.submit(orderConsumer);

        } finally {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                        System.err.println("Executor did not terminate in time.");
                    }
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            System.out.println("Executor has been shut down.");
        }
    }
}
