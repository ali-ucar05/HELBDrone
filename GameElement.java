// La classe abstraite GameElement centralise les attributs et méthodes définissant
// de manière générale un élément du jeu.
// Elle sert de classe mère pour tous les éléments du jeu, qui en héritent afin
// de partager une structure et un comportement communs.

public abstract class GameElement {

    private final String[] IMAGE_PATHS ;
    private int posX;
    private int posY;
    private int currentImageIndex = 0;
    private int widthSize = 1;
    private int heightSize = 1;
    
    public GameElement(int posX, int posY, String[] imagePaths, int widthSize, int heightSize){
        this.posX = posX; 
        this.posY = posY;
        this.IMAGE_PATHS = imagePaths;
        this.widthSize = widthSize;
        this.heightSize = heightSize;
    }
    
    public int getPosX(){return posX;}
    
    public void setPosX(int newPosX){posX = newPosX;}
    
    public int getPosY(){return posY;}
    
    public void setPosY(int newPosY){posY = newPosY;}
    
    public String getPathToImage(int index){return IMAGE_PATHS[index];}
    
    public int getPathToImageLen(){return IMAGE_PATHS.length;}

    public int getCurrentImageIndex(){return currentImageIndex;}

    public void setCurrentImageIndex(int newIndex){currentImageIndex = newIndex;}

    public int getWidthSize(){return widthSize;}

    public int getHeightSize(){return heightSize;}

    public void setHeightSize(int newHeightSize){heightSize = newHeightSize;}

    public void setWidthSize(int newWidthSize){widthSize = newWidthSize;}

    // Permet de cacher les élements après consomation ou détruit.
    public void hide()
    {
        setPosX(Controller.getVoidX());
        setPosY(Controller.getVoidY());
    }

    // Permet aux éléments d’effectuer leurs actions.
    // Paramètres : aucun.
    // Retour : rien.
    public abstract void performAction(Controller gameBoard);
}