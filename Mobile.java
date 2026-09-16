// Cette interface définit les méthodes permettant le déplacement vers une cible
// pour tous les modèles ayant la capacité de se déplacer,
// tels que les drones ou les missiles autoguidés.
import java.util.ArrayList;

public interface Mobile {
    // Vérifie si une coordonnée se trouve dans le gameboard.
    // Paramètre : 
    //   - Coordinate coord : la coordonnée à vérifier.
    // Retour : un booléen (true si la coordonnée est dans le gameboard, false sinon).
    public boolean isCoordinateInBoard(Coordinate coord);

    // Envoie une ArrayList de cases adjacentes en fonction de la position de l'élément.
    // Paramètre : 
    //   - Coordinate coord : la position de l'élément.
    // Retour : une ArrayList de coordonnées représentant les cases adjacentes.
    public ArrayList<Coordinate> getAdjacentCoordinates(Coordinate coord);

    // Envoie une ArrayList des cases adjacentes accessibles à partir de la position de l'élément.
    // Filtre les cases adjacentes en fonction de leur accessibilité.
    // Paramètre : 
    //   - Coordinate coord : la position de l'élément.
    // Retour : une ArrayList de coordonnées représentant les cases adjacentes accessibles.
    public ArrayList<Coordinate> getAccessibleAdjacentCoordinates(Coordinate coord);

    // Récupère la coordonnée la plus proche de la cible.
    // Paramètres : 
    //   - Coordinate currentCoordinate : la position actuelle de l'élément du jeu.
    //   - Coordinate targetCoordinate : la position de la cible.
    // Retour : la coordonnée la plus proche de la cible.
    public Coordinate getNextCoordinateForTarget(Coordinate currentCoordinate, Coordinate targetCoordinate);
    
    // Permet de déplacer un élément du jeu.
    // Paramètres : aucun.
    // Retour : rien.
    public void moveToTarget();
}