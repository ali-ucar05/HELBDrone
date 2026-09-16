// Cette classe représente le drone rouge.
// Elle implémente le comportement de déplacement du drone vers le pilote sentinelle,
// ainsi que la logique permettant d’interrompre son déplacement.

public class RedDrone extends Drone{
    private static final String[] RED_DRONE_IMAGE = {"/img/red_drone.png"};
    private static final boolean IS_RED_DRONE_MOVEMENT_DIAGONAL = true;
    private static boolean IS_RED_DRONE_FROZEN = false;
    public static final String RED_DRONE_KEY = "RED"; 

    public RedDrone(int posX, int posY, AirBase airBase, Controller controller)
    {
        super(posX,posY, RED_DRONE_IMAGE, IS_RED_DRONE_MOVEMENT_DIAGONAL, airBase, controller);
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        super.performAction(gameBoard);

        setTargetForRedDrone(gameBoard);

        // Si le drone n'est pas figé, il se déplace.
        if(!IS_RED_DRONE_FROZEN)
        {
            moveToTarget();
        }
    }

    public static void freezeRedDrone(){IS_RED_DRONE_FROZEN = true;}

    private void setTargetForRedDrone(Controller gameBoard)
    {
        // Le drone rouge suit le pilote sentinelle.
        setTarget(new Coordinate(gameBoard.getSentinelPilot().getPosX(), gameBoard.getSentinelPilot().getPosY()));
    }
}