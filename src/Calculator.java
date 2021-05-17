import java.math.BigDecimal;
import java.util.HashMap;

public class Calculator {
    public BigDecimal getIndividualCoinTotal(CoinHandler d) {
        BigDecimal val = d.getValue();
        return val.multiply(BigDecimal.valueOf(d.getQuantity()));
    }

    //coin total - drink total, (leftover money from purchasing drinks)
    public BigDecimal getLeftOver(BigDecimal cTotal, BigDecimal dTotal) {
        return cTotal.subtract(dTotal);
    }

    public BigDecimal getDrinkTotal(BigDecimal bigDecimal, int quantity) {
        return bigDecimal.multiply(BigDecimal.valueOf(quantity));
    }




}
