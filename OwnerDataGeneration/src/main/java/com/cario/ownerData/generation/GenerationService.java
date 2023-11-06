package com.cario.ownerData.generation;

import jdk.nashorn.internal.runtime.logging.DebugLogger;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.cario.ownerData.helper.GenerationHelper;

import java.io.*;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class GenerationService {


    private DebugLogger logger;

    public boolean generate_and_append( String newOwner, int numberOfVehicles) {
        String outputFileName = newOwner + "_vehicle_data.xlsx";
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(outputFileName)) {
            Sheet sheet = workbook.createSheet("Sheet1");
            for (int i = 0; i < numberOfVehicles; i++) {
                Row row = sheet.createRow(i);
                GenerationHelper ghelp = new GenerationHelper();
                String[] completeData =ghelp.generateCompleteData(newOwner);
                for (int j = 0; j <= completeData.length; j++) {
                    if (j == 0) {
                        Cell cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                    } else if(j == 4 || j == 7 || j == 12 || j == 20 || (j == 28 && ghelp.isLien())) {
                        Cell cell = row.createCell(j);
                        System.out.println(completeData[j - 1]);
                        cell.setCellValue(Integer.parseInt(completeData[j - 1]));
                    } else if (j == 10) {
                        Cell cell = row.createCell(j);
                        System.out.println(completeData[j - 1]);
                        cell.setCellValue(Double.parseDouble(completeData[j - 1]));
                    }else if(j == 14) {
                        Cell cell = row.createCell(j);
                        System.out.println(completeData[j - 1]);
                        cell.setCellValue(Integer.parseInt(completeData[j - 1]));
                    } else {// Create a cell for each piece of vehicle data
                        Cell cell = row.createCell(j);
                        cell.setCellValue(completeData[ j - 1]);
                    }
                }
            }
            workbook.write(outputStream);
            System.out.println("Vehicle data written to " + outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
//        logger.info("File generated");
        return true;
    }


}
