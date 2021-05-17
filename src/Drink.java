import java.math.BigDecimal;

public class Drink {
    private String name;
    private int productID;
    private BigDecimal price;

    public Drink(String name, int productID, BigDecimal price) {
        this.name = name;
        this.productID = productID;
        this.price = price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getName() {
        return this.name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public int getProductID() {
        return this.productID;
    }
}
