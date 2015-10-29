package vending;

public class Dispenser {
    final private int MINSEL = 1;
    final private int MAXSEL = 20;
    final private int VAL = 50;

    private int[] availSelectionVals = 
            { 1, 2, 3, 4, 6, 7, 8, 9, 10, 
                11, 12, 13, 14, 15, 16, 17, 19};
	
    public int dispense(int credit, int sel) {
        int val = 0;

        if (credit == 0)
            System.err.println("No coins inserted");
        else if ((sel < MINSEL) || (sel > MAXSEL))
            System.err.println("Wrong selection " + sel);
        else if (!available(sel))
            System.err.println("Selection " + sel + " unavailable");
        else {
            val = VAL;
            if (credit < val) {
                System.err.println("Enter " + (val - credit) + " coins");
                val = 0;
            } else
                System.out.println("Take selection");
        }
        return val;
    }

    private boolean available(int sel) {
        try {
            for (int i = 0; i < availSelectionVals.length; i++)
                if (availSelectionVals[i] == sel) 
                    return true;
        } catch (NullPointerException npe) {
            return false;
        }
        return false;
    }
    
    public void setValidSelection(int[] v) {
        availSelectionVals = v;
    }
} // class Dispenser
