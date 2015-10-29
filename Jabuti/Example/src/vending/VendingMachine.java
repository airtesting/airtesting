/*  Copyright 2003  Auri Marcelo Rizzo Vicenzi, Marcio Eduardo Delamaro, 			    Jose Carlos Maldonado

    This file is part of Jabuti.

    Jabuti is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as 
    published by the Free Software Foundation, either version 3 of the      
    License, or (at your option) any later version.

    Jabuti is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with Jabuti.  If not, see <http://www.gnu.org/licenses/>.
*/


package br.jabuti.example.vending;

public class VendingMachine {

    final private int COIN = 25;
    final private int VALUE = 50;
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
