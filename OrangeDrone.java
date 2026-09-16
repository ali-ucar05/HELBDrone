// Cette classe représente le drone orange.
// Elle implémente le comportement de déplacement du drone
// vers une position aléatoire, ainsi que la logique permettant
// d’interrompre son déplacement.
public class OrangeDrone extends Drone{
    private static final String[] ORANGE_DRONE_IMAGE = {"/img/orange_drone.png"};
    private static final boolean IS_ORANGE_DRONE_MOVEMENT_DIAGONAL = true;
    private static boolean IS_ORANGE_DRONE_FREEZE = false;
    public static final String ORANGE_DRONE_KEY = "ORANGE"; 

    public OrangeDrone(int posX, int posY, AirBase airBase, Controller controller)
    {
        super(posX,posY, ORANGE_DRONE_IMAGE, IS_ORANGE_DRONE_MOVEMENT_DIAGONAL, airBase, controller);
        setTargetForOrangeDrone(controller);
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        super.performAction(gameBoard);

        int previousPosX = getPosX();
        int previousPosY = getPosY();

        if(checkTargetPosition()) 
        {
            setTargetForOrangeDrone(gameBoard);
        }

         // Si le drone n'est pas figé, il se déplace.
        if(!IS_ORANGE_DRONE_FREEZE)
        {
            moveToTarget();
        }

        // Vérifie si le drone orange, après son déplacement, se trouve à la même position qu’auparavant.
        // Si c’est le cas, une nouvelle position cible est générée pour éviter qu’il ne soit bloqué.
        Coordinate coord = getNextCoordinateForTarget(new Coordinate(getPosX(), getPosY()), getTarget());

        if((previousPosX == coord.x && previousPosY == coord.y ))
        {
           setTargetForOrangeDrone(gameBoard);
        }
    }

    public static void freezeOrangeDrone(){IS_ORANGE_DRONE_FREEZE = true;}

    // Génère une position aléatoire pour le drone orange et vérifie qu’elle ne se situe pas sur un autre élément du jeu.
    // Paramètre : 
    //   - Controller gameBoard : permet de vérifier si la position générée est libre.
    // Retour : la position aléatoire validée.
    private void setTargetForOrangeDrone(Controller gameBoard)
    {
        start:
        while (true) {
            int posX = (int) (Math.random() * gameBoard.getRows());
            int posY = (int) (Math.random() * gameBoard.getEnemyZoneMaxHeight());

            if(posY < Controller.getEnemyZoneMinHeight())
            {
                posY = Controller.getEnemyZoneMinHeight();
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