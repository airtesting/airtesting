package toyprogram;

public class Triangulo {

	/*teste de commit*/
	public static int triangulo (int a, int b, int c)
	{
		int classe=-2;
		double area,as,bs,cs,s;
		
		
		if(a<b || a<c || b<c){
			/*Nao eh triangulo - Valores Nao estao em ordem decrescente*/
			classe = -1;
		}
		else if((a<b+c) && (b<a+c) && (c<a+b)){
			if((a!=b)&&(b!=c)){
			/*triangulo escaleno*/	
				as=a*a;
				bs=b*b;
				cs=c*c;
				if(as==(bs+cs)){
				/*triangulo retangulo*/
					classe=3;
					area=(b*c)/2;
				}
				else{
					s=(a+b+c)/2;
					area=Math.sqrt(s*((s-a)*((s-b)*(s-c))));
					if(as<(bs+cs)){
					/*triangulo agudo*/			
						classe=4;
					}
					else if (as> (bs+cs)){
					/*triangulo obtuso*/
						classe=5;
					}
				}
			}
			else if((a==b)&&(b==c)){
			/*triangulo equilatero*/
				classe=1;
				area=(a*a)*Math.sqrt(3)/4;
			}	
			else{
			/*triangulo isoceles*/
				classe=2;
				if(a==b || a==c || b==c){
					s = a+b+c/2;
					area=Math.sqrt(s*((s-a)*((s-b)*(s-c))));
				}
			}
		} 
		else {
		/*Nao forma triangulo*/	
			classe=0;
			area=0;
		}
		return classe;
	}
}
