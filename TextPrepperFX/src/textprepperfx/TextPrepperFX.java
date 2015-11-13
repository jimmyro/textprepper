
package textprepperfx;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class TextPrepperFX extends Application {
    
    private static final String FROM_LATIN = 
            "http://www.archives.nd.edu/cgi-bin/wordz.pl?keyword=";
    private static final String FROM_ENGLISH = 
            "http://www.archives.nd.edu/cgi-bin/wordz.pl?english=";
    private static final int MAX_LENGTH = 250;
    private static final Text PATH = new Text(System.getProperty("user.home"));
    private static final Text NOTIF = new Text();
    private static boolean BUSY = false;
    
    /* TO-DO:
     * Work on ask mode formatting
     * Start thinking about how to make parsing interactive... might require
     *      substantial reorganizing/restructuring of the code.  D:
     * 
     * Fast, pretty way to strip punctuation?
     *      Should probably make a point of getting familiar with regex
     * 
     * BUGS:
     * Fix the double [XXXXX] problem (change parsing algorithm)
     * -UNTESTED ATTEMPT- Find cause of occasional duplicate entries
     */
    
    @Override
    public void start(final Stage primaryStage) {
        
        primaryStage.setTitle("Latin Vocab Generator");
        
        /* * * * * * * * * * * * * * * * * *
         * AUTO MODE FORMATTING
         * * * * * * * * * * * * * * * * * */
        
        //Configure grid pane
        final GridPane autoMode = new GridPane();
        autoMode.setAlignment(Pos.TOP_CENTER);
        autoMode.setHgap(10);
        autoMode.setVgap(10);
        autoMode.setPadding(new Insets(5, 5, 5, 5));

        //labels
        final Text sceneTitle = new Text("Latin Vocab Generator");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 22));
        autoMode.add(sceneTitle, 0, 0, 2, 1);
        
        //title field
        final Text titleFieldLabel = new Text("Title:");
        titleFieldLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        autoMode.add(titleFieldLabel, 0, 1);
        
        final TextField titleField = new TextField();
        titleField.setPromptText("Enter title.");
        autoMode.add(titleField, 1, 1);
        
        //radio buttons
        final Text langLabel = new Text("Input language:");
        langLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        autoMode.add(langLabel, 0, 2, 2, 1);
        
        final RadioButton latin = new RadioButton("Latin");
        latin.setSelected(true);
        
        final RadioButton english = new RadioButton("English");
        
        ToggleGroup langGroup = new ToggleGroup();
        langGroup.getToggles().setAll(latin, english);
        
        HBox langBox = new HBox(10);
        langBox.setAlignment(Pos.CENTER_RIGHT);
        langBox.setPadding(new Insets(0, 10, 0, 0));
        langBox.getChildren().setAll(latin, english);
        
        autoMode.add(langBox, 1, 2);
        
        //checkboxes and auto/ask thingy
        final CheckBox alphabetize = new CheckBox("Alphabetize");
        alphabetize.setSelected(false);
        
        final Separator toggleSep = new Separator();
        toggleSep.setOrientation(Orientation.VERTICAL);
        
        final ToggleButton auto = new ToggleButton("Auto");
        auto.setSelected(true);
        
        final ToggleButton ask = new ToggleButton ("Ask");
        
        ToggleGroup modeGroup = new ToggleGroup();
        modeGroup.getToggles().setAll(auto, ask);
        
        HBox toggleBox = new HBox(0);
        toggleBox.setAlignment(Pos.CENTER_RIGHT);
        toggleBox.setPadding(new Insets(0, 0, 0, 0));
        toggleBox.getChildren().setAll(auto, ask);
        
        HBox modeBox = new HBox(14);
        modeBox.setAlignment(Pos.CENTER);
        modeBox.setPadding(new Insets(0, 0, 0, 0));
        modeBox.getChildren().setAll(alphabetize, toggleSep, toggleBox);
        
        autoMode.add(modeBox, 0, 3, 2, 1);
        
        //query field
        final TextArea textArea = new TextArea();
        textArea.setPromptText("Enter words separated by spaces.");
        textArea.setWrapText(true);
        autoMode.add(textArea, 0, 4, 2, 1);    
        
        //submit button
        HBox hbBtn = new HBox(15);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        
        final Button submit = new Button("Generate");
        final Button folder = new Button("Choose Folder");
        
        hbBtn.getChildren().addAll(folder, submit);
        autoMode.add(hbBtn, 1, 6);
        
        //notification text
        autoMode.add(NOTIF, 1, 7);
        
        /* * * * * * * * * * * * * * * * * *
         * ASK MODE FORMATTING
         * * * * * * * * * * * * * * * * * */
        
        //configure grid pane
        final GridPane askMode = new GridPane();
        askMode.setAlignment(Pos.TOP_CENTER);
        askMode.setHgap(10);
        askMode.setVgap(10);
        askMode.setPadding(new Insets(5, 5, 5, 5));
        
        //text for displacement only
            //I really hate doing this.  Is there a better way?
        final Text displace = new Text("X");
        displace.setFont(Font.font("Tahoma", FontWeight.NORMAL, 22));
        displace.setFill(Color.TRANSPARENT);
        askMode.add(displace, 0, 0, 2, 1);
        
        //separator
        Separator sep = new Separator();
        sep.setOrientation(Orientation.VERTICAL);
        askMode.add(sep, 0, 1, 1, 7);
        
        //progress bar
        ProgressBar progress = new ProgressBar(.3f);
        askMode.add(progress, 1, 1, 2, 1);
        
        //query display w/ text label
        final Text queryLabel = new Text("Current query:");
        queryLabel.setFont(Font.font("Tahoma", FontWeight.NORMAL, 14));
        askMode.add(queryLabel, 1, 2);
        
        final Text currentQuery = new Text("placeholder");
        currentQuery.setFont(Font.font("Tahoma", FontWeight.EXTRA_BOLD, 14));
        askMode.add(currentQuery, 2, 2);
        
        //entry display (ListView?) w/ label
            //WEBENGINES
        
        //approve/decline buttons
        final Button accept = new Button("Accept"), 
                     reject = new Button("Reject");
        
        accept.setStyle("-fx-base: #b6e7c9;");
        reject.setStyle("-fx-base: #f3a8a4;");
        
        HBox askBtnBox = new HBox(10);
        askBtnBox.setAlignment(Pos.CENTER_LEFT);
        askBtnBox.getChildren().addAll(accept, reject);
        
        askMode.add(askBtnBox, 1, 5, 2, 1);
        
        //frame to hold controls for both
        GridPane extensionFrame = new GridPane();
        extensionFrame.setAlignment(Pos.CENTER);
        extensionFrame.setPadding(new Insets(5, 5, 5, 5));
        extensionFrame.add(autoMode, 0, 0);
        extensionFrame.add(askMode, 1, 0);
        
        /* * * * * * * * * * * * * * * * * *
         * EVENT HANDLERS
         * * * * * * * * * * * * * * * * * */
        submit.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (titleField.getText().length() == 0 ||
                        textArea.getText().length() == 0) {
                    NOTIF.setFill(Color.FIREBRICK);
                    NOTIF.setText("Must enter text in all fields");
                } else {
                    try {
                        BUSY = true;
                        NOTIF.setFill(Color.STEELBLUE);
                        NOTIF.setText("Submitting...");
                        
                        String[] query = textArea.getText().trim().split(" ");
                        if (alphabetize.isSelected())
                            Arrays.sort(query, String.CASE_INSENSITIVE_ORDER);
                        
                        submit(titleField.getText().trim(), query, 
                                latin.isSelected());
                                                
                        NOTIF.setText("Generation successful");
                        BUSY = false;
                    } catch (IOException e) {
                        NOTIF.setFill(Color.FIREBRICK);
                        NOTIF.setText("Generation failed");
                    }
                }
            }
        });
        
        folder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                
                DirectoryChooser dc = new DirectoryChooser();
                dc.setTitle("Choose folder");
                File file = dc.showDialog(primaryStage);
                
                if (file != null) {
                    PATH.setText(file.getAbsolutePath());
                    NOTIF.setFill(Color.STEELBLUE);
                    NOTIF.setText("Folder changed");
                }  else {
                    NOTIF.setFill(Color.FIREBRICK);
                    NOTIF.setText("Folder not changed");
                }
            }
        });
        
        ask.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (BUSY || ask.isSelected()) return;
                
                
            }
        });
        
        //Set scene and show stage
        Scene scene = new Scene(extensionFrame, 600, 400);
        primaryStage.setScene(scene);
        
        primaryStage.show();
    }

    protected BufferedReader getReaderFromURL(String url) 
            throws IOException {
        return new BufferedReader(new InputStreamReader(new URL(url)
                .openConnection().getInputStream()));
    }

    protected void parseWhitaker(BufferedReader reader, PrintWriter writer) 
            throws IOException {
        
        String line;
        boolean foundDef = false;
        
        reader.readLine(); //skip title
        
        while ((line = reader.readLine()) != null) {
            if (foundDef) {
                if (line.trim().contains(";")) { 
                    writer.print(line + " ");
                } else {
                    writer.println("<br/>"); //end of an entry -> new line
                    foundDef = false;
                }
            } else {
                if (line.equals("No Match")) {
                    //check for unknown English words
                    writer.println(line + "<br/>");
                } else if (line.trim().endsWith("UNKNOWN")) { 
                    //check for unknown Latin words
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
    }

    protected void submit(String label, String[] raw, boolean isLatin) 
            throws IOException {
        File output = new File(PATH.getText(), label + ".txt");
        BufferedReader reader = null;
        PrintWriter writer = null;
        
        //Concatenate Latin queries for optimum performance. (Max 250 characters
        //are allowed for L-to-E on Whitaker's Words.)
        String[] querySet;
        if (isLatin) {
            
            ArrayList<String> a = new ArrayList<>();
            
            for (String query : raw) {
                if (a.isEmpty() || a.get(a.size() - 1).length() 
                        + (query.length() + 1) > MAX_LENGTH) {
                    a.add(query);
                } else {
                    a.set(a.size() - 1, a.get(a.size() - 1) + "+" + query);
                }
            }
            
            querySet = a.toArray(new String[a.size()]);
        } else {
            querySet = raw;
        }
        
        try {
            writer = new PrintWriter(new FileWriter(output));
            writer.println("<!DOCTYPE html><html><body><h2>" 
                    + label + "</h2>"); //opening tags and title            
            
            for (String query : querySet) {
                
                if (!isLatin) { //E-to-L needs subheadings
                    writer.println("<h3> > " + query + "</h3>");
                    //only Latin queries are concatenated with + signs
                }
                
                reader = getReaderFromURL(
                        (isLatin ? FROM_LATIN : FROM_ENGLISH) + query);

                parseWhitaker(reader, writer);
                
                if (!isLatin) writer.println("<br/>");
                //need indent for E-to-L, but L-to-E should look continuous
            }
            
            writer.println("*</body></html>"); //closing tags
            
            reader.close(); writer.close();
        } finally {
            if (reader != null) reader.close();
            if (writer != null) writer.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}

