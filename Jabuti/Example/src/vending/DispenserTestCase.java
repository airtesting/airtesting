package vending;

import org.junit.Before;
import org.junit.Test;

import junit.framework.*;

import br.jabuti.probe.DefaultProber;

public class DispenserTestCase {
	protected Dispenser d;
	
	@Before
	protected void setUp() throws Exception {
		d = new Dispenser();
	}
 
	@Test
    public void testDispenserException() {
    	int val;

    	d.setValidSelection( null );
    	val = d.dispense( 50, 10 );
    	Assert.assertEquals( 0, val );
    }
	
	@After
	public void tearDown() {
		DefaultProber.dump();
	}

}
