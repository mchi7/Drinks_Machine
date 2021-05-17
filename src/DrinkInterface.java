import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

import javafx.scene.control.TextField;

public interface DrinkInterface {
    void drinksClicked(); //handling for 'get drinks' button
    
    //total1: summation of all the user's drink choices together
    //total2: summation of the user's inputted coins
    void addCoinsToInventory(BigDecimal total1, BigDecimal total2);
    
    //parameters: 
    //1. map of purchases (<Item, quantity>),
    //2. list of all textfields so we can clear them when user presses "ok" on alert
    //3. drinkTotal = summation of the user's selected drinks
    //4. the change from getChange(total1,total2), so we can display change
    void alertPurchase(HashMap<String, Integer> purchaseList, List<TextField> list, BigDecimal drinkTotal, HashMap<String, Integer> change);

    
    void alertInsufficientChange();

    void alertSoldOut();

    String getPurchases(HashMap<String, Integer> purchases); //takes in purchases list, prints out all purchases

    boolean checkZeroDrinks(); //if all drinks are sold out -> true

    HashMap<String, Integer> getChange(BigDecimal total1, BigDecimal total2); //return a hash map of coins and their numbers (for change)

}
