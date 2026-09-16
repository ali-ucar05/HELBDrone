// Cette classe centralise les attributs et méthodes permettant au nuage
// de se déplacer vers le bas, ainsi que la logique nécessaire pour gérer
// son comportement et son évolution dans le jeu.
public class Cloud extends GameElement{
    
    private static final String[] CLOUD_IMAGES = {"img/cloud.png"};
    private static final int CLOUD_WIDTH_SIZE = 1;
    private static final int CLOUD_HEIGHT_SIZE = CLOUD_WIDTH_SIZE;
    private boolean isActive = true; 

    public Cloud(int posX, int posY)
    {
        super(posX, posY, CLOUD_IMAGES, CLOUD_WIDTH_SIZE, CLOUD_HEIGHT_SIZE);
    }

    @Override
    public void performAction(Controller gameBoard){
        if(isActive)
        {
            moveCloud();
            gameBoard.checkCloudsOutside();
        }
    }

    // Déplace le nuage vers le bas en augmentant sa position sur l’axe Y.
    // Paramètres : aucun.
    // Retour : rien.
    public void moveCloud(){
        setPosY(getPosY()+CalculationConstants.OFFSET);
    }

    // Désactive les nuages lorsque la touche 'P' est pressée (code de triche).
    // Paramètres : aucun.
    // Retour : rien.
    public void disableClouds(){isActive = false;}

    // Renvoie un booléen indiquant si les nuages sont activés ou désactivés.
    // Paramètres : aucun.
    // Retour : un booléen.
    public boolean isCloudsActif(){return isActive;}
}