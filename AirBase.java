// Cette classe centralise les méthodes permettant de contrôler la base aérienne
// ainsi que l’ensemble de son comportement au cours de la partie.
// Elle gère notamment la génération des drones et le suivi de leur état,
// notamment pour déterminer s’ils sont encore actifs ou détruits.
import java.util.ArrayList;
import java.util.HashMap;

public class AirBase extends Living {
    private static final String[] AIR_BASE_IMAGES = {"/img/air_base.png", "/img/vulnerable_airbase.png", "/img/air_base_heart.png"};
    private static final double AIRBASE_TIMER_DELAYS = 5;
    private static final int AIR_BASE_WIDTH_SIZE = 2;
    private static final int AIR_BASE_HEIGHT_SIZE = AIR_BASE_WIDTH_SIZE;
    private static boolean IS_AIRBASE_Vulnerable = false;
    private HashMap<String, Drone> droneMap = new HashMap<String, Drone>();
    private boolean isInvunerabilityCheatCodeActivated = false;
    private Controller controller;
    public static final int AIRBASE_MAX_HEALTH = 5;
    public static final Coordinate RED_DRONE_INIT_POSITION = new Coordinate(Controller.getHalfRows(), (Controller.getHalfColumns())-Drone.getDroneHeightSize());
    public static final Coordinate ORANGE_DRONE_INIT_POSITION = new Coordinate(Controller.getHalfRows(), Controller.getHalfColumns() + AirBase.getAirBaseHeightSize());
    public static final Coordinate BLUE_DRONE_INIT_POSITION = new Coordinate(Controller.getHalfRows() + AirBase.getAirBaseWidthSize(), Controller.getHalfColumns());
    public static final Coordinate PURPLE_DRONE_INIT_POSITION = new Coordinate((Controller.getHalfRows())-Drone.getDroneWidthSize(), Controller.getHalfColumns());

    public AirBase(int posX, int posY, Controller controller)
    {
        super(posX, posY, AIR_BASE_IMAGES, AIR_BASE_WIDTH_SIZE,  AIR_BASE_HEIGHT_SIZE, 
        AIRBASE_MAX_HEALTH, IS_AIRBASE_Vulnerable,AIRBASE_TIMER_DELAYS);
        this.controller = controller;
    }

    @Override
    public void changeState()
    {
        int firstImageIndex = 0;
        int secondImageIndex = getPathToImageLen() / getPathToImageLen();

        if(isVulnerable())
        {
            setCurrentImageIndex(secondImageIndex);
        }
        else
        {
            setCurrentImageIndex(firstImageIndex);
        }
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        // Vérifie les points de vies des drones.
        checkDroneHealth(gameBoard.getGameElementList());
        // Gère le temps de vulnérabilité de la base aérienne.
        manageAirBaseVulnerability(gameBoard);
        // Change l'état de la base aérienne.
        changeState();
    }

    public static int getAirBaseWidthSize(){return AIR_BASE_WIDTH_SIZE;}

    public static int getAirBaseHeightSize(){return AIR_BASE_HEIGHT_SIZE;}

    public HashMap<String, Drone> getDroneMap(){return droneMap;}

    public int getDroneMapLen(){return droneMap.size();}

    // Vérifie si le code de triche permettant de rendre la base aérienne
    // indéfiniment vulnérable ou invulnérable est activé.
    // Paramètres : aucun.
    // Retour : un booléen indiquant si le code de triche est activé (true) ou désactivé (false).
    public boolean isCheatCodeActivated(){return isInvunerabilityCheatCodeActivated;}

    // Modifie l’état du code de triche permettant de rendre la base aérienne
    // indéfiniment vulnérable ou invulnérable.
    // Paramètres : aucun.
    // Retour : rien.
    public void toggleVulnerabilityCheatCode(){isInvunerabilityCheatCodeActivated = !isInvunerabilityCheatCodeActivated;}

    // Génère des drones de manière aléatoire en fonction du niveau actuel.
    // Utilise un nombre aléatoire pour sélectionner l’un des quatre types de drones à créer.
    // Paramètres : 
    //   - int round : nombre de drones à générer. 
    //   - ArrayList<GameElement> gameElementList : liste dans laquelle les drones générés sont ajoutés
    //     afin que le contrôleur puisse gérer leurs interactions logiques.
    // Retour : rien.
    public void generateDrone(int round, ArrayList<GameElement> gameElementList)
    {
        // Permet d'éviter de générer un drone en plus lorsque la manche en cours est la dérnière.
        if(round > Controller.getMaxRounds())
        {
            round = Controller.getMaxRounds();
        }

        if(droneMap.size() != round)
        {
            String key = "";
            int redDroneNumber = 0;
            int orangeDroneNumber = 1;
            int blueDroneNumber = 2;
            int purpleDroneNumber = 3;
            HashMap<Integer, String> droneTypeMap = new HashMap<Integer, String>();
            droneTypeMap.put(redDroneNumber, RedDrone.RED_DRONE_KEY);
            droneTypeMap.put(orangeDroneNumber, OrangeDrone.ORANGE_DRONE_KEY);
            droneTypeMap.put(blueDroneNumber, BlueDrone.BLUE_DRONE_KEY);
            droneTypeMap.put(purpleDroneNumber, PurpleDrone.PURPLE_DRONE_KEY);

            // Le nombre de drones génénérer se fait avec la manche en cours.
            while (droneMap.size() < round ) {
                int randomNum = (int)(Math.random() * Controller.getMaxRounds()); // Génération d'un nombre random.
                // Vérification si type de drone n'est pas présent dans la hashMap.
                // par la suite la base aerienne va créer un drone et l'ajouter dans la hashMap et dans la liste d'éléments de jeu.
                key = droneTypeMap.get(randomNum);
                if(!droneMap.containsKey(key))
                {
                    Drone drone = createDrone(key);
                    droneMap.put(key, drone);
                    gameElementList.add(CalculationConstants.MIN_INDEX,drone);
                }
            }
        }
    }

    // Génère des drones en utilisant une clé permettant de sélectionner l’un des quatre types possibles.
    // Utilisée par les codes de triche pour créer instantanément un type de drone spécifique.
    // Paramètres : 
    //   - int round : nombre de drones à générer. 
    //   - ArrayList<GameElement> gameElementList : liste dans laquelle les drones générés sont ajoutés
    //     afin que le contrôleur puisse gérer leurs interactions logiques.
    //   - String keyDrone : clé indiquant le type de drone à générer.
    // Retour : rien.
    public void generateDrone(int round, ArrayList<GameElement> gameElementList, String keyDrone)
    {
        if(round > Controller.getMaxRounds())
        {
            round = Controller.getMaxRounds();
        }

        if(droneMap.size() == round)
        {
            System.out.println("A " + keyDrone + " drone can't been generated.");
        }

        else
        {
            if(!droneMap.containsKey(keyDrone))
            {
                Drone drone = createDrone(keyDrone);
                if(checkIfDroneSpawnPointItsFree(new Coordinate(drone.getPosX(), drone.getPosY())))
                droneMap.put(keyDrone, drone);
                gameElementList.add(CalculationConstants.MIN_INDEX,drone);
                System.out.println("A " + keyDrone + " drone has been generated.");
            }
        }
    }

    // Vérifie dans le HashMap droneMap de la base aérienne si les drones sont toujours vivants.
    // Si un drone est détruit, il est retiré de la liste gameElementList ainsi que du droneMap.
    // Lorsque tous les drones sont éliminés, la base aérienne devient vulnérable et un timestamp est fixé,
    // permettant ensuite de la rendre invulnérable si elle ne subit aucun dégât pendant ce délai.
    //
    // Paramètre : 
    //   - ArrayList<GameElement> gameElementList : liste contenant les éléments du jeu,
    //     utilisée pour retirer les drones détruits.
    //
    // Retour : rien.
    public void checkDroneHealth(ArrayList<GameElement> gameElementList)
    {
        ArrayList<String> destroyedDroneKeyList = new ArrayList<String>();

        if(droneMap.size() > CalculationConstants.MIN_SIZE){
            for (String key : droneMap.keySet()) {
                Drone drone = droneMap.get(key);
                if(!drone.isAlive())
                {
                    destroyedDroneKeyList.add(key);
                }
            }

            for (String key : destroyedDroneKeyList)
            {
                Drone drone = droneMap.get(key);
                drone.hide();
                gameElementList.remove(drone);
                droneMap.remove(key);

                if(droneMap.size()==CalculationConstants.MIN_SIZE)
                {
                    if(!isCheatCodeActivated())
                    {
                        toggleVulnerable();
                    }
                    setTimer((System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER) + getTimerDelays());
                }
            }
        }
    }

    // Détruit tous les drones lorsque la touche 'L' est pressée (code de triche).
    // Paramètres : aucun.
    // Retour : rien.
    public void destroyAllDrone()
    {
        if(droneMap.size() > CalculationConstants.MIN_SIZE)
        {
            for(Drone drone : droneMap.values())
            {
                drone.changeState(); 
                drone.setNotAlive();
            }
        }
    }

    // Gère les actions déclenchées par la base aérienne après avoir été touchée.
    // Cette méthode permet d’exécuter les comportements spécifiques de la base,
    // tels que la génération de drones ou l’activation de mécanismes particuliers.
    // Paramètre : 
    //   - Controller gameBoard : le contrôleur du jeu.
    // Retour : rien.
    public void onAirBasehit(Controller gameBoard, DamagingElement damagingElement)
    {
        if(!isCheatCodeActivated()){toggleVulnerable();}
        generateDrone(gameBoard.getCurrentRound(), gameBoard.getGameElementList());
        gameBoard.generatePhilosopherStone(gameBoard.getChanceOfPhilosopherstone());
        gameBoard.incrementScore(damagingElement.getDamage());
    }

    // Vérifie et met à jour l’état de vulnérabilité de la base aérienne.
    // Deux cas sont possibles :
    //   - Si le code de triche rendant la base aérienne vulnérable ou invulnérable est activé,
    //     aucun changement n’est effectué.
    //   - Sinon, le délai de vulnérabilité est vérifié afin de rendre la base aérienne invulnérable
    //     une fois le temps écoulé.
    // Paramètres : aucun.
    // Retour : rien.
    private void manageAirBaseVulnerability(Controller gameBoard)
    {
        if(isVulnerable())
        {
            long currentTime = System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER;
            if(currentTime > getTimer())
            {
                if(!isInvunerabilityCheatCodeActivated)
                {
                    toggleVulnerable();
                }
                generateDrone(gameBoard.getCurrentRound(), gameBoard.getGameElementList());
                gameBoard.generatePhilosopherStone(gameBoard.getChanceOfPhilosopherstone());
            }
        }
    }

    // Crée et retourne un drone en fonction de la clé indiquant son type.
    // Paramètre : 
    //   - String keyDrone : clé définissant le type de drone à créer.
    // Retour : le drone créé correspondant au type spécifié.
    private Drone createDrone(String keyDrone)
    {
        if(keyDrone.equals(RedDrone.RED_DRONE_KEY)){return new RedDrone(RED_DRONE_INIT_POSITION.x, RED_DRONE_INIT_POSITION.y,this, controller);}
        else if(keyDrone.equals(OrangeDrone.ORANGE_DRONE_KEY)){return new OrangeDrone(ORANGE_DRONE_INIT_POSITION.x, ORANGE_DRONE_INIT_POSITION.y,this, controller);}
        else if(keyDrone.equals(BlueDrone.BLUE_DRONE_KEY)){return new BlueDrone(BLUE_DRONE_INIT_POSITION.x, BLUE_DRONE_INIT_POSITION.y,this, controller);}
        return new PurpleDrone(PURPLE_DRONE_INIT_POSITION.x, PURPLE_DRONE_INIT_POSITION.y,this, controller);
    }
    
    // Vérifie si les points d’apparition des drones sont libres pour permettre la génération d’un nouveau drone.
    // La vérification se fait en comparant la position initiale du drone avec les positions des drones déjà présents.
    // Paramètre : 
    //   - Position initiale du drone à vérifier.
    // Retour : un booléen indiquant si la zone est libre (true) ou occupée (false).
    private boolean checkIfDroneSpawnPointItsFree(Coordinate coord)
    {
        for(Drone drone : droneMap.values())
        {
            if(drone.getPosX() == coord.x && drone.getPosY() == coord.y)
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public void takeDamage(int damage)
    {
        // Si la base aérienne est vulnérable et que ses points de vie ont changé, un point de vie est retiré.
        // Si les points de vie atteignent 0, le jeu passe au round suivant.
        if(isVulnerable())
        {
            if(getHealth() > CalculationConstants.MIN_HEALTH)
            {
                setHealth(getHealth()-damage);
                if(getHealth() <= CalculationConstants.MIN_HEALTH)
                {
                    setHealth(getMaxHealth());
                    Controller.nextRound();
                }
            }
        }
    }
}