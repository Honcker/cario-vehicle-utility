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

    private DebugLogger logger;

    public boolean generate_and_append(InputStream attachment, String newOwner) {
        String filePath = "VehicleDatabase.xlsx";
        String outputPath = "VehicleDatabaseClone.xlsx";
        String fieldToChange = "Email";
        String newValue = newOwner; // The new email
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis);
             FileOutputStream fos = new FileOutputStream(outputPath)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming you want to process the first sheet
            for (int rowIndex = 0; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                Row row = sheet.getRow(rowIndex);
                if (row != null) {
                    Cell originalCell = row.getCell(getCellIndex(row, fieldToChange));
                    if (originalCell != null) {
                        Cell newCell = row.createCell(originalCell.getColumnIndex() + 1, originalCell.getCellType());
                        newCell.setCellValue(newValue);
                    }
                }
            }
            workbook.write(fos);
            System.out.println("Excel file duplicated and modified successfully.");
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
