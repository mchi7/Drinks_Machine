import java.math.BigDecimal;

public class CoinHandler {
    private String coinType;
    private BigDecimal value;
    private int quantity;

    public CoinHandler(String coinType, BigDecimal value, int quantity) {
        this.coinType = coinType;
        this.value = value;
        this.quantity = quantity;
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public String getType() {
        return this.coinType;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public void setType(String coinType) {
        this.coinType = coinType;
    }
}
