import java.util.*;
import java.io.*;
import java.util.concurrent.locks.*;
import javax.script.*;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

//clase que implementa el hilo
class thFuncion implements Runnable {
	private Thread t;
   	private String threadName;
   	private volatile boolean flag;
   	private String funcion;
   	private String operacion;
   	public double resultado;
   	private String numero;
   	public boolean calculado;
   	public Lock lock = new ReentrantLock();
   	public Condition condicion = lock.newCondition();

   	//constructor
   	thFuncion(String nombre, String func, String op, String num) {
   		threadName = nombre;flag = false;
   		resultado = 0;
   		funcion = func;
   		operacion = op;
   		numero = num;
   		calculado = false;
   	}

   	// Corre el hilo, se realizan las funciones
	public void run() {
		while (!flag) {
			// Si no ha realizado los calculos
			if (!calculado) {
				ArrayList<thFuncion> t_funciones = new ArrayList<thFuncion>();
				// Verificar funciones dentro
				int i = 1;
				for (Map.Entry<String,String>entrada:funciones.functions.entrySet()) {
					String key = entrada.getKey();
					String value = entrada.getValue();
					// Si encuentra la función actual
					if (operacion.indexOf(key) != -1) {
						thFuncion subfuncion = new thFuncion("Thread de "+key+" "+i, key, value, numero);
						t_funciones.add(subfuncion);
						i++;
						subfuncion.start();

					}

					// Ir a buscar resultados si es que hay subfunciones
					if (t_funciones.size() != 0) {
						double sub_res = 0;
						Iterator<thFuncion> iterador = t_funciones.iterator();
						// Iterar por subfunciones
						while (iterador.hasNext()) {
							thFuncion f_actual = iterador.next();
							f_actual.lock.lock();
							try {
								// Esperar si no se ha obtenido
								if (!f_actual.calculado) {
									f_actual.condicion.await();
								}
								sub_res = f_actual.resultado;
							}
							catch (InterruptedException exc) {
								System.out.println("Operación interrumpida");
							}
							f_actual.lock.unlock();
							// Reemplazar resultado en la operación
							operacion = operacion.replaceAll(f_actual.funcion.substring(0,1)+"\\(x\\)", String.valueOf(sub_res));
						}
					}
				}

				// Calcular expresión
				Expression exp = new ExpressionBuilder(operacion.replace("x", numero)).build();
				lock.lock();
				// Guardar resultado
				try {
					resultado = exp.evaluate();
					calculado = true;
					// Avisar obtención
					try {
						condicion.signal();
					}
					catch (Exception e) {
						System.out.println("Error avisando"+e);
					}
				}
				// Error en Expression, probablemente division por 0
				catch (Exception e) {
					System.out.println("Error aritmetico.");
					System.exit(-1);
				}
				lock.unlock();
				this.stop();
			}
			// Si terminó operación y hay que esperar
			else {
				try {
					Thread.sleep(50);
				}
				catch (InterruptedException e) {}
			}
		}
	}

	// Correr el thread
	public void start() {
      if (t == null) {
         t = new Thread (this, threadName);
         t.start ();
      }
   	}

   	// Detener el thread
   	public void stop() {
   		flag = true;
   	}
}

//main funciones
class funciones {
	public static HashMap<String,String> functions;
	public static void main(String[] args) {
		int cant_funciones = 0;
		// Leemos el archivo
		BufferedReader file = null;
		try {
			file = new BufferedReader(new FileReader("./funciones.txt"));
		}
		catch (Exception e) {
			System.out.println("Error al abrir el archivo");
			System.exit(-1);
		}
		// Obtenemos cantidad de funciones 
		try {
			cant_funciones = Integer.parseInt(file.readLine());
		}
		catch (IOException e) {
			System.out.println("Error al leer el archivo");
			System.exit(-1);
		}
		//creamos una especie de diccionario para guardar las funciones
		functions = new HashMap<String, String>();
		// Leer funciones
		for (int i=0; i<cant_funciones; i++) {
			String func = "";
			try {
				func = file.readLine();
			}
			catch (IOException e) {
				System.out.println("Error al leer el archivo, es posible que haya mas o menos funciones de las indicadas");
				System.exit(-1);
			}
			if (func == null) {
				break;
			}
			functions.put(func.substring(0,4), func.substring(5).trim());
		}
		System.out.println("Funciones ingresadas!");

		// Iterar peticiones
		while (true) {
			// Recibir input
			System.out.println("Ingrese operación: (para salir presione 's' )");
			Scanner reader = new Scanner(System.in);
			String funcion_ingresada = reader.nextLine().trim();

			// Salir
			if (funcion_ingresada.compareTo("s") == 0) {
				reader.close();
				break;
			}

			// Buscar función recibida en nuestro diccionario, buscamos por la letra
			String letra = funcion_ingresada.substring(0,1);
			if (!functions.containsKey(letra+"(x)")) {
				System.out.println("Función inválida, ingresar nuevamente.");
				continue;
			}
			String num = funcion_ingresada.substring(2, funcion_ingresada.length()-1);
			if (!isNumeric(num)) {
				System.out.println("Función inválida, ingresar nuevamente.");
				continue;
			}
			if (funcion_ingresada.indexOf("(") == -1 || funcion_ingresada.indexOf(")") == -1){
				System.out.println("Función inválida, ingresar nuevamente.");
				continue;
			}

			// Función valida
			String valor = functions.get(letra+"(x)");
			double res = 0;
			//creamos thread
			thFuncion respuesta = new thFuncion("Thread principal", letra+"(x)", valor, num);
			respuesta.start();
			// Buscar resultado
			respuesta.lock.lock();
			try {
				// Esperar si no se ha obtenido
				if (!respuesta.calculado) {
					respuesta.condicion.await();
				}
				res = respuesta.resultado;
			}
			catch (InterruptedException exc) {
				System.out.println("Operación interrumpida");
			}
			respuesta.lock.unlock();


			if ((res % ((int) res)) == 0 || (((int) res) == 0)) {
				System.out.println("El resultado es "+(int) respuesta.resultado);
			}
			else {
				System.out.println("El resultado es "+respuesta.resultado);
			}


		}

		System.out.println("Saliendo.");	

	}

//funcion para saber si es numero
	public static boolean isNumeric(String str) {
  		try  {  
    		double d = Double.parseDouble(str);  
  		}  
  		catch(NumberFormatException nfe)  {  
    		return false;  
  		}  
  		return true;  
	}
}

