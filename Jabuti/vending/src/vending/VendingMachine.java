package vending;

public class VendingMachine {

    final private int COIN = 25;
//    final private int VALUE = 50;
    private int totValue;
    private int currValue;
    private Dispenser d;
	
    public VendingMachine() {
        totValue = 0;
        currValue = 0;
        d = new Dispenser();
    }

    public void insertCoin() {
        currValue += COIN;
        System.out.println("Current value = " + currValue);
    }
	
    public void returnCoin() {
        if (currValue == 0)
            System.err.println("No coins to return");
        else {
            System.out.println("Take your coins");
            currValue = 0;
        }
    }

    public void vendItem(int selection) {
        int expense;

        expense = d.dispense(currValue, selection);
        totValue += expense;
        currValue -= expense;
        System.out.println("Current value = " + currValue);
    }
} // class VendingMachine
