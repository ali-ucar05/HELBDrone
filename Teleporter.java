// Cette classe représente le téléporteur.
// Elle centralise les méthodes permettant de téléporter le pilote sentinelle
// vers le côté opposé du terrain, ainsi que de gérer l’activation ou la
// désactivation des téléporteurs.
// Elle est utilisée dans le Controller pour instancier les deux téléporteurs du jeu.
public class Teleporter extends GameElement{

    private static final String[] TELEPORTER_IMAGES = {"img/left_teleporter.png","img/right_teleporter.png"};
    private static final int TELEPORTER_WIDTH_SIZE = 1;
    private static final int TELEPORTER_HEIGHT_SIZE = 3;
    private boolean isActive = true;
    private int minIndex = 0;
    private int maxIndex = 1;

    public Teleporter(int posX, int posY, String key)
    {
        super(posX, posY, TELEPORTER_IMAGES, TELEPORTER_WIDTH_SIZE, TELEPORTER_HEIGHT_SIZE);
        if(key.equals("LEFT"))
        {
            setCurrentImageIndex(minIndex);
        }
        else
        {
            setCurrentImageIndex(maxIndex);
        }
    }

    @Override
    public void performAction(Controller gameBoard){
        teleportSentinelPilot(gameBoard.getSentinelPilot(), gameBoard.getZoneMinWidth()-CalculationConstants.OFFSET, gameBoard.getZoneMaxWidth());
    };

    public static int getTeleporterHeightSize(){return TELEPORTER_HEIGHT_SIZE;}

    public void toggleActive(){isActive = !isActive;}

    // Téléporte le pilote sentinelle vers le côté opposé si les téléporteurs sont actifs.
    // Le paramètre minWidth représente la largeur minimale de la fenêtre du jeu,
    // et maxWidth représente la largeur maximale de la fenêtre.
    // Paramètres : 
    //   - int minWidth : la largeur minimale de la fenêtre.
    //   - int maxWidth : la largeur maximale de la fenêtre.
    // Retour : rien.
    public void teleportSentinelPilot(SentinelPilot sentinelPilot, int minWidth, int maxWidth)
    {
        if(isActive)
        {
            if(sentinelPilot.getPosX() < minWidth)
            {
                sentinelPilot.setPosX(maxWidth-CalculationConstants.OFFSET);
            }

            if(sentinelPilot.getPosX() == maxWidth)
            {
                sentinelPilot.setPosX(minWidth);
            }
        }
        else
        {
            sentinelPilot.toggleStable();
            if(sentinelPilot.getPosX() == minWidth)
            {
                sentinelPilot.setPosX(minWidth+CalculationConstants.OFFSET);
            }

            if(sentinelPilot.getPosX() == maxWidth)
            {
                sentinelPilot.setPosX(maxWidth-CalculationConstants.OFFSET);
            }
        }
    }
}