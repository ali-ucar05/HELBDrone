// Cette classe représente le drone bleu.
// Elle implémente le comportement spécifique de déplacement du drone bleu
// vers le nuage, ainsi que la logique permettant
// d’interrompre son déplacement.
public class BlueDrone extends Drone{
    private static final String[] BLUE_DRONE_IMAGE = {"/img/blue_drone.png"};
    private static final boolean IS_BLUE_DRONE_MOVEMENT_DIAGONAL = true;
    private static boolean IS_BLUE_DRONE_FROZEN = false;
    public static final String BLUE_DRONE_KEY = "BLUE"; 

    public BlueDrone(int posX, int posY, AirBase airBase, Controller controller)
    {
        super(posX,posY, BLUE_DRONE_IMAGE, IS_BLUE_DRONE_MOVEMENT_DIAGONAL, airBase, controller);
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        super.performAction(gameBoard);
            
        setTargetForBlueDrone(gameBoard);

        // Si le drone n'est pas figé, il se déplace.
        if(!IS_BLUE_DRONE_FROZEN)
        {
            moveToTarget();
        }
    }

    public static void freezeBlueDrone(){IS_BLUE_DRONE_FROZEN = true;}

    // Met à jour la cible du drone bleu en fonction de la position du nuage.
    // Si le nuage se trouve dans la zone ennemie, le drone bleu le prend pour cible
    // et le poursuit. Sinon, le drone retourne à sa position initiale.
    // Paramètres : aucun.
    // Retour : rien.
    private void setTargetForBlueDrone(Controller gameBoard){
        if(gameBoard.getCloudPosY() >= gameBoard.getEnemyZoneMinHeight() 
        && gameBoard.getCloudPosY() < gameBoard.getEnemyZoneMaxHeight()-CalculationConstants.OFFSET)
        {
            setTarget(new Coordinate(gameBoard.getCloud().getPosX()+(gameBoard.getCloud().getWidthSize()/CalculationConstants.HALF_DIVIDER), 
            gameBoard.getCloud().getPosY()+(gameBoard.getCloud().getHeightSize()/CalculationConstants.HALF_DIVIDER)));
        }
        else
        {
            setTarget(AirBase.BLUE_DRONE_INIT_POSITION);
        }
    }
}