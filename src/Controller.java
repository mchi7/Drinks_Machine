import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Text;


public class Controller implements DrinkInterface, Initializable {
    private Inventory<Drink> inv = new Inventory<Drink>();
    private Inventory<CoinHandler> changeInv = new Inventory<CoinHandler>();

    private Drink coke;
    private Drink pepsi;
    private Drink soda;
    private HashMap<String, Integer> purchases = new HashMap<String, Integer> ();

    private List<TextField> textBoxes = new ArrayList<TextField>(); //for clearing
    private BigDecimal orderTotal = BigDecimal.valueOf(0.0);

    private CoinHandler pennies = new CoinHandler("Pennies", BigDecimal.valueOf(0.01), 100);
    private CoinHandler nickels = new CoinHandler("Nickels", BigDecimal.valueOf(0.05), 5);
    private CoinHandler dimes = new CoinHandler("Dimes", BigDecimal.valueOf(0.1), 10);
    private CoinHandler quarters = new CoinHandler("Quarters", BigDecimal.valueOf(0.25), 25);

    @FXML
    private TextField centsBox;

    @FXML
    private TextField penniesBox;

    @FXML
    private TextField nickleBox;

    @FXML
    private TextField QuarterBox;

    @FXML
    private TextField cokeBox;

    @FXML
    private TextField pepsiBox;

    @FXML
    private TextField SodaBox;

    @FXML
    private Text cokeLabel;

    @FXML
    private Text sodaLabel;

    @FXML
    private Text pepsiLabel;

    @FXML
    private Text orderText;

    @FXML
    private Button drinkBtn;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
         //add drinks with respective names, product ids, prices, and quantities
         coke = new Drink("Coke", 1, BigDecimal.valueOf(0.25));
         pepsi = new Drink("Pepsi", 2, BigDecimal.valueOf(0.36));
         soda = new Drink("Soda", 3, BigDecimal.valueOf(0.45));
         
         inv.addItems(coke, 5);
         inv.addItems(pepsi, 15);
         inv.addItems(soda, 3);
         
         //load labels for coke, pepsi, soda
         loadLabels(5, cokeLabel, coke.getPrice());
         loadLabels(15, pepsiLabel, pepsi.getPrice());
         loadLabels(3, sodaLabel, soda.getPrice());
         

         //keep an array of all textboxes for easier clearing later
         textBoxes.add(cokeBox);
         textBoxes.add(SodaBox);
         textBoxes.add(pepsiBox);
         textBoxes.add(QuarterBox);
         textBoxes.add(centsBox);
         textBoxes.add(nickleBox);
         textBoxes.add(penniesBox);

         //initialize the coins in the machine for change
         changeInv.addItems(pennies, 100);
         changeInv.addItems(nickels, 5);
         changeInv.addItems(dimes, 10);
         changeInv.addItems(quarters, 25);


    }

    @FXML
    public void drinksClicked () {

        if (!textBoxAlert(centsBox) && !textBoxAlert(penniesBox) && !textBoxAlert(nickleBox) && !textBoxAlert(QuarterBox) && !textBoxAlert(cokeBox) && !textBoxAlert(pepsiBox) && !textBoxAlert(SodaBox)) {
            //if not alerted, they are valid numbers (unless they total up to 0 cents)

            //parse coins into integers
            int dimeQuantity = Integer.parseInt(centsBox.getText());
            int pennyQuantity = Integer.parseInt(penniesBox.getText());
            int nickleQuantity = Integer.parseInt(nickleBox.getText());
            int quarterQuantity = Integer.parseInt(QuarterBox.getText());

            Calculator calc = new Calculator();
            
            //calculate the sum of coins entered
            BigDecimal coinTotal = calc.getIndividualCoinTotal(new CoinHandler("Dimes", BigDecimal.valueOf(0.1), dimeQuantity)) 
            .add(calc.getIndividualCoinTotal(new CoinHandler("Pennies", BigDecimal.valueOf(0.01), pennyQuantity)))
            .add(calc.getIndividualCoinTotal(new CoinHandler("Nickles", BigDecimal.valueOf(0.05), nickleQuantity))) 
            .add(calc.getIndividualCoinTotal(new CoinHandler ("Quarters", BigDecimal.valueOf(0.25), quarterQuantity)));

            if (coinTotal.equals(BigDecimal.valueOf(0.0))) { //invalid input
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Error Message");
                alert.setHeaderText("Invalid Number");
                alert.setContentText("Please enter a valid, non-negative number.");
                alert.showAndWait().ifPresent(rs -> {
                    if (rs == ButtonType.OK) {
                        clearAllBoxes(textBoxes);
                    }
                });
            }else{
              //parse drinks into integers because they have entered a valid number
                int cokeQuantity = Integer.parseInt(cokeBox.getText());
                int pepsiQuantity = Integer.parseInt(pepsiBox.getText());
                int sodaQuantity = Integer.parseInt(SodaBox.getText());

                
                if (checkNonZeroes(cokeQuantity, pepsiQuantity, sodaQuantity)) { //if the user enters at minimum one drink, continue with the purchase
                    BigDecimal drinkTotal = calc.getDrinkTotal(coke.getPrice(), cokeQuantity).add(calc.getDrinkTotal(pepsi.getPrice(), pepsiQuantity)) 
                    .add(calc.getDrinkTotal(soda.getPrice(), sodaQuantity));
                    
                    //if we have enough money, continue with purchase, otherwise method throws an alert
                    if (!notEnoughMoneyException(drinkTotal, coinTotal)) {
                            
                        try {
                            //check quantities and then add them to purchases (map of drinks) for receipt
                            if (cokeQuantity > 0) {
                                purchases.put(coke.getName(), cokeQuantity);
                            }
                                
                            if (pepsiQuantity > 0) {
                                purchases.put(pepsi.getName(), pepsiQuantity);
                            }
                                
                            if (sodaQuantity > 0) {
                                purchases.put(soda.getName(), sodaQuantity);
                            }
                            
                            //subtract drinks from inv (if item is sold out, catch exception)
                            inv.subtractItems(coke, cokeQuantity);
                            inv.subtractItems(pepsi, pepsiQuantity);
                            inv.subtractItems(soda, sodaQuantity);

                            addCoinsToInventory(drinkTotal, coinTotal); //add the user's coins into the inventory of coins for the drinks machine
                            HashMap<String, Integer> change = getChange(drinkTotal, coinTotal); //try receiving change first before subtracting drinks
                            
                            alertPurchase(purchases, textBoxes, drinkTotal, change);

                            HashMap<Drink, Integer> temp = inv.getPopulatedMap();
                            reloadLabels(temp, cokeLabel, "Coke");
                            reloadLabels(temp, pepsiLabel, "Pepsi");
                            reloadLabels(temp, sodaLabel, "Soda");

                            //if the machine has no more drinks available, disable the 'get drinks' button
                            if (checkZeroDrinks() == true) {
                                drinkBtn.setDisable(true);
                            }
                            
                        }catch (InsufficientChangeException e) {
                            alertInsufficientChange();    
                            
                        }catch (SoldOutException e) {
                            alertSoldOut();
                        }

                    }

                }

                orderText.setText("0.00");
                

            }
        }

    }

    @FXML
    public void addCoinsToInventory(BigDecimal drinkTotal, BigDecimal coinTotal) {
        //parse the textfields for the coins
        Integer dimesInt = Integer.parseInt(centsBox.getText());
        Integer penniesInt = Integer.parseInt(penniesBox.getText());
        Integer quartersInt = Integer.parseInt(QuarterBox.getText());
        Integer nickelsInt = Integer.parseInt(nickleBox.getText());

        //add the coins to inv
        changeInv.addItems(dimes, dimesInt);
        changeInv.addItems(pennies, penniesInt);
        changeInv.addItems(quarters, quartersInt);
        changeInv.addItems(nickels, nickelsInt);
    }

    @FXML
    public void alertInsufficientChange() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error Message");
        alert.setHeaderText("Error processing request");
        alert.setContentText("Not sufficient change in the inventory");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) { //clear all textboxes on 'ok'
                textBoxes.clear();
            }
        });
    }

    //when a SoldOutException is thrown, an alert is sent out
    @FXML
    public void alertSoldOut () {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Error Message");
        alert.setHeaderText("Error processing request");
        alert.setContentText("Drink is sold out or you are trying to purchase too many drinks. Your purchase cannot be processed.");
        alert.showAndWait().ifPresent(rs -> {
            if (rs == ButtonType.OK) { //clear all textboxes on 'ok'
                cokeBox.clear();
                SodaBox.clear();
                pepsiBox.clear();
            }
        });
    }
    
    //update total by getting drink textboxes (event activated when the user inputs something into the textbox)
    @FXML
    private void updateTotalByBoxes (KeyEvent keyEvent) {
        BigDecimal cokeTotal = BigDecimal.valueOf(0.0);
        BigDecimal pepsiTotal = BigDecimal.valueOf(0.0); 
        BigDecimal sodaTotal = BigDecimal.valueOf(0.0);

        if (!checkIfNum(cokeBox.getText()) || Integer.parseInt(cokeBox.getText()) < 0 || !checkIfNum(pepsiBox.getText()) || Integer.parseInt(pepsiBox.getText()) < 0 
        || !checkIfNum(SodaBox.getText()) || Integer.parseInt(SodaBox.getText()) < 0) {
            
            textBoxAlert(cokeBox);
            orderText.setText("0.00");

        }else {
            int cokeTemp = Integer.parseInt(cokeBox.getText());
            BigDecimal cokeBefore = BigDecimal.valueOf(cokeTemp);

            if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                if (cokeBox.getText().length() >= 1) {
                    int cokeTemp2 = Integer.parseInt(cokeBox.getText());
                    BigDecimal cokeNow = BigDecimal.valueOf(cokeTemp2);

                    BigDecimal prevTotal = cokeBefore.multiply(coke.getPrice());
                    cokeTotal = prevTotal.subtract((cokeBefore.subtract(cokeNow)).multiply(coke.getPrice())); //cokeTotal = prevTotal - ((cokeBefore - cokeNow) * coke.getPrice()) 
                }else{
                    cokeTotal = BigDecimal.valueOf(0.0);
                }
            }else{
                cokeTotal = BigDecimal.valueOf(cokeTemp).multiply(coke.getPrice());
            }

            int pepsiTemp = Integer.parseInt(pepsiBox.getText());
            BigDecimal pepsiBefore = BigDecimal.valueOf(pepsiTemp);

            if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                if (pepsiBox.getText().length() >= 1) {
                    int pepsiTemp2 = Integer.parseInt(pepsiBox.getText());
                    BigDecimal pepsiNow = BigDecimal.valueOf(pepsiTemp2);

                    BigDecimal prevTotal = pepsiBefore.multiply(pepsi.getPrice());
                    pepsiTotal = prevTotal.subtract((pepsiBefore.subtract(pepsiNow)).multiply(pepsi.getPrice()));
                }else{
                    pepsiTotal = BigDecimal.valueOf(0.0);
                }
        
            }else{
                pepsiTotal = BigDecimal.valueOf(pepsiTemp).multiply(pepsi.getPrice());
            }

            int sodaTemp = Integer.parseInt(SodaBox.getText());
            BigDecimal sodaBefore = BigDecimal.valueOf(sodaTemp);

            if (keyEvent.getCode() == KeyCode.BACK_SPACE) {
                if (SodaBox.getText().length() >= 1) {
                    int sodaTemp2 = Integer.parseInt(SodaBox.getText());
                    BigDecimal sodaNow = BigDecimal.valueOf(sodaTemp2);

                    BigDecimal prevTotal = sodaBefore.multiply(soda.getPrice());
                    sodaTotal = prevTotal.subtract((sodaBefore.subtract(sodaNow)).multiply(soda.getPrice()));
                }else{
                    sodaTotal = BigDecimal.valueOf(0.0);
                }
            
            }else{
                sodaTotal = BigDecimal.valueOf(sodaTemp).multiply(soda.getPrice());
            }
            
        
            orderTotal = cokeTotal.add(pepsiTotal).add(sodaTotal);
            orderText.setText(String.valueOf(orderTotal));
        }
    }
    
    @FXML
    //notEnoughMoneyException
    //total = price of all drinks
    //money = the total of the user's input of coins
    //return true if not enough money
    //return false if enough money
    private boolean notEnoughMoneyException (BigDecimal total, BigDecimal money) {
        if (total.compareTo(money) == 1) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText("Error: Processing request");
            alert.setContentText("You do not have enough money to continue the purchase.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) { //clear all textboxes on 'ok'
                    clearAllBoxes(textBoxes);
                }
            });
            return true;
        }
        return false;
         
    }

    @FXML //check if the user selects at minimum one drink
    //return true if the user has entered at minimum one drink
    //return false if the user entered 0,0,0 for all drinks
    private boolean checkNonZeroes(int c, int p, int s) {
        if (c == 0 && p == 0 && s == 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText("Invalid Number");
            alert.setContentText("You need to select at least one drink");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) { //clear all textboxes on 'ok'
                    cokeBox.clear();
                    SodaBox.clear();
                    pepsiBox.clear();
                }
            });
            return false;
        }
        return true;
    }
    
    @FXML
    private void reloadLabels(HashMap<Drink, Integer> map, Text t, String d) {
        String newStr = "";
        Iterator it = map.entrySet().iterator();
        int quantity = 0;
        Drink temp = null;

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            temp = (Drink) pair.getKey();
            if (temp.getName().equalsIgnoreCase(d)) {
                quantity = (Integer) pair.getValue();
                break;
            }
                
        }

        newStr += quantity + " drinks available, " + "Cost: " + temp.getPrice();
        t.setText(newStr);
    }

    //clear all textboxes for a new purchase
    @FXML
    private void clearAllBoxes(List<TextField> list) { 
        for (int i = 0; i < list.size(); i++) {
            list.get(i).clear();
        }
    }

    //get all purchases for the receipt
    @FXML
    public String getPurchases(HashMap<String, Integer> purchase) { 
        String newStr = "";
        Iterator it = purchase.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            if (!it.hasNext()) {
                newStr += pair.getValue() + " " + pair.getKey() + " ";
                break;
            }
            
            newStr += pair.getValue() + " " + pair.getKey() + ", ";
        }

        return newStr;

    }

    @FXML
    public void alertPurchase (HashMap<String, Integer> purchaseList, List<TextField> list, BigDecimal drinkTotal, HashMap<String, Integer> change) { //when purchases are completed
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Your Purchase");
        alert.setHeaderText("Thank you for your purchase");
        alert.setContentText("Your order was: " + getPurchases(purchaseList) + "Total: $" + drinkTotal + ". Your change is: " + getPurchases(change));
        alert.showAndWait().ifPresent(rs -> {
        if (rs == ButtonType.OK) { //clear all textboxes on 'ok'
            clearAllBoxes(list);
            purchaseList.clear(); //clear receipt for the next purchase
        }
        });
    }

    @FXML
    public boolean textBoxAlert(TextField t) { //invalid input handling -> alert
        String str = t.getText();
        
        boolean check = checkIfNum(str); //check if string is numeric
        int input = 0;

        if (check == true) {
            input = Integer.parseInt(str);
        }

        if (input < 0 || check == false) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Error Message");
            alert.setHeaderText("Invalid Number");
            alert.setContentText("Please enter a valid, non-negative number.");
            alert.showAndWait().ifPresent(rs -> {
                if (rs == ButtonType.OK) {
                    
                }
            });
                
            return true;
        }

        return false;
        
    }

    //check if string contains all numbers
    @FXML
    private static boolean checkIfNum(String str) {
        if (str == null) {
            return false;
        }

        try {
            int d = Integer.parseInt(str);
        }catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    @FXML
    private void loadLabels(int quantity, Text t, BigDecimal bigDecimal) {
        String newStr = quantity + " " + t.getText() + " " + bigDecimal;
        t.setText(newStr);
    }

    //check if there are any available drinks in the machine, if drinks are all out of stock
    //return true if no more drinks in the machine
    //return false if there are still drinks in the machine
    @FXML
    public boolean checkZeroDrinks() {
        Iterator it = inv.getPopulatedMap().entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            
            if ((Integer)pair.getValue() > 0) return false;
        }

        return true;
    }

    @FXML
    public HashMap<String, Integer> getChange(BigDecimal drinkTotal, BigDecimal coinTotal) {
        HashMap<String, Integer> change = new HashMap<String, Integer>(); //map of all our coins with their respective quantities

        //BigDecimal parsing for values of our coins and difference between drinkTotal and coinTotal
        BigDecimal totalDifference = coinTotal.subtract(drinkTotal); 
        BigDecimal quartersVal = quarters.getValue();
        BigDecimal dimesVal = dimes.getValue();
        BigDecimal penniesVal = pennies.getValue();
        BigDecimal nickelsVal = nickels.getValue();

        //calculate the change needed to be returned
        while (totalDifference.compareTo(BigDecimal.valueOf(0.00)) == 1) { //while totalDifference > 0.00
            if ((totalDifference.compareTo(quartersVal) == 0 || totalDifference.compareTo(quartersVal) == 1) && changeInv.containsObj(quarters)) {
                totalDifference = totalDifference.subtract(quartersVal);
                if (change.containsKey("Quarters")) {
                    change.put("Quarters", change.get("Quarters") + 1);
                }else{
                    change.put("Quarters", 1);
                }
                changeInv.subtractItems(quarters, 1);

            }else if ((totalDifference.compareTo(dimesVal) == 1 || totalDifference.compareTo(dimesVal) == 0) && changeInv.containsObj(dimes)) {
                totalDifference = totalDifference.subtract(dimesVal);
                if (change.containsKey("Dimes")) {
                    change.put("Dimes", change.get("Dimes") + 1);
                }else{
                    change.put("Dimes", 1);
                }
                changeInv.subtractItems(dimes, 1);

            }else if ((totalDifference.compareTo(nickelsVal) == 0 || totalDifference.compareTo(nickelsVal) == 1) && changeInv.containsObj(nickels)) {
                totalDifference = totalDifference.subtract(nickelsVal);
                if (change.containsKey("Nickels")) {
                    change.put("Nickels", change.get("Nickels") + 1);
                }else{
                    change.put("Nickels", 1);
                }
                changeInv.subtractItems(nickels, 1);

            }else if ((totalDifference.compareTo(penniesVal) == 0 || totalDifference.compareTo(penniesVal) == 1) && changeInv.containsObj(pennies)) {
                totalDifference = totalDifference.subtract(penniesVal);
                if (change.containsKey("Pennies")) {
                    change.put("Pennies", change.get("Pennies") + 1);
                }else{
                    change.put("Pennies", 1);
                }
                changeInv.subtractItems(pennies, 1);
            
            }else{
                //clean up inventory
                change.clear();
                

                throw new InsufficientChangeException("Not sufficient change in the inventory");
            }
        }
        return change;
    }

    
}
