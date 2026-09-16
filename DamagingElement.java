// Cette classe centralise les méthodes permettant d’infliger des dégâts à l’ennemi
// lors d’une collision.
// Elle implémente également les attributs liés aux dégâts et à leur gestion.

public abstract class DamagingElement extends TriggerElement {
    private int damage = 1;

    public DamagingElement(int posX, int posY, String[] imagePaths,int widthSize, int heightSize, int damage)
    {
        super(posX, posY, imagePaths, widthSize, heightSize);
        this.damage = damage;
    }

    public int getDamage(){return damage;}

    // Inflige des dégâts à un ennemi.
    // Paramètre : 
    //   - Living enemy : l’ennemi qui reçoit les dégâts.
    // Retour : rien.
    public void hitEnemy(Living enemy){enemy.takeDamage(getDamage());}

    // Inflige des dégâts à un ennemi lorsque l’élément entre en collision avec celui-ci.
    // Paramètres : 
    //   - Living enemy : l’ennemi qui reçoit les dégâts.
    // Retour : rien.
    public abstract void triggerDamage(Living enemy);
}