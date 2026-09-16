// Cette classe implémente l’ensemble des attributs et méthodes liés au système de tir
// pour certains éléments du jeu, tels que le pilote sentinelle ou les drones.
// Elle fournit notamment la méthode shoot, permettant de gérer la création
// et l’émission des projectiles.

import java.util.ArrayList;

public abstract class Shooter extends Living {
    private ArrayList<Projectile> projectileList = new ArrayList<Projectile>();
    private double shootingTime = 0;
    private double shootingTimedelays = 1;

    public Shooter(int posX, int posY, String[] imagePaths, int widthSize, int heightSize, int maxHealth, boolean isVulnerable, double timerDelays, double shootingTimedelays){
        super(posX, posY, imagePaths, widthSize, heightSize, maxHealth, isVulnerable, timerDelays);
        this.shootingTimedelays = shootingTimedelays;
    }

    @Override
    public void takeDamage(int damage)
    {
        if(isVulnerable())
        {
            if(getHealth() > CalculationConstants.MIN_HEALTH)
            {
                setHealth(getHealth()-damage);
                setTimer((System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER) + getTimerDelays());   
                changeState();
                if(getHealth() <= CalculationConstants.MIN_HEALTH)
                {
                    setNotAlive();
                }
            }
        }
    }

    @Override
    public void changeState(){};

    @Override
    public void performAction(Controller gameBoard){
        // Déplacement des projectiles
        generateProjectile(gameBoard);
        
        // Vérifie les projectiles dans la liste.
        // pour les retirer et faire subir des dégats aux cibles.
        checkProjectileInProjectileList(gameBoard);

        // La couleur de l'élément se remet à l'initial apres un délais après avoir été touché.
        restoreInitialColorAfterDelay();
    }

    // Permet aux éléments de tirer des projectiles après un court délai
    // et ajoute les projectiles à leur liste correspondante.
    // Paramètres : aucun.
    // Retour : rien.
    public void shoot()
    {
        long currentTime = System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER;

        // On vérifie si le temps actuel est plus grand que le temps du tir précédent.
        if(currentTime > getShootingTime() + getshootingTimedelays())
        {
            // Ajoute le projectile dans la liste.
            addProjectile(createProjectile()); 
            // Fixe temps du nouveau tir.
            setShootingTime(System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER); 
        }
    }

    // Crée le projectile tiré.
    // Paramètres : aucun.
    // Retour : le projectile créé.
    public abstract Projectile createProjectile();

    public ArrayList<Projectile> getProjectileList(){return projectileList;}

    public void addProjectile(Projectile projectile){projectileList.add(projectile);}

    public void removeProjectileInList(ArrayList projectileRemoveList){projectileList.removeAll(projectileRemoveList);}

    // Permet de déplacer les projectiles tirés par l'élément.
    // Paramètres : aucun.
    // Retour : rien.
    public void generateProjectile(Controller gameBoard)
    {
        ArrayList<Projectile> projectileList = getProjectileList();
        if(projectileList.size() > CalculationConstants.MIN_SIZE)
        {
            for(Projectile projectile : projectileList)
            {
                projectile.performAction(gameBoard);
            }
        }
    }

    // Récupère le temps du tir avec le délai afin de permettre de retirer après le délai,
    // en le comparant avec le temps actuel.
    // Retour : un double représentant le temps restant avant de pouvoir tirer à nouveau.
    public double getShootingTime(){return shootingTime;}

    // Fixe le dernier temps de tir.
    // Paramètre : 
    //   - double newShootingTime : la nouvelle valeur du temps de tir.
    // Retour : rien.
    public void setShootingTime(double newShootingTime){shootingTime = newShootingTime;}

    // Récupère le temps de délai du tir.
    // Retour : un double représentant le délai du tir.
    public double getshootingTimedelays(){return shootingTimedelays;}
    
    // Supprime les projectiles du game element lorsqu’ils dépassent la zone limite
    // ou lorsqu’ils touchent leur cible, afin d’optimiser les ressources.
    // Paramètres : aucun.
    // Retour : rien.
    private void checkProjectileInProjectileList(Controller gameBoard)
    {
        ArrayList<Projectile> removeProjectilesList = new ArrayList<Projectile>();

        // Tout les projectiles de la liste sont parcouru
        for(Projectile projectile : projectileList)
        {
            // on vérifie si le projectil est tiré par un drone ou le pilote sentinelle
            // si le projectile à touché une cible ou est sortie de la zone le projectile est ajouté
            // dans une lsite des projectiles qui doit etre retirer.
            if(projectile.isTheProjectileShootByDrone())
            {
                if(gameBoard.checkDroneProjectileOutside(projectile) || gameBoard.checkDronetHit(projectile))
                {
                    removeProjectilesList.add(projectile);
                }
            }
            else
            {
                if(gameBoard.checkSentinelPilotProjectileOutside(projectile) || gameBoard.checkSentinelPilotHit(projectile))
                {
                    removeProjectilesList.add(projectile);
                }
            }
        }
        // les projectiles qui doivent être retiré sont retirer de la liste
        removeProjectileInList(removeProjectilesList);
    }

    // Restaure la couleur initiale de l’élément après un délai,
    // une fois que celui-ci a été touché.
    // Paramètres : aucun.
    // Retour : rien.
    private void restoreInitialColorAfterDelay()
    {
        if(getTimer() != CalculationConstants.TIMER_MIN_VALUE)
        {
            long currentTime = System.currentTimeMillis() / CalculationConstants.MILLIS_DIVIDER;
            if(currentTime > getTimer())
            {
                changeState();
                setTimer(CalculationConstants.TIMER_MIN_VALUE);
            }
        }
    }
}
