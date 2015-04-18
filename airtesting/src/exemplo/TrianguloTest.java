package exemplo;

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
		public void equilatero() {
			assertEquals(1, Triangulo.triangulo(3,3,3));
		}
		@Test
		public void isosceles() {
			assertEquals(2, Triangulo.triangulo(3,3,1));
		}
		@Test
		public void retangulo() {
			assertEquals(3, Triangulo.triangulo(5,4,3));
		}
		@Test
		public void acutangulo() {
			assertEquals(4, Triangulo.triangulo(7,6,5));
		}
		@Test
		public void obtusangulo() {
			assertEquals(5, Triangulo.triangulo(8,6,5));
		}
		@Test
		public void ordemCrescente() {
			assertEquals(-1, Triangulo.triangulo(6,7,4));
		}
		@Test
		public void naoFormaTriangulo() {
			assertEquals(0, Triangulo.triangulo(10,3,3));
		}
		@After
		public void tearDown(){
			System.out.println("Finalizando...");
		}
}