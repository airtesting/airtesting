package vending;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DispenserTestCase {
	protected Dispenser d;

	@Before
	public void setUp() throws Exception {
		d = new Dispenser();
	}

	@Test
	public void testDispenserException() {
		int val;

		new VendingMachine().returnCoin();

		d.setValidSelection( null );
		val = d.dispense( 50, 10 );
		Assert.assertEquals( 0, val );
	}

	@After
	public void tearDown() throws Exception {
		//		DefaultProber.dump();
	}

}
