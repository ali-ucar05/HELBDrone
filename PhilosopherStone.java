// Cette classe représente la pierre philosophale.
// Elle implémente les méthodes et le comportement permettant d’être collectée
// et de rendre le pilote sentinelle invulnérable pendant un certain nombre de secondes.
import java.util.ArrayList;

public class PhilosopherStone extends TriggerElement{
    private static final String[] PHILOSOPHERSTONE_IMAGES = {"/img/yellow_philosopher_stones.png","/img/orange_philosopher_stones.png", 
    "/img/red_philosopher_stones.png"};
    private static final int PHILOSOPHERSTONE_WIDTH_SIZE = 1;
    private static final int PHILOSOPHERSTONE_HEIGHT_SIZE = PHILOSOPHERSTONE_WIDTH_SIZE;
    private double[] timeEffect = {5, 10, 15};
    
    public PhilosopherStone(int posX, int posY) {
        super(posX, posY, PHILOSOPHERSTONE_IMAGES, PHILOSOPHERSTONE_WIDTH_SIZE, PHILOSOPHERSTONE_HEIGHT_SIZE);
    }
    
    @Override
    public void triggerAction(Controller gameBoard){
        // La pierre philosophale disparait après la consommation.
        hide();
        SentinelPilot sentinelPilot = gameBoard.getSentinelPilot();
        // Le pilote sentinelle devient invulnérable.
        sentinelPilot.setSentinelPilotInvulnerable(getTimeEffect(), gameBoard.getTeleporterList());
        // Le pilote sentinelle devient rouge.
        sentinelPilot.changeState();
        // La pierre philosophale change de couleur pour avoir une nouvelle pierre philosophale pour la prochaine génération.
        performAction(gameBoard);
    }

    // réflechir comment faire le perform action.
    @Override
    public void performAction(Controller gameBoard){setPhilosopherStoneImage();}

    // Change l'image de la pierre philosophale de manière aléatoire à chaque génération.
    // Paramètres : aucun.
    // Retour : rien.
    public void setPhilosopherStoneImage(){setCurrentImageIndex((int) (Math.random() * getPathToImageLen()));}

    // Retourne la durée de l'effet d'invulnérabilité que la pierre philosophale offre,
    // en fonction de l'index de l'image.
    // Paramètre : aucun.
    // Retour : le temps de l'effet d'invulnérabilité.
    public double getTimeEffect(){return timeEffect[getCurrentImageIndex()];}
}