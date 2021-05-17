import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Inventory<T> {

    //item in inv can be either: 
    //1. <drinkObj, quantity>  
    //2. <typeOfCoin, quantity>

    private HashMap<T, Integer> map;
    private List<Integer> vals = new ArrayList<Integer>();
    
    public Inventory () {
        map = new HashMap<T, Integer>();
    }

    public void addItems(T item, int quantity) {
        if (map.containsKey(item)) {
            map.put(item, map.get(item) + quantity);
        }else{
            map.put(item, quantity);
        }
        
    }

    public boolean containsObj(T item) {
        if (map.containsKey(item)) {
            return (map.get(item) != 0);
        }

        return false;
    }

    public List<Integer> getValues() {
        if (!map.isEmpty()) {
            for (Integer val : map.values()) {
                vals.add(val);
            }
            return vals;
        }
        return null;
    }

    public void subtractItems (T item, int quantity) {
        if (map.containsKey(item)) {
            if (quantity <= map.get(item)) {
                map.put(item, map.get(item) - quantity);
            }else{
                throw new SoldOutException("You are trying to purchase too many drinks");
            }
           
        }else{
            System.out.println("There are no drinks in stock");
        }
    }

    public HashMap<T, Integer> getPopulatedMap () {
        if (map.isEmpty()) {
            System.out.println("Map has not been populated");
            return null;
        }else{
            return map;
        }
        
    }

    public void clear() {
        map.clear();
    }
}
