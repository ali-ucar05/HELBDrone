// La classe Drone est une classe abstraite qui regroupe les variables et méthodes générales
// communes à tous les drones.
// Elle sert de classe mère aux différentes implémentations de drones, qui en héritent
// pour partager et spécialiser leur comportement.

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Drone extends Shooter implements Mobile{
    private static final String DRONE_HIT_IMAGE = "img/drone_hit.png";
    private static final int DRONE_WIDTH_SIZE = 1;
    private static final int DRONE_HEIGHT_SIZE = DRONE_WIDTH_SIZE;
    private static final int DRONE_MAX_HEALTH = 1;
    private static final boolean IS_DRONE_Vulnerable = true;
    private static final double DRONE_TIMER_DELAYS = 0.5;
    private static final double DRONE_SHOOTING_TIMEDELAYS = 1;
    private AirBase airBase;
    private Controller controller;
    private Coordinate target;
    private boolean diagonalMovement = true;

    public Drone(int posX, int posY, String[] imagePaths, boolean diagonalMovement,AirBase airBase , Controller controller)
    {
        super(posX,posY,new String[]{imagePaths[0], DRONE_HIT_IMAGE}, 
        DRONE_WIDTH_SIZE,  DRONE_HEIGHT_SIZE, DRONE_MAX_HEALTH, IS_DRONE_Vulnerable,DRONE_TIMER_DELAYS, DRONE_SHOOTING_TIMEDELAYS);
        this.diagonalMovement = diagonalMovement;
        this.airBase = airBase;
        this.controller = controller;  
    }

    @Override
    public void changeState()
    {
        int minImageIndex = CalculationConstants.MIN_INDEX;

        if(getCurrentImageIndex() == minImageIndex)
        {
            setCurrentImageIndex(getPathToImageLen()/getPathToImageLen());
        }
        else
        {
            setCurrentImageIndex(minImageIndex);
        }
    }

    @Override
    public boolean isCoordinateInBoard(Coordinate coord){
        return(coord.x >= CalculationConstants.MIN_POS_X && coord.y >= CalculationConstants.MIN_POS_Y && coord.x < Controller.getColumns() && coord.y < Controller.getRows()); 
    }
    
    @Override
    public ArrayList<Coordinate> getAdjacentCoordinates(Coordinate coord){

        ArrayList<Coordinate> resultList = new ArrayList<Coordinate>();
        
        for(int i = coord.x-CalculationConstants.OFFSET;  i <= coord.x+CalculationConstants.OFFSET; i++){
            for(int j = coord.y-CalculationConstants.OFFSET;  j <= coord.y+CalculationConstants.OFFSET; j++){
                if(!(i == coord.x && j == coord.y)){
                    resultList.add(new Coordinate(i, j));
                }
            }     
        }
        return resultList;
    }
    
    // Retourne une ArrayList<Coordinate> contenant les cases accessibles depuis la position du drone,
    // c’est-à-dire celles situées dans la fenêtre de jeu, non occupées par un autre drone
    // et en dehors de la base aérienne.
    // Paramètre : Coordinate coord la position actuelle du drone.
    // Retour : une liste de coordonnées accessibles.
    @Override
    public ArrayList<Coordinate> getAccessibleAdjacentCoordinates(Coordinate coord){

        ArrayList<Coordinate> resultList = new ArrayList<Coordinate>();
        
        ArrayList<Coordinate> adjacentCoordinatesList = getAdjacentCoordinates(coord);
        for (Coordinate adjacent : adjacentCoordinatesList) {
            if(isCoordinateInBoard(adjacent) && isNotAirbase(adjacent) && isNotDrone(adjacent)){
                resultList.add(adjacent);
            }
        }
        return resultList;
    }

   @Override
    public Coordinate getNextCoordinateForTarget(Coordinate currentCoordinate, Coordinate targetCoordinate)
    {
        double minDistance = Controller.getRows()+Controller.getColumns();
        int minIndex = -1;

        ArrayList<Coordinate> accessibleAdjacentCoordinatesList = getAccessibleAdjacentCoordinates(currentCoordinate);
        
        for (int index = 0 ; index < accessibleAdjacentCoordinatesList.size(); index++) {
            Coordinate coord = accessibleAdjacentCoordinatesList.get(index);
            double distance = coord.getDistanceWith(targetCoordinate);
            if(distance < minDistance){
                minDistance = distance;
                minIndex = index;
            }         
            
        }

        return accessibleAdjacentCoordinatesList.get(minIndex);
    }

    // Déplace les drones soit en diagonale, soit horizontalement/verticalement,
    // selon leur mode de déplacement.
    // Paramètres : aucun.
    // Retour : rien.
    @Override
    public void moveToTarget(){
        if(!checkTargetPosition())
        {

            if(diagonalMovement)
            {
                Coordinate droneCoordinate = new Coordinate(getPosX(), getPosY());
                Coordinate nextPosition = getNextCoordinateForTarget(droneCoordinate, target);
                setPosX(nextPosition.x);
                if(getTarget().y < Controller.getEnemyZoneMaxHeight() || getPosY() + getHeightSize() < Controller.getEnemyZoneMaxHeight())
                {
                    setPosY(nextPosition.y);
                }
            }
            else
            {
                if(getPosX() != target.x && (getPosX()-CalculationConstants.OFFSET) + getWidthSize() != target.x) 
                {
                    if(getPosX() > target.x)
                    {
                        setPosX(getPosX()-CalculationConstants.OFFSET);
                    }
                    else
                    {
                        setPosX(getPosX()+CalculationConstants.OFFSET);
                    }
                }
                else
                {
                    if(getPosY() > target.y)
                    {
                        setPosY(getPosY()-CalculationConstants.OFFSET);
                    }
                    else
                    {
                        setPosY(getPosY()+CalculationConstants.OFFSET);
                    }
                }
                avoidDrone();
                avoidAirbase();
            }
        }
    }

    @Override
    public Projectile createProjectile()
    {
        boolean isShootByDrone = true;
        Projectile projectile = new Projectile(getPosX(),getPosY()+CalculationConstants.OFFSET, isShootByDrone);
        return projectile;
    }

    @Override
    public void performAction(Controller gameBoard){
        super.performAction(gameBoard);
        // Vérifie si le drone n'est pas sur la base aérienne pour éviter de tirer sur la base aérienne.
        if (((getPosX() <= airBase.getPosX() &&  getPosX() + getWidthSize() <= airBase.getPosX()) 
        || (getPosX() >= airBase.getPosX() + airBase.getWidthSize() && getPosX() + getWidthSize() >= airBase.getPosX() + airBase.getWidthSize()))
        || (getPosY() >= airBase.getPosY() + airBase.getHeightSize()))
        {
            shoot();
        }
    }

    public static int getDroneWidthSize(){return DRONE_WIDTH_SIZE;}

    public static int getDroneHeightSize(){return DRONE_HEIGHT_SIZE;}

    public Coordinate getTarget(){return target;}

    public void setTarget(Coordinate newTarget){target = newTarget;}

    // Vérifie si le drone se trouve à la même position que sa cible.
    // Paramètre : aucun.
    // Retour : un booléen indiquant si le drone a atteint sa cible (true) ou non (false).
    public boolean checkTargetPosition(){
        return (target.x == getPosX() || target.x == ((getPosX() - CalculationConstants.OFFSET) + getWidthSize())) && (target.y == getPosY() || (target.y == (getPosY()-CalculationConstants.OFFSET) + getHeightSize()));
    }

    // Vérifie si la coordonnée donnée en paramètre se trouve dans la base aérienne.
    // Paramètre : une coordonnée à vérifier.
    // Retour : un booléen indiquant si la coordonnée est dans la base aérienne (true) ou non (false).
    private boolean isNotAirbase(Coordinate coord)
    {
        if ((coord.x >= airBase.getPosX() && coord.x < airBase.getPosX() + airBase.getWidthSize()) && (coord.y >= airBase.getPosY()  && coord.y < airBase.getPosY() + airBase.getHeightSize()))
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    // Vérifie si la position indiquée par la coordonnée correspond à un drone.
    // Paramètre : une coordonnée à analyser.
    // Retour : un booléen indiquant si la coordonnée correspond à un drone (true) ou non (false).
    private boolean isNotDrone(Coordinate coord)
    {
        HashMap<String, Drone> droneMap = airBase.getDroneMap();
        for(Drone drone : droneMap.values())
        {
            if(drone == this)
            {
                continue;
            }

            if(drone.getPosX() == coord.x && drone.getPosY() == coord.y)
            {
                return false;
            }
        }
        return true;
    }
    // Vérifie si le drone risque de percuter la base aérienne
    // et ajuste sa trajectoire pour l’esquiver si nécessaire.
    // Paramètres : aucun.
    // Retour : rien.
    private void avoidAirbase()
    {
        int minSize = 1;

        if(getPosX() + (getWidthSize()-CalculationConstants.OFFSET)  == airBase.getPosX() - CalculationConstants.OFFSET && getPosY() >= airBase.getPosY() && getPosY() <= airBase.getPosY() + airBase.getHeightSize() - CalculationConstants.OFFSET)
        {
            int stepSize = (airBase.getPosY() + airBase.getHeightSize()) - getPosY();
            if(stepSize == minSize){stepSize++;}
            setPosY(getPosY() - stepSize);
        }

        if(getPosX() == (airBase.getPosX() + airBase.getWidthSize() - CalculationConstants.OFFSET) + CalculationConstants.OFFSET && getPosY() >= airBase.getPosY() && getPosY() <= airBase.getPosY() + airBase.getHeightSize() - CalculationConstants.OFFSET)
        {
            int stepSize = (airBase.getPosY() + airBase.getHeightSize()) - getPosY();
            setPosY(getPosY() + stepSize);
        }
    }

    // Vérifie si le drone risque de percuter un autre drone,
    // utilisé pour les drones ne se déplaçant pas en diagonale.
    // Ajuste la trajectoire pour éviter la collision si nécessaire.
    // Paramètres : aucun.
    // Retour : rien.
    private void avoidDrone()
    {
        HashMap<String,Drone> droneMap = airBase.getDroneMap();

        for(Drone drone : droneMap.values())
        {
            if(drone == this)
            {
                continue;
            }

            if(getPosX() + (getWidthSize()-CalculationConstants.OFFSET)  == drone.getPosX() - CalculationConstants.OFFSET && getPosY() == drone.getPosY())
            {
                setPosY(getPosY() - drone.getHeightSize());
            }

            if(getPosX() == (drone.getPosX() + drone.getWidthSize() - CalculationConstants.OFFSET) + CalculationConstants.OFFSET && getPosY() == drone.getPosY())
            {
                setPosY(getPosY() + drone.getHeightSize());
            }
        }
    }
}