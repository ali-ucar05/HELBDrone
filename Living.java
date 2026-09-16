// Cette classe centralise l’ensemble des attributs et méthodes permettant de gérer
// les points de vie des éléments vivants du jeu, tels que les drones,
// la base aérienne et le pilote sentinelle.

public abstract class Living extends GameElement {
    private int maxHealth;
    private int health;
    private boolean isVulnerable;
    private boolean isAlive = true;  
    private double timer = 0;
    private double timerDelays = 0;

    public Living(int posX, int posY, String[] imagePaths, int widthSize, int heightSize, int maxHealth, boolean isVulnerable, double timerDelays)
    {
        super(posX,posY, imagePaths, widthSize, heightSize);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.isVulnerable = isVulnerable;
        this.timerDelays = timerDelays;
    }

    public int getHealth(){return health;}

    public int getMaxHealth(){return maxHealth;}

    public void setHealth(int newHealth){health = newHealth;}

    // Vérifie si l'élément du jeu vivant est toujours en vie et retourne un booléen.
    // Paramètres : aucun.
    // Retour : un booléen indiquant si l'élément est vivant (true) ou non (false).
    public boolean isAlive(){return isAlive;}

    // Met l'élément du jeu vivant en état non vivant.
    // Paramètres : aucun.
    // Retour : rien.
    public void setNotAlive(){isAlive = !isAlive;}

    // Récupère le timer défini lorsqu’un élément du jeu vivant reçoit des dégâts.
    // Ce temps est comparé pour modifier l’état de l’élément du jeu.
    // Paramètres : aucun.
    // Retour : le temps restant sur le timer.
    public double getTimer() {return timer;}

    // Définit un nouveau timer pour l'élément du jeu vivant.
    // Paramètre : 
    //   - double newTimer : la nouvelle valeur du timer à fixer.
    // Retour : rien.
    public void setTimer(double newTimer){timer = newTimer;}

    // Récupère le délai à ajouter au timer.
    // Paramètres : aucun.
    // Retour : le délai à ajouter sous forme de double.
    public double getTimerDelays(){return timerDelays;}

    // Définit un nouveau délai à ajouter au timer.
    // Paramètre : 
    //   - double newTimerDelays : la nouvelle valeur du délai à ajouter.
    // Retour : rien.
    public void setTimerDelays(double newTimerDelays){timerDelays = newTimerDelays;}

    // Le pilote sentinelle et la base aérienne deviennent vulnérables ou invulnérables.
    // Si la touche 'O' est pressée (code de triche), cela permet de rendre le pilote sentinelle vulnérable ou invulnérable.
    // Paramètres : aucun.
    // Retour : rien.
    public void toggleVulnerable(){isVulnerable = !isVulnerable;}

    // Envoie un booléen indiquant si l'élément est vulnérable ou non.
    // Paramètres : aucun.
    // Retour : un booléen (true si vulnérable, false sinon).
    public boolean isVulnerable(){return isVulnerable;}

    // Permet d'infliger des dégâts aux éléments vivants.
    // Paramètres : aucun.
    // Retour : rien.
    public abstract void takeDamage(int damage);

    // Permet de changer la couleur de chaque élément vivant.
    // Paramètres : aucun.
    // Retour : rien.
    public abstract void changeState();
}