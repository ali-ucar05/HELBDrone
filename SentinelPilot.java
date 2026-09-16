// Cette classe représente le pilote sentinelle.
// Elle centralise les méthodes permettant de gérer son comportement
// ainsi que son traitement durant la partie.
// Elle est utilisée pour instancier le pilote sentinelle contrôlé par le joueur.
import java.util.ArrayList;

class SentinelPilot extends Shooter{
    private static final String[] SENTINEL_PILOT_IMAGES = {"/img/sentinel_pilot.png", "img/sentinel_pilot_hit.png","/img/invulnerable_sentinel_pilot.png" ,"/img/sentinel_pilot_heart.png"};
    private static final int SENTINEL_PILOT_MAX_HEALTH = 3;
    private static final boolean IS_SENTINEL_PILOT_Vulnerable = true;
    private static final double SENTINEL_PILOT_TIMER_DELAYS = 0.2;
    private static final double SENTINEL_PILOT_SHOOTING_TIMEDELAYS = 0.5;
    private static final int SENTINEL_PILOT_WIDTH_SIZE = 1;
    private static final int SENTINEL_PILOT_HEIGHT_SIZE = SENTINEL_PILOT_WIDTH_SIZE;
    private int currentDirection = 0; 
    private boolean isStable = false; 
    private boolean hasSelfDestructing = false;
    private boolean hasMisile = false; 
    private double invulnerabilityTime = 0;
    private boolean hasEatenPhilosopherStone = false;

    public SentinelPilot(int posX, int posY)
    {
        super(posX, posY ,SENTINEL_PILOT_IMAGES,SENTINEL_PILOT_WIDTH_SIZE, SENTINEL_PILOT_HEIGHT_SIZE, 
        SENTINEL_PILOT_MAX_HEALTH, IS_SENTINEL_PILOT_Vulnerable, SENTINEL_PILOT_TIMER_DELAYS, SENTINEL_PILOT_SHOOTING_TIMEDELAYS);
    }

    @Override
    public Projectile createProjectile()
    {
        boolean isShootByDrone = false;
        Projectile projectile = new Projectile(getPosX(),getPosY()+CalculationConstants.OFFSET, isShootByDrone);
        return projectile;
    }

    @Override
    public void changeState()
    {
        int subtrahendIndex = 2;
        int firstImageIndex = 0;
        int secondImageIndex = getPathToImageLen() / getPathToImageLen();
        int lastImageIndex = getPathToImageLen() - subtrahendIndex;


        if(isVulnerable())
        {
            if(getCurrentImageIndex() == firstImageIndex)
            {
                setCurrentImageIndex(secondImageIndex);
            }
            else
            {
                setCurrentImageIndex(firstImageIndex);
            }
        }
        else
        {
            if(isAlive())
            {
                setCurrentImageIndex(lastImageIndex);
            }
            else
            {
                setCurrentImageIndex(secondImageIndex);
            }
        }
    }

    @Override
    public void performAction(Controller gameBoard)
    {
        super.performAction(gameBoard);

        // on vérifie si le pilote sentinelle à consommé une pierre philosophale 
        // on vérifie par la suite si l'effet de invulnérabilité 
        // est terminer pour le remettre en vulnérable et aussi sa couleur initiale.
        if(hasSentinelPilotAte())
        {
            if(checkPhilospherStoneVunerabilityTimeEffectEnd())
            {
                setSentinelPilotVulnerable(gameBoard.getTeleporterList());
            }
        }
    }

    // Retourne la direction du pilote sentinelle.
    // Paramètres : aucun.
    // Retour : la direction actuelle du pilote sentinelle.
    public int getcurrentDirection(){return currentDirection;}

    // Fixe une nouvelle direction au pilote sentinelle.
    // Paramètre : 
    //   - int newDirection : la nouvelle direction à assigner au pilote sentinelle.
    // Retour : rien.
    public void setCurrentDirection(int newDirection){currentDirection = newDirection;}

    // Fige le pilote sentinelle ou défige le pilote sentinelle.
    public void toggleStable(){isStable = !isStable;}

    // Renvoie un booléen indiquant si le pilote sentinelle est stable.
    // Paramètres : aucun.
    // Retour : un booléen (true si stable, false sinon).
    public boolean isStable(){return isStable;}

    // Ces méthodes permettent de déplacer le pilote sentinelle en fonction de sa direction.
    public void moveRight()
    {
        setPosX(getPosX()+CalculationConstants.OFFSET);
    }

    public void moveLeft()
    {
        setPosX(getPosX()-CalculationConstants.OFFSET);
    }

    // Auto-destruction du pilote sentinelle lorsqu'une touche 'M' est pressée (code de triche).
    // Paramètres : aucun.
    // Retour : rien.
    public void autoDestroySentinelPilot()
    {
        setHealth(CalculationConstants.MIN_HEALTH);
        setNotAlive();
        changeState();
        hasSelfDestructing = true;
    }

    // Renvoie un booléen indiquant si le pilote sentinelle s'est auto-détruit.
    // Paramètres : aucun.
    // Retour : un booléen (true si le pilote s'est auto-détruit, false sinon).
    public boolean isItSelfDestructing(){return hasSelfDestructing;}

    public boolean hasSentinelPilotAte(){return hasEatenPhilosopherStone;}

    public void toggleHasEatenPhilosopherStone(){hasEatenPhilosopherStone = !hasEatenPhilosopherStone;}

    public double getInvulnerabilityTime(){return invulnerabilityTime;}
    
    public void setInvulnerabilityTime(double invulnerabilityTimedelays)
    {
        invulnerabilityTime = (System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER) + invulnerabilityTimedelays;
    }

    // Rendra le pilote sentinelle invulnérable après la consommation de la pierre philosophale.
    // Cette méthode prend en paramètre une liste de téléporteurs et les désactive.
    // Paramètre : 
    //   - List<Teleporter> teleporterList : la liste des téléporteurs à désactiver.
    // Retour : rien.
    public void setSentinelPilotInvulnerable(double invulnerabilityTimedelays,ArrayList<Teleporter> teleporterList)
    {
        toggleVulnerable(); // Le pilote sentinelle devient invulnérable.
        setInvulnerabilityTime(invulnerabilityTimedelays); // Le timestamp est enregistrer.
        toggleHasEatenPhilosopherStone(); // Le pilote sentinelle a manger une pierre philosophale.
        for (Teleporter teleporter : teleporterList) // Les téleporteurs sont désactivés.
        {
            teleporter.toggleActive();
        }
        System.out.println("The pilot sentinel is inVulnerable");
    }

    // Ces méthodes permet d'activer ou désactiver la possesion du missile par le pilote sentinelle.
    public void toggleMissile(){hasMisile = !hasMisile;}

    public void equipMissile(){hasMisile=true;}

    // Active et tire le missile autoguidé lorsque la touche 'F' est appuyée,
    // à condition que le pilote sentinelle ait consommé un missile autoguidé.
    // Paramètres : aucun.
    // Retour : rien.
    public void shootMissile(Missile missile)
    {
        if(hasMisile)
        {
            missile.setPosX(getPosX());
            missile.setPosY(getPosY()-CalculationConstants.OFFSET);
            toggleMissile();
            missile.toggleFollowTarget();
            System.out.println("A missile has been shooted.");
        }
    }

    // Vérifie si l'effet de vulnérabilité de la pierre philosophale est terminé et renvoie un booléen.
    // Paramètres : aucun.
    // Retour : un booléen (true si l'effet est terminé, false sinon).
    private boolean checkPhilospherStoneVunerabilityTimeEffectEnd()
    {
        if(System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER >= getInvulnerabilityTime())
        {
            return true;
        }
        return false;
    }

    // Rendra le pilote sentinelle vulnérable après la fin des effets de la pierre philosophale.
    // Cette méthode prend en paramètre une liste de téléporteurs et les active.
    // Paramètre : 
    //   - List<Teleporter> teleporterList : la liste des téléporteurs à activer.
    // Retour : rien.
    private void setSentinelPilotVulnerable(ArrayList<Teleporter> teleporterList)
    {
        toggleVulnerable();
        toggleHasEatenPhilosopherStone();
        changeState();

        for (Teleporter teleporter : teleporterList)
        {
            teleporter.toggleActive();
        }
        System.out.println("The pilot sentinel is Vulnerable");
    }
}