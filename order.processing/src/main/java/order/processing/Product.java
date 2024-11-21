package order.processing;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public abstract class Product {
    private final String name;

    protected Product(String name) {
        this.name = name;
    }
}