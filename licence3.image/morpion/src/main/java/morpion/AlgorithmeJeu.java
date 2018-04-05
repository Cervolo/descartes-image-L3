package morpion;

import java.util.HashMap;

public class AlgorithmeJeu {

	//private String[] morpion = new String[9];
	private HashMap<Cell, Shape> morpion;
	private boolean victoireCROIX =false;
	private boolean victoireROND =false;
	private int nbrCoups=9;
	private int coupsRond=0;
	private int coupsCroix=0;
	
	
	public AlgorithmeJeu(HashMap<Cell, Shape> morpion) {
		this.morpion = morpion;
	}
	
	public void testVictoire() {
		
		/*for (int i=0;i<morpion.size();i++) {
			if (morpion[i]==Shape.EMPTY) nbrCoups--;
			if (morpion[i]=="ROND") coupsRond++;
			if (morpion[i]=="CROIX") coupsCroix++;
		}*/
		
		for (Shape shape : morpion.values()) {
			if (shape==Shape.EMPTY) nbrCoups--;
			if (shape==Shape.CIRCLE) coupsRond++;
			if (shape==Shape.CROSS) coupsCroix++;
		}
		
		Shape[] tabShapes = morpion.values().toArray(new Shape[0]);
		
		if (tabShapes[0]==Shape.CROSS && tabShapes[1]==Shape.CROSS && tabShapes[2]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[0]==Shape.CROSS && tabShapes[4]==Shape.CROSS && tabShapes[8]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[0]==Shape.CROSS && tabShapes[3]==Shape.CROSS && tabShapes[6]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[1]==Shape.CROSS && tabShapes[4]==Shape.CROSS && tabShapes[7]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[2]==Shape.CROSS && tabShapes[4]==Shape.CROSS && tabShapes[6]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[2]==Shape.CROSS && tabShapes[5]==Shape.CROSS && tabShapes[8]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[3]==Shape.CROSS && tabShapes[4]==Shape.CROSS && tabShapes[5]==Shape.CROSS) {
			victoireCROIX = true;
		}
		if (tabShapes[6]==Shape.CROSS && tabShapes[7]==Shape.CROSS && tabShapes[8]==Shape.CROSS) {
			victoireCROIX = true;
		}
		
		if (tabShapes[0]==Shape.CIRCLE && tabShapes[1]==Shape.CIRCLE && tabShapes[2]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[0]==Shape.CIRCLE && tabShapes[4]==Shape.CIRCLE && tabShapes[8]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[0]==Shape.CIRCLE && tabShapes[3]==Shape.CIRCLE && tabShapes[6]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[1]==Shape.CIRCLE && tabShapes[4]==Shape.CIRCLE && tabShapes[7]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[2]==Shape.CIRCLE && tabShapes[4]==Shape.CIRCLE && tabShapes[6]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[2]==Shape.CIRCLE && tabShapes[5]==Shape.CIRCLE && tabShapes[8]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[3]==Shape.CIRCLE && tabShapes[4]==Shape.CIRCLE && tabShapes[5]==Shape.CIRCLE) {
			victoireROND = true;
		}
		if (tabShapes[6]==Shape.CIRCLE && tabShapes[7]==Shape.CIRCLE && tabShapes[8]==Shape.CIRCLE) {
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
			String tour="";
			if (coupsCroix>coupsRond) tour="C'est au tour du joueur rond";
			else if (coupsCroix<coupsRond) tour="C'est au tour du joueur croix";
			System.out.println("Partie toujours en cours actuellement au "+nbrCoups+" Coups "+ tour);
		}
	
	}
}