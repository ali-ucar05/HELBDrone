// Cette classe représente le missile autoguidé.
// Elle implémente les méthodes et le comportement associés au missile,
// notamment sa capacité à être collecté ainsi qu’à suivre automatiquement ses cibles.
import java.util.ArrayList;
import java.util.HashMap;

public class Missile extends DamagingElement implements Mobile {
    private static final String[] MISSILE_IMAGES = {"/img/missile.png"};
    private static final int MISSILE_WIDTH_SIZE = 1;
    private static final int MISSILE_HEIGHT_SIZE = MISSILE_WIDTH_SIZE;
    private static final int MISSILE_DAMAGE = 1;
    private AirBase target;
    private HashMap<String ,Drone> targetMap;
    private boolean isFollowingTarget = false; // Cette variable permet de faire suivre le missile autoguidé vers sa cible.

    public Missile(int posX, int posY, boolean isFollowingTarget, AirBase target,HashMap<String ,Drone> targetMap){
        super(posX, posY, MISSILE_IMAGES, MISSILE_WIDTH_SIZE, MISSILE_HEIGHT_SIZE, MISSILE_DAMAGE);
        this.isFollowingTarget = isFollowingTarget;
        this.target = target;
        this.targetMap = targetMap;
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
    
    @Override
    public ArrayList<Coordinate> getAccessibleAdjacentCoordinates(Coordinate coord){

        ArrayList<Coordinate> resultList = new ArrayList<Coordinate>();
        
        ArrayList<Coordinate> adjacentCoordinatesList = getAdjacentCoordinates(coord);
        for (Coordinate adjacent : adjacentCoordinatesList) {
            if(isCoordinateInBoard(adjacent)){
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

    @Override
    public void moveToTarget(){
        Coordinate autoGuidedMissileCoordinate = new Coordinate(getPosX(), getPosY());
        Coordinate nextPosition;
        int minDroneMapSize = CalculationConstants.MIN_SIZE;

        if(target.getDroneMapLen() > minDroneMapSize)
        {
            ArrayList<Coordinate> targetsCoordinateList = new ArrayList<Coordinate>();
            setTargetMap(target.getDroneMap());
            for(Drone target : targetMap.values())
            {
                targetsCoordinateList.add(new Coordinate(target.getPosX(),target.getPosY()));
            }
            nextPosition = getNextCoordinateForTarget(autoGuidedMissileCoordinate, targetsCoordinateList);
        }
        else
        {
            Coordinate targetCoordinate =  new Coordinate(target.getPosX(), target.getPosY());
            nextPosition = getNextCoordinateForTarget(autoGuidedMissileCoordinate, targetCoordinate);
        }

        setPosX(nextPosition.x);
        setPosY(nextPosition.y);
    }

    @Override
    public void triggerAction(Controller gameBoard){
        hide();
        gameBoard.getSentinelPilot().equipMissile();
    }

    @Override
    public void triggerDamage(Living enemy)
    {
        hide();
        hitEnemy(enemy);
        toggleFollowTarget();
    }

    @Override
    public void performAction(Controller gameBoard){
        // Vérification si le missile est activé.
        if(isGameElementFollowTarget())
        {
            // Déplacement du missile vers sa cible.
            moveToTarget();
            // Vérification du missile si il a touché sa cible.
            gameBoard.checkMissileHit();
        }
    }

    public HashMap<String ,Drone> getTargetMap(){return targetMap;}

    public void setTargetMap(HashMap<String ,Drone> newTargetMap){targetMap=newTargetMap;}

    // Retourne un booléen indiquant si le missile autoguidé suit sa cible.
    // Paramètres : aucun.
    // Retour : un booléen (true si le missile suit sa cible, false sinon).
    public boolean isGameElementFollowTarget(){return isFollowingTarget;}

    public void toggleFollowTarget(){isFollowingTarget = !isFollowingTarget;}

    // Retourne la coordonnée d'une case proche de la cible.
    // Paramètres : 
    //   - Coordinate currentCoordinate : la position actuelle du missile.
    //   - ArrayList<Coordinate> targetsCoordinateList : la liste des coordonnées des cibles.
    // Retour : la coordonnée d'une case proche de la cible.
    public Coordinate getNextCoordinateForTarget(Coordinate currentCoordinate,ArrayList<Coordinate> targetsCoordinateList){
        double minDistance = Controller.getRows()+Controller.getColumns();
        int minIndex = -1;
        ArrayList<Coordinate> accessibleAdjacentCoordinatesList = getAccessibleAdjacentCoordinates(currentCoordinate);
        
        for (int index = 0 ; index < accessibleAdjacentCoordinatesList.size(); index++) {
            Coordinate coord = accessibleAdjacentCoordinatesList.get(index);
            for (Coordinate targetCoordinate : targetsCoordinateList)
            {
                double distance = coord.getDistanceWith(targetCoordinate);
                if(distance < minDistance){
                    minDistance = distance;
                    minIndex = index;
                }         
            }
        }
        return accessibleAdjacentCoordinatesList.get(minIndex);
    }
}