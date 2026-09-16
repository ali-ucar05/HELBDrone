// Cette classe centralise la méthode trigger utilisée pour la gestion des collisions.
public abstract class TriggerElement extends GameElement {
    public TriggerElement(int posX, int posY, String[] imagePaths, int widthSize, int heightSize)
    {
        super(posX, posY, imagePaths, widthSize, heightSize);
    }

    // Permet aux éléments d’effectuer leurs actions lorsqu’ils sont activés/ collisons.
    // Paramètres : aucun.
    // Retour : rien.
    public abstract void triggerAction(Controller gameBoard);
}