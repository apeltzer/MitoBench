package io.writer;

import
        io.IOutputData;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import view.table.controller.TableControllerUserBench;

import java.io.*;

/**
 * Created by neukamm on 17.01.17.
 */
public class StatisticsWriter implements IOutputData{

    private TableView table;
    private String content="";
    private Object tab_content;


    public StatisticsWriter(Tab tab) {
        tab_content = tab.getContent();
        if(tab_content instanceof TableView){
            table = (TableView) tab.getContent();
            content = parseTableContent(table);
        } else if(tab_content instanceof Label){
            content = ((Label) tab_content).getText();

        }

    }

    private String parseTableContent(TableView table){
        String content="";
        int counter = 0;
        // write column names as first line
        for(Object col : table.getColumns()){
            TableColumn column = (TableColumn) col;
            if(counter == table.getColumns().size()-1){
                content += column.getText();
            } else {
                content += column.getText()+",";
            }
            counter++;
        }
        content += "\n";
        counter = 0;
        for(Object r : table.getItems()){
            ObservableList row = (ObservableList) r;
            for(Object cell : row){
                if(counter == row.size()-1)
                    content += cell;
                else
                    content += cell + ",";

                counter++;
            }
            content += "\n";
            counter=0;
        }

        return content;

    }


    /**
     * This method writes the statistics to csv file.
     * @throws IOException
     */
    @Override
    public void writeData(String path, TableControllerUserBench tableController) throws IOException {
        if(!path.endsWith(".csv")){
            path += ".csv";
        }
        OutputStream outputStream = new FileOutputStream(new File(path).getAbsoluteFile());
        OutputStreamWriter writerOutputStream = new OutputStreamWriter(outputStream, "UTF-8");
        writerOutputStream.write(content);
        writerOutputStream.close();

    }


    @Override
    public void setGroups(String groupID) {
        //Do nothing here, not required for this format at all
    }
}
