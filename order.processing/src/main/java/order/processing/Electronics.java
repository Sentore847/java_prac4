package order.processing;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class Electronics extends Product {
    private final String brand;

    public Electronics(String name, String brand) {
        super(name);
        this.brand = brand;
    }
}