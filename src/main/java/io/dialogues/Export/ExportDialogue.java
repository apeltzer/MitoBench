package io.dialogues.Export;

import Logging.LogClass;
import controller.ChartController;
import io.writer.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import controller.TableControllerUserBench;
import view.dialogues.information.InformationDialogue;

import java.util.List;
import java.util.Optional;

/**
 * Created by peltzer on 30/11/2016.
 */
public class ExportDialogue extends Application {
    private final String[] userdefinedHGlist;
    private final LogClass logClass;
    private final boolean exportAllData;
    private final ChartController cc;
    private List<String> columnsInTable;
    private TableControllerUserBench tableController;
    private String MITOBENCH_VERSION;
    private ObservableList<ObservableList> dataToExport;
    private Logger LOG;
    private int year=2018;



    public static void main(String[] args) {
        Application.launch(args);
    }

    public ExportDialogue(TableControllerUserBench tableManager, String mitoVersion, LogClass logClass,
                          ChartController chartController, boolean exportAllData) {

        LOG = logClass.getLogger(this.getClass());
        this.logClass = logClass;
        this.exportAllData = exportAllData;
        this.cc = chartController;
        this.tableController = tableManager;
        this.columnsInTable = tableManager.getCurrentColumnNames();
        this.MITOBENCH_VERSION = mitoVersion;
        this.userdefinedHGlist = chartController.getCustomHGList();
        if(exportAllData){
            dataToExport = tableController.getTable().getItems();
        } else {
            dataToExport = tableController.getTable().getSelectionModel().getSelectedItems();
        }
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Select your desired export format");
        alert.setHeaderText("We need to know which kind of output you would like to save here and your desired grouping category.");
        alert.setContentText("Choose your desired export format.");

        ButtonType fasta_button = new ButtonType("FASTA");
        ButtonType arp_button = new ButtonType("ARP");
        ButtonType beast_button = new ButtonType("BEAST");
        ButtonType csv_button = new ButtonType("CSV");
        ButtonType tsv_button = new ButtonType("TSV");
        ButtonType xlsx_button = new ButtonType("XLSX");
        ButtonType mito_button = new ButtonType("MITOPROJ");
        ButtonType nexus_button = new ButtonType("NEXUS");
        ButtonType phylip_button = new ButtonType("PHYLIP");
        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        //We disallow ID and mtSequence as entries
        removeUnwantedEntries();
        List<String> options = this.columnsInTable;


        alert.getButtonTypes().setAll(fasta_button, arp_button, beast_button, csv_button, tsv_button, xlsx_button, mito_button,
                nexus_button, phylip_button, buttonTypeCancel);

        Optional<ButtonType> result = alert.showAndWait();


        //ARP output
        if (result.get() == arp_button) {
            if(tableController.sequencesHaveSameLength(tableController.getTableColumnByName("ID"))){

                DataChoiceDialogue dataChoiceDialogue = new DataChoiceDialogue(options);
                String selection = dataChoiceDialogue.getSelected();
                FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Arlequin Format (*.arp)", "*.arp");
                SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
                saveAsDialogue.start(new Stage());
                if(saveAsDialogue.getOutFile() != null) {
                    String outfileDB = saveAsDialogue.getOutFile();
                    LOG.info("Export data into ARP format with grouping on column '" + selection + "'. File: " + outfileDB);
                    ARPWriter arpwriter = new ARPWriter(tableController, dataToExport);
                    arpwriter.setGroups(selection);
                    arpwriter.writeData(outfileDB, tableController);
                }
            } else {
                InformationDialogue informationDialogue_unaligned = new InformationDialogue("Warning: Unaligned sequences",
                        "Please align you sequences first to proceed. mitoBench is not able to align you sequences, " +
                                "but you can export you data as multiFasta and align them with an alignment tool of your choice.",
                        "Please align your sequences", "warning_unaligned");
            }
                //fasta output
            } else  if (result.get() == fasta_button) {
                FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Fasta Format (*.fasta)", "*.fasta");
                SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
                saveAsDialogue.start(new Stage());
                if(saveAsDialogue.getOutFile() != null) {
                    String outfileFASTA = saveAsDialogue.getOutFile();
                    LOG.info("Export data into multi FASTA format. File: " + outfileFASTA);
                    MultiFastaWriter multiFastaWriter = new MultiFastaWriter(tableController.getDataTable().getMtStorage(), dataToExport, false);
                    multiFastaWriter.writeData(outfileFASTA, tableController);

                }

            //Beast output
        } else if (result.get() == beast_button) {
            if(tableController.sequencesHaveSameLength(tableController.getTableColumnByName("ID"))){
                FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("BEAST FastA format (*.fasta)", "*.fasta");
                SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
                saveAsDialogue.start(new Stage());
                if (saveAsDialogue.getOutFile() != null) {
                    String outfileDB = saveAsDialogue.getOutFile();
                    LOG.info("Export data into BEAST format. File: " + outfileDB);
                    BEASTWriter beastwriter = new BEASTWriter(tableController, LOG, dataToExport, year);
                    beastwriter.writeData(outfileDB, tableController);
                }
            } else {
                InformationDialogue informationDialogue_unaligned = new InformationDialogue("Warning: Unaligned sequences",
                        "Please align you sequences first to proceed. mitoBench is not able to align you sequences, " +
                                "but you can export you data as multiFasta and align them with an alignment tool of your choice.",
                        "Please align your sequences", "warning_unaligned");
            }

            //CSV Output
        } else if (result.get() == csv_button) {
            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Comma Separated Values (*.csv)", "*.csv");
            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
            saveAsDialogue.start(new Stage());
            if (saveAsDialogue.getOutFile() != null) {
                String outFileDB = saveAsDialogue.getOutFile();
                try {
                    GenericWriter csvWriter = new GenericWriter(dataToExport, ",", true);
                    csvWriter.writeData(outFileDB, tableController);
                    LOG.info("Export data into CSV format. File: " + outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
            //XLSX output
        } else if (result.get() == tsv_button) {
            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Tab Separated Values (*.tsv)", "*.tsv");
            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
            saveAsDialogue.start(new Stage());
            if (saveAsDialogue.getOutFile() != null) {
                String outFileDB = saveAsDialogue.getOutFile();
                try {
                    GenericWriter csvWriter = new GenericWriter(dataToExport, "\t", true);
                    csvWriter.writeData(outFileDB, tableController);
                    LOG.info("Export data into TSV format. File: " + outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
            //XLSX output
        }else if (result.get() == xlsx_button) {
            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Microsoft Excel (*.xlsx)", "*.xlsx");
            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
            saveAsDialogue.start(new Stage());
            if (saveAsDialogue.getOutFile() != null) {
                String outFileDB = saveAsDialogue.getOutFile();
                try {
                    ExcelWriter excelwriter = new ExcelWriter(tableController, dataToExport);
                    excelwriter.writeData(outFileDB, tableController);
                    LOG.info("Export data into Excel format. File: " + outFileDB);

                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
        }
        // project output
        else if (result.get() == mito_button) {
            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("MitoBench project (*.mitoproj)", "*.mitoproj");
            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
            saveAsDialogue.start(new Stage());
            if (saveAsDialogue.getOutFile() != null) {
                String outFileDB = saveAsDialogue.getOutFile();
                try {
                    ProjectWriter projectWriter = new ProjectWriter(MITOBENCH_VERSION, LOG, dataToExport);
                    projectWriter.write(outFileDB, tableController, this.userdefinedHGlist);
                    LOG.info("Export whole project. File: " + outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
        }
        // nexus output
        else if (result.get() == nexus_button) {
            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Nexus (*.nex)", "*.nex");
            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
            saveAsDialogue.start(new Stage());
            if (saveAsDialogue.getOutFile() != null) {
                String outFileDB = saveAsDialogue.getOutFile();
                try {
                    NexusWriter nexusWriter = new NexusWriter(dataToExport);
                    nexusWriter.writeData(outFileDB, tableController);
                    LOG.info("Export data into Nexus format. File: " + outFileDB);
                } catch (Exception e) {
                    System.err.println("Caught Exception: " + e.getMessage());
                }
            }
        }// phylip output
        else if (result.get() == phylip_button) {

            // open new config window
            PhylipSettingsDialogue phylipSettingsDialogue  = new PhylipSettingsDialogue("Phylip format configuration", logClass,
                    dataToExport, tableController, MITOBENCH_VERSION, cc, exportAllData);


//            FileChooser.ExtensionFilter fex = new FileChooser.ExtensionFilter("Phylip (*.phylip)", "*.phylip");
//            SaveAsDialogue saveAsDialogue = new SaveAsDialogue(fex);
//            saveAsDialogue.start(new Stage());
//            if (saveAsDialogue.getOutFile() != null) {
//                String outFileDB = saveAsDialogue.getOutFile();
//                try {
//                    PhyLipWriter phyLipWriter = new PhyLipWriter(dataToExport, "relaxed", false);
//                    phyLipWriter.writeData(outFileDB, tableController);
//                    LOG.info("Export data into Phylip format. File: " + outFileDB);
//                } catch (Exception e) {
//                    System.err.println("Caught Exception: " + e.getMessage());
//                }
//            }
        }else {
            //TODO what happens on cancel() exactly ?
        }
    }


    private void removeUnwantedEntries() {
        List<String> old_options = this.columnsInTable;
        if(old_options.size() > 2){
            old_options.remove("ID");
            old_options.remove("MTSequence");
        } else {
            //do nothing ;-)
        }

    }
}
