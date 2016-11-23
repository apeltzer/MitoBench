package io.writer;

import io.IOutputData;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import view.table.TableController;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created by peltzer on 22/11/2016.
 */
public class ExcelWriter implements IOutputData {
    private TableController tableController;

    public ExcelWriter(TableController tableController) {

        this.tableController = tableController;
    }

    @Override
    public void writeData(String file) throws IOException {
        Writer writer = null;
        Workbook wb = new XSSFWorkbook();

        //Create file extension if its not there already...
        String safe_sheetname = "";
        if(file.endsWith(".xlsx")){
            safe_sheetname = WorkbookUtil.createSafeSheetName(file);
        } else {
           safe_sheetname = WorkbookUtil.createSafeSheetName(file+".xlsx");
        }
        Sheet sheet1 = wb.createSheet(safe_sheetname);
        Row row = sheet1.createRow(0);

        // write header
        String header = "";
        List<String> columns = tableController.getCurrentColumnNames();
        for (int i = 0; i < columns.size(); i++) {
            Cell c = row.createCell(i);
            c.setCellValue(columns.get(i));
        }



        int rowcounter = 1; //Else, we loose our header here!
        for (ObservableList list_entry : tableController.getData()) {
            Row row_to_add = sheet1.createRow(rowcounter);
            rowcounter++;
            for (int i = 0; i < list_entry.size(); i++) {
                Cell c = row_to_add.createCell(i);
                c.setCellValue((String) list_entry.get(i));
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        wb.write(fileOutputStream);
        fileOutputStream.close();

    }

}
