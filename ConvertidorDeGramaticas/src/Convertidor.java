import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class Convertidor {
	public static void convertir() {
		ArrayList<SimboloGenerador> generadores=new ArrayList<>();
		String[] terminales=null;
		//Leer la entrada
		try {
			BufferedReader br=new BufferedReader(new FileReader("input.txt"));
			String linea=br.readLine();
			terminales=linea.split(" ");
			while((linea=br.readLine())!=null) {
				String[] lineaDivida=linea.split(" ");
				String simboloGenerador=lineaDivida[0];
				ArrayList<String>producciones=new ArrayList<>();
				for(int i=1; i<lineaDivida.length;i++) {
					producciones.add(lineaDivida[i]);
				}
				generadores.add(new SimboloGenerador(simboloGenerador,producciones));
			}
			br.close();
			if(generadores.isEmpty()) {
				System.out.println("La gramatica no es valida");
				return;
			}
			//Eliminar epsilon producciones
			ArrayList<String>simbolosConEpsilon=new ArrayList<>();
			for(SimboloGenerador s:generadores) {
				if(s.producciones.contains("epsilon")) {
					simbolosConEpsilon.add(s.simbolo);
				}
			}
			for(int i=0; i<generadores.size(); i++) {
				SimboloGenerador simboloActual=generadores.get(i);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					for(String r: simbolosConEpsilon) {
						if(simboloActual.producciones.get(j).contains(r)) {
							String nuevo=generadores.get(i).producciones.get(j).replaceFirst(r, "");
							generadores.get(i).producciones.add(nuevo);
						}
					}
				}
				generadores.get(i).producciones.remove("epsilon");
			}
			
			
			//Eliminar producciones unitarias
			for(int i=0; i<generadores.size(); i++) {
				SimboloGenerador simboloActual=generadores.get(i);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					for(int k=0; k<generadores.size(); k++) {
						SimboloGenerador simboloComparacion=generadores.get(k);
						if(simboloActual.producciones.get(j).equals(simboloComparacion.simbolo)) {
							for(int l=0; l<simboloComparacion.producciones.size(); l++) {
								generadores.get(i).producciones.add(simboloComparacion.producciones.get(l));
							}
							generadores.get(i).producciones.remove(simboloComparacion.simbolo);
						}
					}
				}
					
			}
			//Agregar producciones por cada simbolo terminal
			String reemplazador;
			char alfabeto=generadores.get(generadores.size()-1).simbolo.charAt(0);
			alfabeto++;
			for(int i=0; i<terminales.length; i++) {
				boolean reemplazoEncontrado=false;
				ciclosARomper:
				for(int j=0; j<generadores.size(); j++) {
					SimboloGenerador simboloActual=generadores.get(j);
					for(int k=0; k<simboloActual.producciones.size(); k++) {
						if(simboloActual.producciones.get(k).equals(terminales[i])) {
							reemplazoEncontrado=true;
							reemplazador=simboloActual.simbolo;
							for(int l=0; l<generadores.size(); l++) {
								SimboloGenerador simboloSustitucion=generadores.get(l);
								for(int m=0; m<simboloSustitucion.producciones.size(); m++) {
									String produccionActual=simboloSustitucion.producciones.get(m);
									if(!produccionActual.equals(terminales[i])) {
										produccionActual=produccionActual.replaceAll(terminales[i], reemplazador);
										generadores.get(l).producciones.set(m,produccionActual);
									}
								}
							}
							break ciclosARomper;
						}
						
					}
				}
				if(!reemplazoEncontrado) {
					ArrayList<String>nueva=new ArrayList<>(Arrays.asList(terminales[i]));
					SimboloGenerador nuevo= new SimboloGenerador(String.valueOf(alfabeto), nueva);
					alfabeto++;
					for(int j=0; j<generadores.size(); j++) {
						SimboloGenerador simboloActual=generadores.get(j);
						for(int k=0; k<simboloActual.producciones.size(); k++) {
							String produccionActual=simboloActual.producciones.get(k);
							produccionActual=produccionActual.replaceAll(terminales[i], nuevo.simbolo);
							generadores.get(j).producciones.set(k, produccionActual);
						}
					}
					generadores.add(nuevo);
				}
				
			}
			for(int i=0; i<generadores.size(); i++) {
				SimboloGenerador simboloActual=generadores.get(i);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					if (simboloActual.producciones.get(j).length()>=3) {
						ArrayList<String>nueva=new ArrayList<>(Arrays.asList(simboloActual.producciones.get(j).substring(1)));
						SimboloGenerador nuevo=new SimboloGenerador(String.valueOf(alfabeto),nueva);
						alfabeto++;
						String nuevaProduccion=simboloActual.producciones.get(j).replaceAll(simboloActual.producciones.get(j).substring(1), nuevo.simbolo);
						generadores.get(i).producciones.set(j, nuevaProduccion);
						generadores.add(nuevo);
					}
				}
			}
			System.out.println("Forma Normal de Chomsky");
			for(SimboloGenerador s:generadores) {
				System.out.println(s.toString());
			}
			//Colocar toda la gramatica en orden
			int puntoPartida=generadores.size();
			for(int i=0; i<generadores.size(); i++) {
				ArrayList<Integer>indicesBorrar=new ArrayList<>();
				SimboloGenerador simboloActual=generadores.get(i);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					for(int k=0; k<=i; k++) {
						SimboloGenerador simboloAnterior=generadores.get(k);
						if(simboloActual.producciones.get(j).startsWith(simboloAnterior.simbolo)) {
							if(simboloAnterior.equals(simboloActual)) {
								ArrayList<String>alfa=new ArrayList<>();
								ArrayList<String>beta=new ArrayList<>();
								for(int m=0; m<simboloActual.producciones.size(); m++) {
									if(simboloActual.producciones.get(m).startsWith(simboloActual.simbolo)){
										alfa.add(simboloActual.producciones.get(m));
									}else {
										alfa.add(simboloActual.producciones.get(m));
									}	
								}
								for(int n=0; n<beta.size(); n++) {
									beta.set(n, beta.get(n)+alfabeto);
								}
								for(int n=0; n<alfa.size(); n++) {
									alfa.add(alfa.get(n)+alfabeto);
								}
								SimboloGenerador nuevo=new SimboloGenerador(String.valueOf(alfabeto),alfa);
								alfabeto++;
								generadores.add(nuevo);
								simboloActual.producciones=beta;
							}else {
								for(int l=0; l<simboloAnterior.producciones.size(); l++) {
									String nuevo=simboloActual.producciones.get(j).replaceFirst(simboloAnterior.simbolo, simboloAnterior.producciones.get(l));
									generadores.get(i).producciones.add(nuevo);
								}
								indicesBorrar.add(j);
							}
						}
						
					}
				}
				for(int k=0; k<indicesBorrar.size(); k++) {
					simboloActual.producciones.remove(indicesBorrar.get(k).intValue()-k);
				}
			}
			//Sustitucion en reversa
			for(int i=puntoPartida-2; i>=0; i--) {
				ArrayList<Integer>indicesBorrar=new ArrayList<>();
				SimboloGenerador simboloActual=generadores.get(i);
				SimboloGenerador simboloAnterior=generadores.get(i+1);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					if(simboloActual.producciones.get(j).contains(simboloAnterior.simbolo)) {
						for(int k=0; k<simboloAnterior.producciones.size();k++) {
							String nuevo=simboloActual.producciones.get(j).replaceAll(simboloAnterior.simbolo, simboloAnterior.producciones.get(k));
							simboloActual.producciones.add(nuevo);
							indicesBorrar.add(j);
						}
					}
				}
				for(int k=0; k<indicesBorrar.size(); k++) {
					simboloActual.producciones.remove(indicesBorrar.get(k).intValue()-k);
				}
			}
			//Sustitucion en los nuevos simbolos
			
			for(int i=puntoPartida; i<generadores.size(); i++) {
				SimboloGenerador simboloActual=generadores.get(i);
				ArrayList<Integer>indicesBorrar=new ArrayList<>();
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					for(int k=0; k<generadores.size()-puntoPartida; k++) {
						SimboloGenerador simboloComparacion=generadores.get(k);
						if(simboloActual.producciones.get(j).startsWith(simboloComparacion.simbolo)) {
							for(int l=0; l<simboloComparacion.producciones.size(); l++) {
								String nuevo=simboloActual.producciones.get(j).replaceAll(simboloComparacion.simbolo, simboloComparacion.producciones.get(l));
								simboloActual.producciones.add(nuevo);
								indicesBorrar.add(j);
							}
							
						}
					}
				}
				for(int k=0; k<indicesBorrar.size(); k++) {
					simboloActual.producciones.remove(indicesBorrar.get(k).intValue()-k);
				}
			}
			System.out.println("Forma Normal de Greibach");
			for(SimboloGenerador s:generadores) {
				System.out.println(s.toString());
			}
			System.out.println();
			System.out.println("Automata de Pila");
			Graph<String,DefaultEdge> automata=new DefaultDirectedGraph<>(DefaultEdge.class);
			automata.addVertex("q0");
			automata.addVertex("q1");
			automata.addVertex("q2");
			automata.addEdge("q0","q1");
			automata.addEdge("q1", "q1");
			automata.addEdge("q1","q2");
			System.out.println(automata);
			System.out.println();
			System.out.println("Transiciones");
			System.out.println("De q0 a q1");
			System.out.println("epsilon,I/"+generadores.get(0).simbolo);
			System.out.println("De q1 a q1");
			for(int i=0; i<generadores.size(); i++) {
				SimboloGenerador simboloActual=generadores.get(i);
				for(int j=0; j<simboloActual.producciones.size(); j++) {
					String produccion=simboloActual.producciones.get(j);
					if(produccion.length()==1) {
						System.out.println(produccion.charAt(0)+","+simboloActual.simbolo+"/"+"epsilon");
					}else {
						System.out.println(produccion.charAt(0)+","+simboloActual.simbolo+"/"+produccion.substring(1));
					}
				}	
			}
			System.out.println();
			System.out.println("De q1 a q2");
			System.out.println("epsilon,I/epsilon");
		}catch(FileNotFoundException ex){
			System.out.println("No se localizó el archivo "+ex);
		}catch (IOException ex) {
			System.out.println("Ocurrio un error de I/O"+ex);
		}catch(OutOfMemoryError ex) {
			System.out.println("No es posible convertir esta gramatica");
		}

	}
	

	public static void main(String[] args) {
		convertir();
	}
	

}
