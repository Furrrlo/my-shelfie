package it.polimi.ingsw.model;

import java.util.Random;

public class MatrixGenerator {

    public MatrixGenerator() {
    }

    int ROWS = 6;
    int COLUMNS = 5;
    String[] colors = {
            "Color.GREEN",
            "Color.WHITE",
            "Color.YELLOW",
            "Color.BLUE",
            "Color.LIGHTBLUE",
            "Color.PINK",
            //"null"
    };
    String INDENT = "        ";

    public void printMatrix() {
        Random rand = new Random();
        System.out.println("Color[][] matrix = {");
        System.out.println(INDENT + "//@formatter:off");
        for (int r = 0; r < ROWS; r++) {
            StringBuilder msg = new StringBuilder(INDENT + "new Color[] { ");
            for (int c = 0; c < COLUMNS - 1; c++) {
                msg.append(String.format("%-15s", colors[rand.nextInt(colors.length)])).append(", ");
            }
            msg.append(String.format("%-15s", colors[rand.nextInt(colors.length)])).append(" },");
            System.out.println(msg);
        }
        System.out.println(INDENT + "//@formatter:on\n};");
    }

    public static void main(String[] args) {
        MatrixGenerator m = new MatrixGenerator();
        m.printMatrix();
    }
}
