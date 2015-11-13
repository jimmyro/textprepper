
package textprepperfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * @author jodonnell
 */
public class TextPrepperFX extends Application {
    
    private static final String WHITAKER = 
            "http://www.archives.nd.edu/cgi-bin/wordz.pl?keyword=";
    private static final BufferedReader stdIn = 
            new BufferedReader(new InputStreamReader(System.in));
    
    @Override
    public void start(Stage primaryStage) {
        
        primaryStage.setTitle("Latin Vocabulary Generator");
        
        //Configure grid pane
        final GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        //Layout
        final Text scenetitle = new Text("Latin Vocabulary Generator");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 18));
        grid.add(scenetitle, 0, 0, 2, 1);
        
        final TextField titleField = new TextField();
        titleField.setPromptText("Enter title or other descriptor.");
        grid.add(titleField, 0, 1, 2, 1);
        
        final TextArea textArea = new TextArea();
        textArea.setPromptText("Enter words separated by spaces.");
        textArea.setWrapText(true);
        grid.add(textArea, 0, 2, 2, 1);
        
        final Button submit = new Button("Generate");
        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(submit);
        grid.add(hbBtn, 1, 4);
        
        final Text notif = new Text();
        grid.add(notif, 1, 5);
        
        //Event handlers
        submit.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent e) {
                if (titleField.getText().equals("") ||
                        textArea.getText().equals("")) {
                    notif.setFill(Color.FIREBRICK);
                    notif.setText("Must enter text in all fields");
                } else {
                    preptext(titleField.getText().trim(), 
                     WHITAKER + textArea.getText().trim().replaceAll(" ", "+"));
                }
            }
        });
        
        //Set scene and show stage
        Scene scene = new Scene(grid, 300, 400);
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

    protected void preptext(String label, String queryURL) {
        //Declarations
        BufferedReader reader;
        PrintWriter writer;
        File output = new File(label + ".txt");

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
            
            reader.close(); writer.close(); //close streams
        } catch (Exception e) {
            Platform.exit();
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
