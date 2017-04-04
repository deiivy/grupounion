package grupounion;

import grupounion.manager.OperacionesManager;

/**
 * @author David Zapata
 *
 */
public class Main {
	
	public static void main(String [ ] args)
	{
		String registroEntrada = "00:01:07,777-777-777\n" +
								 "00:05:01,666-666-666\n" +
								 "00:05:00,777-777-777";
		
		OperacionesManager op = new OperacionesManager();
		System.out.println("Valor a pagar: " + op.facturar(registroEntrada));
	}
	
}
