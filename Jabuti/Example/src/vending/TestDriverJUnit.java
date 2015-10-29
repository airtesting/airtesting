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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.junit.Test;

public class TestDriver {

	@Test
    public void testDriver() throws Exception {
		
		//Rafael
		String input = "input1.txt";

        BufferedReader drvInput;
        String tcLine = new String();
        
        String methodName = new String();
        
        VendingMachine machine = new VendingMachine();

        //if (args.length < 1)
          //  drvInput = new BufferedReader(new InputStreamReader(System.in));
        //else
          //  drvInput = new BufferedReader(new FileReader(args[0]));
        drvInput = new BufferedReader(new FileReader(input));

        System.out.println("VendingMachine ON");
        // Machine is ready. Reading input...
        while ((tcLine = drvInput.readLine()) != null) {
            StringTokenizer tcTokens = new StringTokenizer(tcLine);

            if (tcTokens.hasMoreTokens())
                methodName = tcTokens.nextToken();
			
            if (methodName.equals("insertCoin"))
                machine.insertCoin();
            else if (methodName.equals("returnCoin"))
                machine.returnCoin();
            else if (methodName.equals("vendItem")) {
                Integer selection = new Integer(tcTokens.nextToken());

                machine.vendItem(selection.intValue());
            }
        }
        System.out.println("VendingMachine OFF");
    }
}
