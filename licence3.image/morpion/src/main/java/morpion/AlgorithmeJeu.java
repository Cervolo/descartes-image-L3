package morpion;

import java.util.HashMap;

import javax.swing.JOptionPane;

/**
 * Classe publique qui permets d'afficher le résultat du jeu de morpion
 * @author Groupe 4 Image
 *
 */
public class AlgorithmeJeu {

	//private String[] morpion = new String[9];
	private Shape[] tabShapes;
	private boolean victoireCROIX =false;
	private boolean victoireROND =false;
	private int nbrCoups=9;
	private int coupsRond=0;
	private int coupsCroix=0;
	
	public AlgorithmeJeu(Shape[] tabShapes) {
		this.tabShapes = tabShapes;
	}
	
	
	public void testVictoire() {
		
		/*for (int i=0;i<morpion.size();i++) {
			if (morpion[i]==Shape.EMPTY) nbrCoups--;
			if (morpion[i]=="ROND") coupsRond++;
			if (morpion[i]=="CROIX") coupsCroix++;
		}*/
		
		for (Shape shape : tabShapes) {
			if (shape==Shape.EMPTY) nbrCoups--;
			if (shape==Shape.CIRCLE) coupsRond++;
			if (shape==Shape.CROSS) coupsCroix++;
		}
		
		/*for (Shape shape : tabShapes)
			System.out.println(shape);*/
		
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
			System.out.println();
			JOptionPane.showMessageDialog(null,"Les Croix ont gagné en "+nbrCoups+" coups.", "Victoire Croix",
				    JOptionPane.PLAIN_MESSAGE);
		}
		else if (victoireROND) {
			JOptionPane.showMessageDialog(null,"Les Ronds ont gagné en \"+nbrCoups+\" coups", "Victoire Rond",
				    JOptionPane.PLAIN_MESSAGE);
		}
		else if (nbrCoups==9){
			JOptionPane.showMessageDialog(null,"Match nulle", "Match nulle",
				    JOptionPane.PLAIN_MESSAGE);
		}
		else {		
			String tour="";
			if (coupsCroix>coupsRond) tour="C'est au tour du joueur Rond";
			else if (coupsCroix<coupsRond) tour="C'est au tour du joueur croix";
			tour="Partie toujours en cours actuellement au coup " + nbrCoups + "." + tour;
			JOptionPane.showMessageDialog(null,tour, "Partie en cours",
				    JOptionPane.PLAIN_MESSAGE);
		
		}
	
	}
}