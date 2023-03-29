package it.polimi.ingsw.model;
import java.util.Random;

public class MatrixGenerator {

    public MatrixGenerator() {}
    int ROWS = 6;
    int COLUMNS = 5;
    String[] colors = {
            "Color.GREEN",
            "Color.YELLOW",
            "Color.ORANGE",
            "Color.BLUE",
            "Color.LIGHTBLUE",
            "Color.PINK",
            "null"
            };
    public void printMatrix(){
        Random rand = new Random();
        System.out.println("Color[][] matrix = {");
        for( int r=0; r<ROWS; r++ ){
            String msg = "new Color[] { ";
            for( int c=0; c<COLUMNS-1; c++){
                msg = msg + String.format("%-15s", colors[rand.nextInt(colors.length)]) + ", ";
            }
            msg = msg + String.format("%-15s", colors[rand.nextInt(colors.length)]) + " },";
            System.out.println(msg);
        }
    }

    public static void main(String[] args) {
        MatrixGenerator m = new MatrixGenerator();

        m.printMatrix();
    }
}

