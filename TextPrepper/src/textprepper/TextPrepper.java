
package textprepper;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;

public class TextPrepper {
    
    private static final String WHITAKER = 
            "http://www.archives.nd.edu/cgi-bin/wordz.pl?keyword=";
    private static final BufferedReader stdIn = 
            new BufferedReader(new InputStreamReader(System.in));
    /**
     * @param args the command line arguments
     * @throws IOException 
     */
    public static void main(String[] args) throws IOException {
        
        StringBuilder s = new StringBuilder("one+two+three+four+");
        
        System.out.println(s.delete(0, s.indexOf("+")));
        
        /*
        //Declarations
        BufferedReader reader = null;
        PrintWriter writer = null;
        File output;
        String label, queryURL = WHITAKER;

        //Get user input
        if (args.length > 1) {
            label = args[0];
            
            for (int i = 1; i < args.length; i++) {
                queryURL += (args[i] + "+");
            }
            queryURL = queryURL.substring(0, queryURL.length() - 1);
        } else {
            System.out.println("Enter a title/label for this text: ");
            label = stdIn.readLine(); 
            System.out.println();

            System.out.println("Enter words to define, separated by spaces: ");
            queryURL += stdIn.readLine().trim().replaceAll(" ", "+");
            System.out.println();

            stdIn.close();
        }
        
        output = new File(label + ".txt");

        try {
            //Create a URLConnection to the results page and get input stream
            reader = new BufferedReader(new InputStreamReader(
                    new URL(queryURL).openConnection().getInputStream()));
            reader.readLine(); //Skip title
            
            //Prepare to write edited results to a file
            writer = new PrintWriter(new FileWriter(output));
            writer.println("<!DOCTYPE html><html><body><h2>" 
                    + label + "</h2>"); //opening tags and title
            
            //Logic for editing results
            String line;
            boolean foundDef = false;
            
            while ((line = reader.readLine()) != null) {
                if (foundDef) {
                    if (line.trim().endsWith(";")) { 
                        writer.print(line + " ");
                    } else {
                        writer.println("<br/>"); //end of an entry -> new line
                        foundDef = false;
                    }
                } else {
                    if (line.trim().endsWith("UNKNOWN")) { 
                        //check for unknown words
                        writer.print("<b>" + 
                                line.substring(0, line.indexOf("=")).trim() 
                                + "</b>  UNKNOWN<br/>");
                        continue;
                    } else if (line.contains("[")) { 
                        //found an entry
                        writer.print("<b>" + 
                                line.substring(0, line.indexOf("[")).trim()
                                + "</b>  ");
                        foundDef = true;
                        continue;
                    }
                }
            }
            
            writer.println("</body></html>"); //closing tags
        } catch (IOException e) {
            System.err.println("An error occurred: ");
            System.err.println(e.getMessage());
            System.exit(1);
        } finally {
            if (reader != null) { reader.close(); }
            if (writer != null) { writer.close(); }
        }
        
        Desktop.getDesktop().browse(output.toURI());*/
    }
}
