package pw.wiped.util;

import java.io.*;

/**
 * This class is meant to manage all the input/output Nootbot needs to do
 */
public class IO {

    /**
     * A simple method to read the configfile
     * @param filename Path to the file
     * @return The file's content
     */
    public static String readFile (String filename) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(filename));
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line);
            line = br.readLine();
        }
        return sb.toString();
    }

    public static void writeFile(File configFile, String configString) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(configFile));
        bw.write(configString);
        bw.close();
    }
}
