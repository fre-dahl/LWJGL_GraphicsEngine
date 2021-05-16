package v2.graphics;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Stack;

public class ColorPalette {

    public Color[] colors;

    public ColorPalette(String filePath) {

        Stack<String> hexStack = new Stack<>();

        File file = new File(filePath);

        try {
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {

                hexStack.push(scanner.nextLine());
            }
            scanner.close();
        }
        catch (IOException e) {

            System.out.println(e.toString());
        }

        colors = new Color[hexStack.size()];

        for (int i = colors.length - 1; i > -1; i--) {

            String hex = hexStack.pop();

            assert (hex.length() < 10)
                    : "(ColorPalette) Invalid hex-format.\n\n" +
                    "Try: 6, 8 or 9 (with #) chars," +
                    "separated by a new line";

            colors[i] = Color.fromHex(hex);
        }
    }

    public void sort() {
        Arrays.sort(colors);
    }
}
