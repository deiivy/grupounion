package grupounion.manager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David Zapata
 *
 */
public class OperacionesManager {

	// Se declara map donde se guardará el acumulado de los tiempos de llamada a cada número
	// donde la key será el número y el value el total de tiempo
	Map<String, Integer> totalTiempos;
	// Se declara map donde se guardará el acumulado del valor por las llamadas a cada número
	// donde la key será el número y el value el valor acumulado
	Map<String, Integer> totalValores;
	// Se declara map donde se guardará el total de llamadas realizadas a cada número
	// donde la key será el número y el value el total de llamadas
	Map<String, Integer> totalLlamadas;
	
	/**
	 * Método encargado de facturar
	 * @param registroEntrada
	 * @return
	 */
	public Integer facturar(String registroEntrada){
		totalTiempos = new HashMap<String, Integer>();
		totalValores = new HashMap<String, Integer>();
		totalLlamadas = new HashMap<String, Integer>();
		
		// Se separa cada uno de los registros de llamadas
		String[] registros = registroEntrada.split("\n");
		// Se recorre cada uno de los registros
		for (String registro : registros) {
			// Se separa la duracciíon y el número y se envían a la función de clasificación
			String[] infoLlamada = registro.split(",");
			clasificarInfoLlamada(infoLlamada);
		}
		
		// Se retorna el valor real a pagar
		return calcularValorFactura();
	}
	
	/**
	 * Método encargado de clasificar la información de cada llamada
	 * @param infoLlamada
	 */
	private void clasificarInfoLlamada(String[] infoLlamada){
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		
		try {
			// Se obtiene el tiempo de la llamada
		    Date tiempo = sdf.parse(infoLlamada[0]);
		    
		    Calendar duracion = Calendar.getInstance();
		    duracion.setTime(tiempo);  
		    
		    // De la duracción se obtiene las horas, minutos, segundos
		    Integer horas = duracion.get(Calendar.HOUR);
		    Integer minutos = duracion.get(Calendar.MINUTE);
		    Integer segundos = duracion.get(Calendar.SECOND);
			
			String numero = infoLlamada[1];
			
			// Se valida si hay horas para pasarlas a minutos
			if(horas > 0){
				minutos += horas * 60;
			}
			
			// Se convierte la duración total de la llamada a segundos
			Integer segundosTotales = (minutos * 60) + segundos;
			Integer valor = 0;
			Integer llamadas = 1;
			
			// En caso de que la cantidad de minutos sea menor a 5 se cobra a 3 pesos cada segundo
			if(minutos < 5){
				valor = segundosTotales * 3; 
			}else{
				// Si la cantidad de minutos es mayor a 5 se valida si tiene al menos un segundo 
				// para agregarle un minutos más (Ej: si es 00:05:01 se cobraría como 6 minutos)
				if(segundos > 0){
					minutos += 1;
				}
				
				// Se cobra cada minutos a 150 pesos
				valor = minutos * 150;
			}
			
			// Se llama el método que lleva el acumulado de cada número
			acumularRegistros(numero, segundosTotales, valor, llamadas);
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Método para llevar el acumulado del tiempo total, valor acumulado
	 * y total de llamadas a cada número
	 * @param numero
	 * @param segundosTotales
	 * @param valor
	 * @param llamadas
	 */
	private void acumularRegistros(String numero, Integer segundosTotales, Integer valor, Integer llamadas){
		// Se valida si el número ya tenía un registro previo para sumarlo con el nuevo valor
		if(totalTiempos.containsKey(numero)){
			segundosTotales += totalTiempos.get(numero);
			valor += totalValores.get(numero);
			llamadas += totalLlamadas.get(numero);
		}
		
		// Se agrega el número con su respectivo valor, en caso de existir previamente
		// se reemplaza con sus valores actualizados
		totalTiempos.put(numero, segundosTotales);
		totalValores.put(numero, valor);
		totalLlamadas.put(numero, llamadas);
	}
	
	/**
	 * Método para calcular el valor real a pagar
	 * @return
	 */
	private Integer calcularValorFactura(){
		// Se inicializa map donde se guardará el registro con mayor tiempo de llamadas
		Map.Entry<String, Integer> maximoRegistro = null;
		Integer valorFactura = 0;
		
		// Se recorre cada registro que contiene el total de tiempos a cada número
		for (Map.Entry<String, Integer> tiempo : totalTiempos.entrySet()) {
			// Si el registro actual es mayor se guarda como el nuevo maximoRegistro
		    if (maximoRegistro == null || tiempo.getValue().compareTo(maximoRegistro.getValue()) > 0){
		    	maximoRegistro = tiempo;
		    	// Si no es mayor se valida si tienen tiempos iguales
		    }else if(tiempo.getValue().compareTo(maximoRegistro.getValue()) == 0){
		    	// En el caso de tener tiempos iguales, se toma el que tenga menor número de llamadas
		    	if(totalLlamadas.get(tiempo.getKey()).compareTo(totalLlamadas.get(maximoRegistro.getKey())) < 0){
		    		maximoRegistro = tiempo;
		    	}
		    }
		}
		
		// Se recorre cada registro que contiene el valor total por las llamadas a cada número
		for (Map.Entry<String, Integer> valor : totalValores.entrySet()) {
			// Para sumar el valor total de la factura se valida que no se tenga en cuenta el número
			// con el mayor tiempo acumulado
			if(valor.getKey() != maximoRegistro.getKey()){
				valorFactura += valor.getValue();
			}
		}
		
		// Se retirna el valor de la factura (Valor real a pagar)
		return valorFactura;
	}
	
	
}
