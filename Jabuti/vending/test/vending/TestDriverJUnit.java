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


package vending;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.StringTokenizer;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Test;

public class TestDriverJUnit {

	@Test
	public void testInput1() throws Exception {

//		System.out.println(this.getClass().getResource("..\\test\\input1.txt").getPath());

		//Rafael
		InputStream input = this.getClass().getResourceAsStream("/test/input1.txt");
//		String input = "test\\input1.txt";

		BufferedReader drvInput;
		String tcLine = new String();

		String methodName = new String();

		VendingMachine machine = new VendingMachine();

		//if (args.length < 1)
//		drvInput = new BufferedReader(new InputStreamReader(System.in));
		drvInput = new BufferedReader(new InputStreamReader(input));
		//else
		//  drvInput = new BufferedReader(new FileReader(args[0]));
//		drvInput = new BufferedReader(new FileReader(input));

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
				if (tcTokens.hasMoreTokens()){
					Integer selection = new Integer(tcTokens.nextToken());

					machine.vendItem(selection.intValue());}
			}
		}
		System.out.println("VendingMachine OFF");
	}
}
