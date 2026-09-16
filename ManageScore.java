// Cette classe implémente le mécanisme de sauvegarde des scores et du temps de jeu
// de chaque joueur dans le fichier score.reg.
// Elle est utilisée par le Controller à la fin d’une partie pour enregistrer les résultats.
import java.util.ArrayList;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;                 
import java.io.FileNotFoundException;
import java.util.Scanner; 

public class ManageScore {
    
    // Sauvegarde le score et le temps de jeu du joueur en lui demandant d'entrer son nom.
    // Affiche également le score actuel du joueur ainsi que le meilleur score sauvegardé dans le fichier score.reg.
    // Paramètres : 
    //   - int score : le score actuel du joueur.
    //   - int gameTime : le temps de jeu écoulé.
    // Retour : rien.
    public void writeScore(int score, int gameTime)
    {
        int highScore = 0;
        String playerHighScore = "";
        int numberLineToWrite = 0;
        String pattern = ",";
        int time = 99;
        int millisDivider = 1000;
        String fileName = "score.reg";
        int maxLineArrayLength = 3;
        int minFileLine = 0;
        int playerIndex = 0;
        int scoreIndex = 1;
        int timeIndex = maxLineArrayLength-1;
        String errorMessage = "An error occurred.";

        Scanner scanner = new Scanner(System.in); 
        System.out.println("Enter username");
        String userName = scanner.nextLine();
        System.out.println("Username is: " + userName);
        File file = new File(fileName);
        ArrayList<String> linesList = new ArrayList<String>();

        try (Scanner myReader = new Scanner(file)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                linesList.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println(errorMessage);
            e.printStackTrace();
        }

        for (int i = 0; i < linesList.size(); i++)
        {
            String line = linesList.get(i);
            String[] lineArray = line.split(pattern);
            if(lineArray.length >= maxLineArrayLength)
            {
                int scoreFile = Integer.parseInt(lineArray[scoreIndex]);
                int timeFile = Integer.parseInt(lineArray[timeIndex]);
                
                if(score > scoreFile || ( score == scoreFile && gameTime < timeFile))
                {
                    numberLineToWrite = i;
                    break;
                }
            }
        }

        if(numberLineToWrite != minFileLine)
        {
            linesList.add(numberLineToWrite, userName+","+score+","+gameTime/millisDivider);
        }
        else
        {
            linesList.add(userName+","+score+","+gameTime/millisDivider);
        }

        try {
                FileWriter myWriter = new FileWriter(fileName);
                for(String line : linesList)
                {
                    myWriter.write(line+"\n");
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println(errorMessage);
                e.printStackTrace();
            }
            
        try (Scanner myReader = new Scanner(file)) {
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] lineArray = line.split(pattern);
                if(Integer.parseInt(lineArray[scoreIndex]) > highScore)
                {
                    highScore = Integer.parseInt(lineArray[scoreIndex]);
                    playerHighScore = lineArray[playerIndex];
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println(errorMessage);
            e.printStackTrace();
        }
        
        System.out.println("Your score is " + score);
        System.out.println("The highest score is " + playerHighScore + " : " + highScore);
        System.out.println("Successfully wrote to the file.");
    }
}