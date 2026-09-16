// Cette classe représente le point d’entrée du jeu.
// Elle initialise le contrôleur et configure les éléments nécessaires au lancement du jeu.
// C’est dans cette classe que l’application est exécutée et que le cycle principal du jeu démarre.
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Controller Controller = new Controller(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
