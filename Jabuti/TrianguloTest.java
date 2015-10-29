import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class TrianguloTest {
	
		@Before
		public void setUp(){
			System.out.println("Iniciando...");
		}
		
		@Test
		public void testEquilatero() {
			assertEquals(1, Triangulo.triangulo(3,3,3));
		}
		
		@Test
		public void testIsosceles() {
			assertEquals(2, Triangulo.triangulo(3,3,1));
		}
		@Test
		public void testRetangulo() {
			assertEquals(3, Triangulo.triangulo(5,4,3));
		}
		@Test
		public void testAcutangulo() {
			assertEquals(4, Triangulo.triangulo(7,6,5));
		}
		@Test
		public void testObtusangulo() {
			assertEquals(5, Triangulo.triangulo(8,6,5));
		}
		@Test
		public void testOrdemCrescente() {
			assertEquals(-1, Triangulo.triangulo(6,7,4));
		}
		@Test
		public void testNaoFormaTriangulo() {
			assertEquals(0, Triangulo.triangulo(10,3,3));
		}
		
		@After
		public void tearDown(){
			System.out.println("Finalizando...");
		}
}