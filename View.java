// La classe View représente l’ensemble des éléments visuels du jeu destinés à l’utilisateur.
// Elle gère l’affichage des composants graphiques dans la fenêtre du jeu.
// Elle est utilisée par la classe Controller pour dessiner et mettre à jour les éléments visuels à l’écran.
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.shape.Line;
import javafx.stage.Stage;
import java.util.ArrayList;

public class View {
    private Stage stage;
    private Scene scene;
    private GraphicsContext gc;

    public View(Stage stage)
    {
        this.stage = stage;
        String stageTitle = "HELBDrone";
        stage.setTitle(stageTitle);
        Group root = new Group();
        Canvas canvas = new Canvas(Controller.getWidth(), Controller.getHeight());
        root.getChildren().add(canvas);
        this.scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        gc = canvas.getGraphicsContext2D();
    }

    public Scene getScene(){return scene;}

    // dessine le fond du jeu.
    public void drawBackground() {
        String backgroundColor = "FFFFFF";
        gc.setFill(Color.web(backgroundColor));
        for (int i = 0; i < Controller.getRows(); i++) {
            for (int j = 0; j < Controller.getColumns(); j++) {
                gc.fillRect(i * Controller.getSquareSize(), j * Controller.getSquareSize(), 
                Controller.getSquareSize(), Controller.getSquareSize());
            }
        }
    }

    // dessine la zone informationelle.
    public void drawZoneInfo(int round,String[] roundsImagePath, SentinelPilot sentinelPilot, AirBase airBase)
    {
        Image sentinelPilotHearthImage = new Image(sentinelPilot.getPathToImage(sentinelPilot.getPathToImageLen()-CalculationConstants.OFFSET));
        for(int healthPoint = 0; healthPoint < sentinelPilot.getHealth(); healthPoint++)
        {
            gc.drawImage(sentinelPilotHearthImage, healthPoint * Controller.getSquareSize(), 
            Controller.getInformationalZoneMinHeight() * Controller.getSquareSize(), 
            Controller.getSquareSize(), Controller.getSquareSize());
        }

        if(round > Controller.getMaxRounds())
        {
            round--;
        }

        Image roundImage = new Image(roundsImagePath[round-CalculationConstants.OFFSET]);
        gc.drawImage(roundImage, (Controller.getRows()-CalculationConstants.OFFSET)/CalculationConstants.HALF_DIVIDER * Controller.getSquareSize(), 
        Controller.getInformationalZoneMinHeight() * Controller.getSquareSize(), 
        Controller.getSquareSize(),Controller.getSquareSize());


        Image airBasePilotHearthImage = new Image(airBase.getPathToImage(airBase.getPathToImageLen()-CalculationConstants.OFFSET));
        for(int healthPoint = 0; healthPoint < airBase.getHealth(); healthPoint++)
        {
            gc.drawImage(airBasePilotHearthImage, (Controller.getRows()-CalculationConstants.OFFSET-healthPoint) * Controller.getSquareSize(),
            Controller.getInformationalZoneMinHeight() * Controller.getSquareSize(), 
            Controller.getSquareSize(), Controller.getSquareSize());
        }
    }

    // dessine les éléments du jeu.
    public void drawGameElement(ArrayList<GameElement> gameElementList)
    {
        for (GameElement gameElement : gameElementList)
        {
            Image gameElementImage = new Image(gameElement.getPathToImage(gameElement.getCurrentImageIndex()));
            gc.drawImage(gameElementImage, gameElement.getPosX() * Controller.getSquareSize(), 
            gameElement.getPosY() * Controller.getSquareSize(), Controller.getSquareSize() * gameElement.getWidthSize(), Controller.getSquareSize()*gameElement.getHeightSize());
        }
    }

    // dessine le message de fin de partie.
    public void drawGameOver()
    {
        String fontName = "Digital-7";
        int fontSize = 70;
        String victoryMessage = "Victory !";
        String defeatMeassage = "Game Over";
        double widthDivider = 3.5;
        int heightDivider = 2; 

        if(Controller.getCurrentRound()> Controller.getMaxRounds())
        {
            gc.setFill(Color.GREEN);
            gc.setFont(new Font(fontName, fontSize));
            gc.fillText(victoryMessage, Controller.getWidth() / widthDivider, Controller.getHeight() / heightDivider);
        }
        else
        {
            gc.setFill(Color.RED);
            gc.setFont(new Font(fontName, fontSize));
            gc.fillText(defeatMeassage, Controller.getWidth() / widthDivider, Controller.getHeight() / heightDivider);
        }
    }
}