package morpion;

public class AlgorithmeJeu {

	private String[] morpion = new String[9];
	private boolean victoireCROIX =false;
	private boolean victoireROND =false;
	private int nbrCoups=9;
	
	public AlgorithmeJeu(String[] morpion) {
		this.morpion = morpion;
	}
	
	public void testVictoire() {
		
		for (int i=0;i<morpion.length;i++) {
			if (morpion[i]=="EMPTY") nbrCoups--;
		}
		
		if (morpion[1]== "CROIX" && morpion[2]=="CROIX" &&morpion[3]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[1]== "CROIX" && morpion[5]=="CROIX" &&morpion[9]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[1]== "CROIX" && morpion[4]=="CROIX" &&morpion[7]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[2]== "CROIX" && morpion[5]=="CROIX" &&morpion[8]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[3]== "CROIX" && morpion[5]=="CROIX" &&morpion[7]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[3]== "CROIX" && morpion[6]=="CROIX" &&morpion[9]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[4]== "CROIX" && morpion[5]=="CROIX" &&morpion[6]=="CROIX") {
			victoireCROIX = true;
		}
		if (morpion[7]== "CROIX" && morpion[8]=="CROIX" &&morpion[9]=="CROIX") {
			victoireCROIX = true;
		}
		
		if (morpion[1]== "ROND" && morpion[2]=="ROND" &&morpion[3]=="ROND") {
			victoireROND = true;
		}
		if (morpion[1]== "ROND" && morpion[5]=="ROND" &&morpion[9]=="ROND") {
			victoireROND = true;
		}
		if (morpion[1]== "ROND" && morpion[4]=="ROND" &&morpion[7]=="ROND") {
			victoireROND = true;
		}
		if (morpion[2]== "ROND" && morpion[5]=="ROND" &&morpion[8]=="ROND") {
			victoireROND = true;
		}
		if (morpion[3]== "ROND" && morpion[5]=="ROND" &&morpion[7]=="ROND") {
			victoireROND = true;
		}
		if (morpion[3]== "ROND" && morpion[6]=="ROND" &&morpion[9]=="ROND") {
			victoireROND = true;
		}
		if (morpion[4]== "ROND" && morpion[5]=="ROND" &&morpion[6]=="ROND") {
			victoireROND = true;
		}
		if (morpion[7]== "ROND" && morpion[8]=="ROND" &&morpion[9]=="ROND") {
			victoireROND = true;
		}
		
		
		if (victoireCROIX) {
			System.out.println("Les Croix ont gagner en "+nbrCoups+"Coups");
			
		}
		else if (victoireROND) {
			System.out.println("Les Rond ont gagner en "+nbrCoups+"Coups");
		}
		else if (nbrCoups==9){
			System.out.println("Match nulle");
		}
		else {
			System.out.println("Partie toujours en cours actuellement au "+nbrCoups+" Coups");
		}
	
}
}