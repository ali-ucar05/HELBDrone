// La classe Controller gère les interactions entre le joueur, les modèles et la vue.
// Elle assure la logique de contrôle du jeu et coordonne les actions déclenchées par les entrées utilisateur.
// Elle permet également la gestion des animations et la mise à jour de l’interface visuelle.
// Cette classe est utilisée par la classe Main pour initialiser et orchestrer le fonctionnement du jeu.
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.EventHandler;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.Duration;
import javafx.animation.Animation;
import java.util.ArrayList;
import java.util.HashMap;

public class Controller
{
    // - Les paramètres du fenetrage + les tailles. 
    private static final int WIDTH = 600;
    private static final int HEIGHT = WIDTH;
    private static final int ROWS = 15;
    private static final int COLUMNS = ROWS;
    private static final int SQUARE_SIZE = WIDTH / ROWS;
    private static final int INFORMATIONAL_ZONE_MIN_HEIGTH = 0;
    private static final int ZONE_MIN_WIDTH = 1;
    private static final int ZONE_MAX_WIDTH = ROWS-ZONE_MIN_WIDTH;
    private static final int ENEMY_ZONE_MIN_HEIGTH = 2;
    private static final int ENEMY_ZONE_MAX_HEIGTH = COLUMNS - Teleporter.getTeleporterHeightSize();
    private static final int voidX = -99;
    private static final int voidY = -99;

    // Les variables des conditions de fin de partie, de methode du controller.
    private static final int RIGHT = 0;
    private static final int LEFT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    private static final int TIMELINE_DELAYS = 200;
    private static final int MAXIMUM_ROUNDS = 4;
    private static final int MAXIMUM_SCORE = 20;
    private static final int CHANCE_OF_PHILOSOPHERSTONE = 4;
    private static final int CHANCE_OF_MISSILE = 5;
    private static int currentRound = 1;
    private boolean gameOver = false;
    private int instantlyGeneratingNumber = 0;
    private int resetTimerValue = 0;
    private int score = 0;
    private int gameTime = 0;
    private int listMinSize = 0;

    // - Les models + les listes des models. 
    private Cloud cloud;
    private SentinelPilot sentinelPilot;
    private AirBase airBase;
    private ArrayList<Teleporter> teleporterList = new ArrayList<Teleporter>();
    private PhilosopherStone philosopherStone;
    private Missile missile;
    private View view;
    private ArrayList<GameElement> gameElementList = new ArrayList<GameElement>();
    private ArrayList<TriggerElement> consumableGameElementList = new ArrayList<TriggerElement>();
    private String[] roundsImagePath = {"/img/round_1.png","/img/round_2.png","/img/round_3.png","/img/round_4.png"};
    private HashMap<String, Drone> droneMap = new HashMap<String, Drone>();
    private ArrayList<Projectile> dronesProjectileList = new ArrayList<Projectile>();
    private Timeline timeline;
    private ManageScore manageScore = new ManageScore();
    private boolean isFileWritten = false;

    public Controller(Stage primaryStage)
    {
        String leftTeleporterKey = "LEFT";
        String rightTeleporterKey = "RIGHT";
        this.sentinelPilot = new SentinelPilot((getHalfColumns()), COLUMNS-CalculationConstants.OFFSET);
        this.airBase = new AirBase(ZONE_MIN_WIDTH + (ZONE_MAX_WIDTH - ZONE_MIN_WIDTH) / CalculationConstants.HALF_DIVIDER,ENEMY_ZONE_MIN_HEIGTH + (ENEMY_ZONE_MAX_HEIGTH - ENEMY_ZONE_MIN_HEIGTH) / CalculationConstants.HALF_DIVIDER,this);
        airBase.generateDrone(currentRound, gameElementList);
        this.cloud = new Cloud(getHalfColumns(), ENEMY_ZONE_MIN_HEIGTH);
        this.philosopherStone = new PhilosopherStone(voidX, voidY);
        this.missile = new Missile(voidX, voidY,false, airBase,airBase.getDroneMap());
        gameElementList.add(philosopherStone);
        gameElementList.add(missile);
        consumableGameElementList.add(philosopherStone);
        consumableGameElementList.add(missile);
        teleporterList.add(new Teleporter(ZONE_MIN_WIDTH-CalculationConstants.OFFSET, ENEMY_ZONE_MAX_HEIGTH, leftTeleporterKey));
        teleporterList.add(new Teleporter(ZONE_MAX_WIDTH,ENEMY_ZONE_MAX_HEIGTH, rightTeleporterKey));
        gameElementList.addAll(teleporterList);
        gameElementList.add(sentinelPilot);
        gameElementList.add(airBase);
        gameElementList.add(cloud);
        this.view = new View(primaryStage);
        generateCloud();
        initTimeLine();
        setActions();
    }

    // - Les getters de de models, des tailles des zones, tailles de cases, des postions, ...
    public static int getRows(){return ROWS;}

    public static int getColumns(){return COLUMNS;}

    public static int getInformationalZoneMinHeight(){return INFORMATIONAL_ZONE_MIN_HEIGTH;}

    public static int getZoneMinWidth(){return ZONE_MIN_WIDTH;}

    public static int getWidth(){return WIDTH;}

    public static int getHeight(){return HEIGHT;}

    public static int getZoneMaxWidth(){return ZONE_MAX_WIDTH;}

    public static int getEnemyZoneMinHeight(){return ENEMY_ZONE_MIN_HEIGTH;}

    public static int getEnemyZoneMaxHeight(){return ENEMY_ZONE_MAX_HEIGTH;}

    public static int getVoidX(){return voidX;}

    public static int getVoidY(){return voidY;}

    public static int getSquareSize(){return SQUARE_SIZE;}

    public static int getMaxRounds(){return MAXIMUM_ROUNDS;}

    public static int getCurrentRound(){return currentRound;}

    public static int getChanceOfPhilosopherstone(){return CHANCE_OF_PHILOSOPHERSTONE;}

    public AirBase getAirBase(){return airBase;}

    public SentinelPilot getSentinelPilot(){return sentinelPilot;}

    public ArrayList<GameElement> getGameElementList(){return gameElementList;}

    public ArrayList<Teleporter> getTeleporterList(){return teleporterList;}

    // Incrémente le score du joueur en fonction des dégâts infligés à la base aérienne.
    // Paramètre : 
    //   - int damage : le nombre de dégâts infligés.
    // Retour : rien.
    public void incrementScore(int damage){
        if(damage>AirBase.AIRBASE_MAX_HEALTH)
        {
            damage = AirBase.AIRBASE_MAX_HEALTH;
        }
        score += damage;
    }

    public static void nextRound()
    {
        currentRound++;
    }

    public Cloud getCloud(){return cloud;}

    public static int getHalfRows(){
        return ROWS / CalculationConstants.HALF_DIVIDER;
    }

    public static int getHalfColumns(){
        return COLUMNS / CalculationConstants.HALF_DIVIDER;
    }

    // Renvoie la position Y centrée de l’image du nuage.
    // Paramètres : aucun.
    // Retour : la position Y centrée du nuage.
    public int getCloudPosY(){
        return cloud.getPosY()+(cloud.getHeightSize()/ CalculationConstants.HALF_DIVIDER);
    } 

    // Cette méthode permet d'initialiser la timeline du jeu.
    // Paramètres : aucun.
    // Retour : aucun.
    public void initTimeLine()
    {
        timeline = new Timeline(new KeyFrame(Duration.millis(TIMELINE_DELAYS), e -> run()));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    // Détecte les entrées clavier liées aux déplacements ainsi qu’aux codes de triche.
    // Paramètres : aucun.
    // Retour : rien.
    public void setActions() {
        view.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if (code == KeyCode.RIGHT) {
                    if(!sentinelPilot.isStable())
                    {
                        sentinelPilot.setCurrentDirection(RIGHT);
                    }
                } else if (code == KeyCode.LEFT) {
                    if(!sentinelPilot.isStable())
                    {
                        sentinelPilot.setCurrentDirection(LEFT);
                    }
                } else if (code == KeyCode.UP) {
                    // tir du pilote sentinel
                    sentinelPilot.shoot();
                } else if (code == KeyCode.DOWN) {
                    sentinelPilot.toggleStable();
                } else if (code == KeyCode.A) { // Génère instantanément un drone rouge à sa position de sortie si celle-ci est libre.
                    airBase.generateDrone(currentRound, gameElementList, RedDrone.RED_DRONE_KEY);
                    if(airBase.isVulnerable() && airBase.getDroneMap().size() == currentRound)
                    {
                        airBase.setTimer(resetTimerValue);
                    }
                } else if (code == KeyCode.Z) { // Génère instantanément un drone bleu à sa position de sortie si celle-ci est libre.
                    airBase.generateDrone(currentRound, gameElementList, BlueDrone.BLUE_DRONE_KEY);
                    if(airBase.isVulnerable() && airBase.getDroneMap().size() == currentRound)
                    {
                        airBase.setTimer(resetTimerValue);
                    } 
                } else if (code == KeyCode.E) { // Génère instantanément un drone orange à sa position de sortie si celle-ci est libre.
                    airBase.generateDrone(currentRound, gameElementList, OrangeDrone.ORANGE_DRONE_KEY);
                    if(airBase.isVulnerable() && airBase.getDroneMap().size() == currentRound)
                    {
                        airBase.setTimer(resetTimerValue);
                    }
                } else if (code == KeyCode.R) { // Génère instantanément un drone mauve à sa position de sortie si celle-ci est libre.
                    airBase.generateDrone(currentRound, gameElementList, PurpleDrone.PURPLE_DRONE_KEY);
                    if(airBase.isVulnerable() && airBase.getDroneMap().size() == currentRound)
                    {
                        airBase.setTimer(resetTimerValue);
                    }
                } else if (code == KeyCode.W) { // Fige instantanément le déplacement de tous les drones rouges.
                    RedDrone.freezeRedDrone();
                    System.out.println("All the red drones have been frozen.");
                } else if (code == KeyCode.X) { // Fige instantanément le déplacement de tous les drones bleus.
                    BlueDrone.freezeBlueDrone();
                    System.out.println("All the blue drones have been frozen.");
                } else if (code == KeyCode.C) { // Fige instantanément le déplacement de tous les drones orange.
                    OrangeDrone.freezeOrangeDrone();
                    System.out.println("All the orange drones have been frozen.");
                } else if (code == KeyCode.V) { // Fige instantanément le déplacement de tous les drones mauves.
                    PurpleDrone.freezePurpleDrone();
                    System.out.println("All the purple drones have been frozen.");
                } else if (code == KeyCode.J){ // Retire un point de vie à la base aérienne.
                    int damage = 1;
                    if(!airBase.isVulnerable())
                    {
                        airBase.toggleVulnerable();
                        airBase.takeDamage(damage);
                        airBase.toggleVulnerable();
                        score++;
                    }
                    else
                    {
                        airBase.takeDamage(damage);
                    }
                } else if (code == KeyCode.K){ // Rends la base aérienne indéfiniment vulnérable/invulnérable.
                    airBase.toggleVulnerabilityCheatCode(); 
                    airBase.toggleVulnerable();
                    if(airBase.isVulnerable())
                    {
                        System.out.println("The air base is Vulnerable.");
                    }
                    else
                    {
                        System.out.println("The air base is inVulnerable.");
                    }
                } else if (code == KeyCode.L){ // Détruit instantanément tous les drones.
                    airBase.destroyAllDrone();
                    System.out.println("All the drones have been destroyed.");
                } else if (code == KeyCode.M){ // Fin de partie par la destruction du vaisseau du pilote.
                    sentinelPilot.autoDestroySentinelPilot();
                    System.out.println("The pilot sentinel has self-destructed.");

                } else if(code == KeyCode.U){ // Fais apparaitre instantanément une pierre philosophale si pas déjà une présente.
                    if(sentinelPilot.isVulnerable())
                    {
                        if(philosopherStone.getPosX() != voidX && philosopherStone.getPosY() != voidY)
                        {
                            System.out.println("A philosopher stone has been already generated.");
                        }
                        else
                        {
                            generatePhilosopherStone(instantlyGeneratingNumber);
                            System.out.println("A philosopher stone has been generated.");
                        }
                    }
                    else
                    {
                        System.out.println("The philospher stone can't be generated, the sentinel pilot is inVulnerable.");
                    }
                } else if(code == KeyCode.I){ // Fais apparaitre instantanément un missile autoguidé si pas déjà un présent.
                    if(missile.getPosX() != voidX && missile.getPosY() != voidY)
                    {
                        System.out.println("A missile has been already generated.");
                    }
                    else
                    {
                        System.out.println("A missile has been generated.");
                        generateMissile(instantlyGeneratingNumber);
                    }
                } else if (code == KeyCode.O){ // Rends le pilote indéfiniment vulnérable/invulnérable
                    if(philosopherStone.getPosX() == voidX && philosopherStone.getPosY() == voidY)
                    {
                        sentinelPilot.toggleVulnerable(); 
                        sentinelPilot.changeState();
                        if(sentinelPilot.isVulnerable())
                        {
                            System.out.println("The pilot sentinel is Vulnerable.");
                        }
                        else
                        {
                            System.out.println("The pilot sentinel is inVulnerable.");
                        }
                    }
                    else
                    {
                        System.out.println("The sentinel Pilot can't be inVulnerable, a philosopher stone has been generated.");
                    }
                } else if(code == KeyCode.P){ // Désactive indéfiniment l’apparition des nuages.
                    cloud.disableClouds();
                    cloud.setPosY(voidY);
                    System.out.println("The cloud are desactivated.");
                } else if(code == KeyCode.F){ // Déployer le misile autoguidé.
                    sentinelPilot.shootMissile(missile);
                }
            }
        });
    }

    // Réinitialise la position du nuage sur l’axe Y lorsqu’il est actif et qu’il sort de la zone.
    // Lors de la réapparition du nuage, il y a une chance sur cinq de générer un missile autoguidé.
    // Paramètres : aucun.
    // Retour : rien.
    public void checkCloudsOutside()
    {
        if(cloud.isCloudsActif()) 
        {
            if(cloud.getPosY() > getColumns())
            {
                cloud.setPosY(getEnemyZoneMinHeight());
                generateCloud();
                generateMissile(CHANCE_OF_MISSILE);
            }
        }
    }

    // Fait apparaître une pierre philosophale consommable par le pilote sentinelle,
    // avec une chance sur quatre lorsqu’elle est générée dans la zone contrôlée par le pilote.
    // Cette méthode est appelée chaque fois qu’un drone est généré.
    //
    // Paramètre :
    //   - int chanceOfPhilosopherstone : valeur permettant de définir la probabilité d’apparition.
    //
    // Retour : rien.
    public void generatePhilosopherStone(int chanceOfPhilosopherstone)
    {
        if(sentinelPilot.isVulnerable())
        {
            if((philosopherStone.getPosX() == voidX && philosopherStone.getPosY() == voidY))
            {
                int randomNum = (int)(Math.random() * chanceOfPhilosopherstone); 

                if(randomNum == chanceOfPhilosopherstone)
                {
                    start:
                    while (true) {
                        int posX = (int) (Math.random() * ROWS);
                        int posY = COLUMNS- philosopherStone.getHeightSize();

                        if(!checkConsumableElementPosition(posX, posY))
                        {
                            continue start;
                        }

                        philosopherStone.setPosX(posX);
                        philosopherStone.setPosY(posY);
                        break;
                    }
                }
            }
        }
    }

    // Vérifie si la nouvelle position de la cible du drone entre en collision avec un autre élément du jeu.
    //
    // Paramètres : 
    //   - Deux valeurs représentant la nouvelle position de la cible du drone.
    //
    // Retour : un booléen indiquant si la position est libre (true) ou déjà occupée (false).
    public boolean checkTargetCollision(int posX, int posY)
    {
        if((posX >= airBase.getPosX() && posX < airBase.getPosX() + airBase.getWidthSize()) && (posY >= airBase.getPosY() && posY < airBase.getPosY() + airBase.getHeightSize()))
        {
            return false;
        }

        HashMap<String, Drone> droneMap = airBase.getDroneMap();

        for (Drone drone : droneMap.values())
        {
            if(drone.getPosX() == posX && drone.getPosY() == posY)
            {
                return false;
            }
        }

        return true;
    }

    // Vérifie si le projectile tiré par le pilote sentinelle a dépassé la zone limite.
    // Paramètre : 
    //   - Projectile projectile : le projectile à vérifier.
    // Retour : un booléen indiquant si le projectile a dépassé la zone limite,
    // permettant de le retirer de la liste du pilote sentinelle afin d’optimiser
    // la gestion des ressources.
    public boolean checkSentinelPilotProjectileOutside(Projectile projectile)
    {
        if(projectile.getPosY() == getEnemyZoneMinHeight()-CalculationConstants.OFFSET)
        {
            return true;
        }
        return false;
    }

    // Vérifie si le projectile tiré par un drone ennemi a dépassé la zone limite.
    // Paramètre : 
    //   - Projectile projectile : le projectile à vérifier.
    // Retour : un booléen indiquant si le projectile a dépassé la zone limite,
    // permettant de le retirer de la liste des projectiles du drone ennemi
    // afin d’optimiser la gestion des ressources.
    public boolean checkDroneProjectileOutside(Projectile projectile)
    {
        if(projectile.getPosY() == getColumns())
        {
            return true;
        }
        return false;
    }

    // Vérifie si le projectile du pilote sentinelle a touché un drone ou la base aérienne.
    // Les cibles impactées subissent des dégâts.
    // Si la base aérienne est touchée, elle génère des drones et il existe une chance sur quatre
    // qu’une pierre philosophale soit créée.
    // Paramètre : 
    //   - Projectile projectile : le projectile à vérifier.
    // Retour : un booléen indiquant si le projectile a touché un drone ou la base aérienne,
    // permettant de retirer le projectile de la liste du pilote sentinelle afin
    // d’optimiser la gestion des ressources.
    public boolean checkSentinelPilotHit(Projectile projectile)
    {
        HashMap<String, Drone> droneMap = airBase.getDroneMap();

        for(Drone drone : droneMap.values())
        {
            if((projectile.getPosX() >= drone.getPosX() && projectile.getPosX() < drone.getPosX() + drone.getWidthSize()) && (projectile.getPosY() == drone.getPosY() || projectile.getPosY()-CalculationConstants.OFFSET == drone.getPosY()))
            {
                projectile.triggerAction(this);
                projectile.triggerDamage(drone);
                return true;
            }
        }

       if((projectile.getPosX() >= airBase.getPosX() && projectile.getPosX() < airBase.getPosX() + airBase.getWidthSize()) && projectile.getPosY() == (airBase.getPosY()-CalculationConstants.OFFSET) + airBase.getHeightSize())
        {
            if(airBase.isVulnerable())
            {
                projectile.triggerAction(this);
                projectile.triggerDamage(airBase);
                airBase.onAirBasehit(this, projectile);
            }
            return true;
        }

        return false;
    }

    // Vérifie si un projectile de drone a touché le pilote sentinelle.
    // Si c’est le cas, le pilote subit des dégâts.
    // Paramètre : 
    //   - Projectile projectile : le projectile à vérifier.
    // Retour : un booléen indiquant si le projectile a touché le pilote sentinelle,
    // permettant de retirer le projectile de la liste du drone afin d’optimiser
    // la gestion des ressources.
    public boolean checkDronetHit(Projectile projectile)
    {
        if((projectile.getPosX() >= sentinelPilot.getPosX() && projectile.getPosX() < sentinelPilot.getPosX() + sentinelPilot.getWidthSize()) && projectile.getPosY() == sentinelPilot.getPosY())
        {          
            if(sentinelPilot.isVulnerable())
            {
                projectile.triggerAction(this);
                projectile.triggerDamage(sentinelPilot);
            }
            return true;
        }
        return false;
    }

    // Vérifie si le missile a touché une cible ennemie.
    // Si c’est le cas, la cible subit des dégâts et le missile est détruit.
    // Paramètres : aucun.
    // Retour : rien.
    public void checkMissileHit()
    {
        HashMap<String, Drone> targetMap = missile.getTargetMap();

        // Vérification si le missile à toucher un drone.
        for(Drone target : targetMap.values())
        {
            if(missile.getPosX() == target.getPosX() && missile.getPosY() == target.getPosY())
            {
                missile.triggerDamage(target);
            }
        }

        // vérification si le missile à touché la base aérienne.
        if(missile.getPosX() == airBase.getPosX() && (missile.getPosY() >= airBase.getPosY() 
        && missile.getPosY() < airBase.getPosY() + airBase.getHeightSize()))
        {
            if(airBase.isVulnerable())
            {
                missile.triggerDamage(airBase);
                airBase.onAirBasehit(this, missile);
            }
        }
    }
    
    // Méthode exécutée par la timeline qui contrôle son déclenchement.
    // Permet d’animer le jeu en assurant son rafraîchissement régulier.
    // Paramètres : aucun.
    // Retour : rien.
    private void run() {
        if (gameOver) {            
            if(!isFileWritten)
            {
                manageScore.writeScore(score, gameTime);
                isFileWritten = !isFileWritten;
            }
            return;
        }

        droneMap =  new HashMap<String, Drone>(airBase.getDroneMap());
        for(Drone drone : droneMap.values())
        {
            dronesProjectileList.addAll(drone.getProjectileList());
        }
        
        view.drawBackground();
        view.drawZoneInfo(currentRound,roundsImagePath, sentinelPilot, airBase);

        // La pilote sentinelle va se déplacer en fonction de sa direction.
        if(!sentinelPilot.isStable())
        {
            switch (sentinelPilot.getcurrentDirection()) {
                case RIGHT:
                    sentinelPilot.moveRight();
                    break;
                case LEFT:
                    sentinelPilot.moveLeft();
                    break;
            }
        }

        // Tout les élements du jeu sont dessiner (pilote sentinelle, base aerienne, drones, ...).
        sentinelPilot.performAction(this);
        ArrayList<Projectile> projectileList = new ArrayList<Projectile>();
        projectileList.addAll(dronesProjectileList); 
        projectileList.addAll(sentinelPilot.getProjectileList());
        gameElementList.addAll(0, projectileList);
        missile.performAction(this);
        
        view.drawGameElement(gameElementList);
        gameOver();

        for(Drone drone : droneMap.values())
        {
            drone.performAction(this);
        }

        // Le pilote sentinelle se téleporte lorsqu'il utilise un teleporteur vers le coté opposé.
        for(Teleporter teleporter : teleporterList){
            teleporter.performAction(this);
        }

        cloud.performAction(this);
        airBase.performAction(this);

        eatConsumableElement();
        // Les Arraylist, Hashmap sont vidés pour éviter d'avoir des éléments qui sont pas utilisés.
        droneMap.clear();
        dronesProjectileList.clear();
        gameElementList.removeAll(projectileList);

        gameTime += TIMELINE_DELAYS;
    }

    // Initialise la taille du nuage et sa position sur l’axe X chaque fois qu’il sort de la zone.
    // Paramètres : aucun.
    // Retour : rien.
    private void generateCloud()
    {
        int posX = (int) (Math.random() * getRows()/CalculationConstants.HALF_DIVIDER);
        int newWidthSize = ((int) (Math.random() * getRows()) + getZoneMinWidth());
        int newHeightSize = ((int) (Math.random() * getEnemyZoneMaxHeight()) + getEnemyZoneMinHeight());
        cloud.setPosX(posX);
        cloud.setWidthSize(newWidthSize);
        cloud.setHeightSize(newHeightSize);
    }

    // Fait apparaître un missile autoguidé consommable par le pilote sentinelle,
    // avec une chance sur cinq lorsqu’il est généré dans la zone contrôlée par le pilote.
    // Cette méthode est appelée lors de la génération du nuage.
    //
    // Paramètre : 
    //   - int chanceOfMissile : valeur définissant la probabilité d’apparition.
    //
    // Retour : rien.
    private void generateMissile(int chanceOfMissile)
    {
        if(missile.getPosX() == voidX && missile.getPosY() == voidY)
        {
            int randomNum = (int)(Math.random() * chanceOfMissile); 

            if(randomNum == chanceOfMissile)
            {
                start:
                while (true) {
                    int posX = (int) (Math.random() * ROWS);
                    int posY = COLUMNS - missile.getHeightSize();

                    if(!checkConsumableElementPosition(posX, posY))
                    {
                        continue start;
                    }

                    missile.setPosX(posX);
                    missile.setPosY(posY);
                    break;
                }
            }
        }
    }

    // Vérifie si la nouvelle position d’un élément consommable entre en collision avec un autre élément du jeu.
    //
    // Paramètres : 
    //   - Deux valeurs représentant la nouvelle position de l’élément consommable.
    //
    // Retour : un booléen indiquant si la position est libre (true) ou déjà occupée (false).
    private boolean checkConsumableElementPosition(int posX, int posY)
    {
        if (sentinelPilot.getPosX() == posX && sentinelPilot.getPosY() == posY) {
            return false;
        }

        for(GameElement consumableElement : consumableGameElementList)
        {
            if (consumableElement.getPosX() == posX && consumableElement.getPosY() == posY) {
                return false;
            }
        }

        for(Teleporter teleporter : teleporterList)
        {
            if (teleporter.getPosX() == posX) {
                return false;
            }
        }

        return true;
    }

    // Permet au pilote sentinelle de consommer les éléments disponibles,
    // tels que les pierres philosophales et les missiles autoguidés.
    // Paramètres : aucun.
    // Retour : rien.
    private void eatConsumableElement()
    {
        for (TriggerElement consumableElement : consumableGameElementList)
        {
            if(consumableElement.getPosX() == sentinelPilot.getPosX() && consumableElement.getPosY() + consumableElement.getHeightSize() == sentinelPilot.getPosY() + CalculationConstants.OFFSET)
            {
                consumableElement.triggerAction(this);
            }
        }
    }

    // Vérifie si la partie est terminée, soit parce que le pilote sentinelle est détruit,
    // soit parce que le joueur a atteint le score maximal.
    // Si l’une de ces conditions est remplie, la variable gameOver est définie à true.
    // Paramètres : aucun.
    // Retour : rien.
    private void gameOver() {
        if (!sentinelPilot.isAlive() || score == MAXIMUM_SCORE) {
            if(!sentinelPilot.isItSelfDestructing())
            {
                System.out.println("The sentinel pilot has been destroyed.");
            }

            gameOver = true;
            view.drawGameOver();
        }
    }
}