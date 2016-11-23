package view.menus;

import io.Exceptions.FastAException;
import io.datastructure.Entry;
import io.reader.GenericInputParser;
import io.reader.HSDInput;
import io.reader.MultiFastAInput;
import io.writer.CSVWriter;
import io.writer.ExcelWriter;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;
import view.table.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by neukamm on 16.11.16.
 */
public class FileMenu {

    private Menu menuFile;
    private TableController tableManager;

    public FileMenu(TableController tableManager) throws IOException {
        this.menuFile = new Menu("File");
        this.tableManager = tableManager;
        addSubMenus();

    }


    private void addSubMenus() throws IOException {



        /*
                        IMPORT DIALOGUE

         */

        MenuItem importFile = new MenuItem("Import file");
        importFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                ImportDialogue importDialogue = new ImportDialogue();
                importDialogue.start(new Stage());


                if (importDialogue.getInputFile().getPath() != null) {
                    String absolutePath = importDialogue.getInputFile().getAbsolutePath();


                    //Input is FastA
                    if (absolutePath.endsWith(".fasta") | absolutePath.endsWith("*.fas") | absolutePath.endsWith("*.fa")) {


                        MultiFastAInput multiFastAInput = null;
                        try {
                            try {
                                multiFastAInput = new MultiFastAInput(importDialogue.getInputFile().getPath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (FastAException e) {
                            e.printStackTrace();
                        }
                        HashMap<String, List<Entry>> input_multifasta = multiFastAInput.getCorrespondingData();
                        tableManager.updateTable(input_multifasta);


                    }

                    //Input is HSD Format
                    if (absolutePath.endsWith(".hsd")) {
                        try {
                            HSDInput hsdInputParser = new HSDInput(importDialogue.getInputFile().getPath());
                            HashMap<String, List<Entry>> data_map = hsdInputParser.getCorrespondingData();
                            tableManager.updateTable(data_map);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    //Input is Generic Format

                    if (absolutePath.endsWith("*.tsv")) {
                        try {
                            GenericInputParser genericInputParser = new GenericInputParser(importDialogue.getInputFile().getPath());
                            HashMap<String, List<Entry>> data_map = genericInputParser.getCorrespondingData();
                            tableManager.updateTable(data_map);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        });

        

        /*
                        EXPORT DIALOGUE

         */

        MenuItem exportFile = new MenuItem("Export DB file");
        exportFile.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                ExportDialogue exportDialogue = new ExportDialogue();
                exportDialogue.start(new Stage());
                String outFileDB = exportDialogue.getOutFile();

                try {
                    CSVWriter csvWriter = new CSVWriter(tableManager);
                    csvWriter.writeData(outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
        });

        MenuItem exporttoXLS = new MenuItem("Export XLSX File");
        exporttoXLS.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                ExportDialogue exportDialogue = new ExportDialogue();
                exportDialogue.start(new Stage());
                String outFileDB = exportDialogue.getOutFile();

                try {
                    ExcelWriter excelwriter = new ExcelWriter(tableManager);
                    excelwriter.writeData(outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
        });


        /*

                EXIT OPTION


         */

        MenuItem exit = new MenuItem("Exit");
        exit.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
                System.exit(0);
            }
        });

        menuFile.getItems().addAll(importFile, exportFile, exporttoXLS, new SeparatorMenuItem(), exit);
    }


    public Menu getMenuFile() {
        return menuFile;
    }


}
