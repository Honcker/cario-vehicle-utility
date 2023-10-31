package com.cario.ownerData.generation;

import jdk.nashorn.internal.runtime.logging.DebugLogger;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class GenerationService {

    @Autowired
    private DebugLogger logger;

    public boolean generate_and_append(InputStream attachment, String newOwner) {
            String filePath = "VehicleDatabase.xlsx";
            String fieldToChange = "Email";
            String newValue = newOwner;
            try (FileInputStream fis = new FileInputStream(filePath);
                 Workbook workbook = new XSSFWorkbook(fis)) {
                Sheet originalSheet = workbook.getSheetAt(0); // Assuming you want to process the first sheet
                for (int rowIndex = 0; rowIndex <= originalSheet.getLastRowNum(); rowIndex++) {
                    Row originalRow = originalSheet.getRow(rowIndex);
                    if (originalRow != null) {
                        Cell originalCell = originalRow.getCell(getCellIndex(originalRow, fieldToChange));
                        if (originalCell != null) {
                            // Create a new sheet for the modified data
                            Sheet newSheet = workbook.createSheet("ModifiedData");
                            // Create a new row in the new sheet
                            Row newRow = newSheet.createRow(newSheet.getLastRowNum() + 1);
                            // Duplicate the original row in the new sheet
                            for (int cellIndex = 0; cellIndex <= originalRow.getLastCellNum(); cellIndex++) {
                                Cell newCell = newRow.createCell(cellIndex, originalRow.getCell(cellIndex).getCellType());
                                if (cellIndex == originalCell.getColumnIndex()) {
                                    newCell.setCellValue(newValue);
                                } else {
                                    newCell.setCellValue(originalRow.getCell(cellIndex).getStringCellValue());
                                }
                            }
                        }
                    }
                }
                // Write the updated workbook back to the same file
                try (FileOutputStream fos = new FileOutputStream(filePath)) {
                    workbook.write(fos);
                    logger.info("Excel file duplicated and modified successfully. Modified data appended.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
    }

    private static int getCellIndex(Row row, String cellValue) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cellValue.equals(cell.getStringCellValue())) {
                return cellIndex;
            }
        }
        return -1; // Not found
    }


}
