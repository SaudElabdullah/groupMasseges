package sample;

import java.io.IOException;
import java.io.Writer;

import java.util.ArrayList;

// class to add the data to the file as CSV
public class CsvFile {


    private static final char separator = ',';

    // method to get the needed information of the new contact
    public static void writeLine(Writer file, ArrayList<String> contactInfo) throws IOException {
        writeLine(file, contactInfo, separator, ' ');
    }

    private static String followCVSformat(String value) {

        String result = value;
        if (result.contains("\"")) {
            result = result.replace("\"", "\"\"");
        }
        return result;

    }

    //method to convert the arrayList of contact's information to CSV file
    public static void writeLine(Writer w, ArrayList<String> values, char separators, char customQuote) throws IOException {

        boolean first = true;

        //default customQuote is empty
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (!first) {
                sb.append(separators);
            }
            if (customQuote == ' ') {
                sb.append(followCVSformat(value));
            }

            first = false;
        }
        sb.append("\n");
        w.append(sb.toString());
    }
}