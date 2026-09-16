// Cette classe représente les projectiles du jeu.
// Elle centralise les méthodes permettant de gérer leur comportement,
// notamment leur déplacement et leur traitement en cours de partie.
// Le mode de déplacement varie selon que le projectile est tiré par le pilote
// ou par un drone.
public class Projectile extends DamagingElement{
    private static final String PROJECTILE_IMAGES[] = {"img/sentinel_pilot_bullets.png", "img/drone_bullets.png"};
    private static final int PROJECTILE_WIDTH_SIZE = 1;
    private static final int PROJECTILE_HEIGHT_SIZE = PROJECTILE_WIDTH_SIZE;
    private static final int PROJECTILE_DAMAGE = 1;
    private boolean isShootByDrone = false;
    
    public Projectile(int posX, int posY, boolean isShootByDrone)
    {
        super(posX, posY, PROJECTILE_IMAGES, PROJECTILE_WIDTH_SIZE, PROJECTILE_HEIGHT_SIZE, PROJECTILE_DAMAGE);
        this.isShootByDrone = isShootByDrone;
        if(isShootByDrone)
        {
            setCurrentImageIndex(getPathToImageLen()-CalculationConstants.OFFSET);
        }
    }

    @Override
    public void triggerAction(Controller gameBoard){hide();}

    @Override
    public void triggerDamage(Living enemy){
        hitEnemy(enemy);
    }

    @Override
    public void performAction(Controller gameBoard){move();}

    // Déplace les projectiles de manière différente selon qu'ils ont été tirés par un drone
    // ou par le pilote sentinelle.
    // Paramètres : aucun.
    // Retour : rien.
    public void move()
    {

        if(isShootByDrone)
        {
            setPosY(getPosY()+CalculationConstants.OFFSET);
        }
        else
        {
            setPosY(getPosY()-CalculationConstants.OFFSET);
        }
    }

    // Vérifie si le projectile a été tiré par un drone.
    // Retour : un booléen (true si tiré par un drone, false sinon).
    public boolean isTheProjectileShootByDrone(){return isShootByDrone;}
}