// Cette classe représente des coordonnées et est utilisée pour stocker les positions cibles
// des éléments mobiles du jeu afin de leur permettre de suivre un objectif.
// Elle peut également servir à mémoriser des positions initiales.
// Elle est principalement utilisée par les éléments mobiles du jeu.
public class Coordinate {

    public int x = 0;
    public int y = 0;
    
    
    public Coordinate(int x, int y){
        this.x = x;
        this.y = y;
    }
    
    // Retourne la distance entre deux coordonnées.
    // Paramètre : 
    //   - Coordinate coord : la coordonnée avec laquelle calculer la distance.
    // Retour : un double représentant la distance calculée.
    public double getDistanceWith(Coordinate coord){
        return Math.sqrt((coord.y - this.y) * (coord.y - this.y) + (coord.x - this.x) * (coord.x - this.x));
    }
}