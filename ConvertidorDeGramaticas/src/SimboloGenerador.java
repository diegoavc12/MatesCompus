import java.util.ArrayList;

public class SimboloGenerador {
	public String simbolo;
	public ArrayList<String>producciones;
	public SimboloGenerador(String simbolo, ArrayList<String>producciones) {
		this.simbolo=simbolo;
		this.producciones=producciones;
	}
	public String toString() {
		String cadena=this.simbolo+" -> ";
		for(String p:this.producciones) {
			cadena+=p+" | ";
		}
		return cadena;
	}
}
