package com.cario.ownerData.generation;

import jdk.nashorn.internal.runtime.logging.DebugLogger;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
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

    private static String generateRandomVin() {
        StringBuilder vinBuilder = new StringBuilder();
        Random random = new Random();
        // The first character of a VIN is always an uppercase letter
        vinBuilder.append((char) ('A' + random.nextInt(26)));
        // Generate the next 16 characters (digits and uppercase letters)
        for (int i = 1; i < 17; i++) {
            char randomChar;
            if (i == 9) {
                // The 9th character is always the check digit (calculated later)
                randomChar = '0';
            } else {
                if (i < 10) {
                    // Generate a digit for positions 1-8
                    randomChar = (char) ('0' + random.nextInt(10));
                } else {
                    // Generate an uppercase letter for positions 10-17
                    randomChar = (char) ('A' + random.nextInt(26));
                }
            }
            vinBuilder.append(randomChar);
        }
        // Calculate and set the check digit (9th character)
        char checkDigit = calculateVinCheckDigit(vinBuilder.toString());
        vinBuilder.setCharAt(8, checkDigit);
        return vinBuilder.toString();
    }
    private static char calculateVinCheckDigit(String vin) {
        // VIN check digit calculation based on ISO 3779
        char[] weights = "8765432X098765432".toCharArray();
        int sum = 0;
        for (int i = 0; i < vin.length(); i++) {
            char c = vin.charAt(i);
            int weight = Character.isDigit(c) ? c - '0' : (c == 'X' ? 10 : c - 'A' + 1);
            sum += weight * (i < 8 ? weight : weights[i - 8]);
        }
        int checkDigitValue = sum % 11;
        return (checkDigitValue == 10) ? 'X' : (char) ('0' + checkDigitValue);
    }
    public static void main(String[] args) {
        String randomVin = generateRandomVin();
        System.out.println("Random VIN: " + randomVin);
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

        public String[] decodeVin(String vin) {
            if (vin.length() != 17) {
                System.out.println("Invalid VIN length. A VIN must be 17 characters.");
                return null;
            }

            String country = vin.substring(0, 3);
            String manufacturer = vin.substring(3, 8);
            String yearCode = vin.charAt(9) == 'X' ? "2019 or later" : "20" + vin.charAt(9);
            int assemblyPlant = vin.charAt(10) - 'A' + 1;
            String serialNumber = vin.substring(11);

            return new String[yearCode, serialNumber];
        }



}
