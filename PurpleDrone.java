// Cette classe représente le drone mauve.
// Elle implémente son comportement de déplacement :
// - Le drone se déplace horizontalement jusqu’au bord gauche ou droit de la zone ennemie.
// - Une fois ce bord atteint, il descend verticalement jusqu’à une position choisie aléatoirement.
// - Il repart ensuite en direction du bord opposé, en répétant ce cycle.
// Elle inclut également la logique permettant d’interrompre son déplacement.

public class PurpleDrone extends Drone{
    private static final String[] PURPLE_DRONE_IMAGE = {"/img/purple_drone.png"};
    private static final boolean IS_PURPLE_DRONE_MOVEMENT_DIAGONAL = false;
    private static boolean IS_PURPLE_DRONE_FROZEN = false;
    public static final String PURPLE_DRONE_KEY = "PURPLE"; 

    public PurpleDrone(int posX, int posY, AirBase airBase, Controller controller)
    {
        super(posX,posY, PURPLE_DRONE_IMAGE, IS_PURPLE_DRONE_MOVEMENT_DIAGONAL, airBase, controller);
        setTargetForPurpleDrone(controller);
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        super.performAction(gameBoard);

        if(checkTargetPosition()) 
        {
            setTargetForPurpleDrone(gameBoard);
        }

        // Si le drone n'est pas figé, il se déplace.
        if(!IS_PURPLE_DRONE_FROZEN)
        {
            moveToTarget();
        }
    }

    public static void freezePurpleDrone(){IS_PURPLE_DRONE_FROZEN = true;}

    // Génère une position sur l’axe X vers le côté opposé ainsi qu’une position Y aléatoire
    // pour le drone mauve.
    // Paramètres : aucun.
    // Retour : rien.
    private void setTargetForPurpleDrone(Controller gameBoard)
    {
        start:
        while (true) {
            int posX = 0;
            int posY = (int) (Math.random() * gameBoard.getEnemyZoneMaxHeight());

            if(posY < gameBoard.getEnemyZoneMinHeight()){posY=gameBoard.getEnemyZoneMinHeight();}

            if(getPosX() != CalculationConstants.MIN_POS_X && getPosX() != gameBoard.getRows()-CalculationConstants.OFFSET)
            {
                posX = CalculationConstants.MIN_POS_X;
            }
            else if(getPosX() == CalculationConstants.MIN_POS_X)
            {
                posX = gameBoard.getRows()-CalculationConstants.OFFSET;
            }
            else
            {
                posX = CalculationConstants.MIN_POS_X;
            }

            if(!gameBoard.checkTargetCollision(posX, posY))
            {
                continue start;
            }

            setTarget(new Coordinate(posX, posY));
            break;
        }
    }
}