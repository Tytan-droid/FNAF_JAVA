package Class;

import java.io.*;

public class SaveManager {

    private static final String SAVE_FILE = "save.dat";

    public static int loadNight() {
        File file = new File(SAVE_FILE);
        if (!file.exists()) return 1;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line = br.readLine();
            if (line != null && line.startsWith("night=")) {
                return Integer.parseInt(line.substring(6));
            }
        } catch (Exception ignored) {}

        return 1;
    }

    public static void saveNight(int night) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(SAVE_FILE))) {
            pw.println("night=" + night);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void reset() {
        File file = new File(SAVE_FILE);
        if (file.exists()) file.delete();
    }
}
