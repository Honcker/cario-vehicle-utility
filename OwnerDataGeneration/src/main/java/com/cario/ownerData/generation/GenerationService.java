package com.cario.ownerData.generation;

import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.zip.ZipEntry;

@Service
public class GenerationService {

    public boolean generate_and_append(InputStream attachment) {
        InputStream attachmentStream = rpcOps.openAttachment(SecureHash.parse(hash));

        while ((entry = zipStream.getNextEntry()) != null) {
            ZipEntry entry;
            // Process each entry in the zip file
            String entryName = entry.getName();
            // Check if the entry is an XLSX file
            if (entryName.endsWith(".xlsx")) {
                logger.info("Found XLSX file: " + entryName);

                // Create a ByteArrayOutputStream to read the entry's content
                ByteArrayOutputStream entryContent = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;

                while ((bytesRead = zipStream.read(buffer)) != -1) {
                    entryContent.write(buffer, 0, bytesRead);
                }

                try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(entryContent.toByteArray()))) {
                    Sheet sheet = workbook.getSheetAt(0);

                    for (int rowIndex = 1; rowIndex <= sheet.getLastRowNum(); rowIndex++) {
                        Row row = sheet.getRow(rowIndex);
                        boolean isRowEmpty = isRowEmpty(row);
                        if (!isRowEmpty) {

                            Vehicle vehicle = new Vehicle();
                            vehicle.setVin(row.getCell(1).getStringCellValue());
                            vehicle.setMake(row.getCell(2).getStringCellValue());
                            vehicle.setModel(row.getCell(3).getStringCellValue());
                            vehicle.setYear(row.getCell(4).getNumericCellValue() + "");
                            vehicle.setBodyType(row.getCell(5).getStringCellValue());
                            vehicle.setColor(row.getCell(6).getStringCellValue());
                            vehicle.setMileage((int) row.getCell(12).getNumericCellValue());

                            Lien.Builder lien = Lien.newBuilder();
                            if (row.getCell(21).getStringCellValue().equals("Yes")) {
                                //serach for lien from cenm database
                                lien = lookUpLienHolder(row.getCell(22).getStringCellValue()).toBuilder();
                            }
                            String titleId = row.getCell(8).getStringCellValue();


                            Owner owner = new Owner();
                            owner.setVehicle(vehicle);
                            owner.setLien(lien);
                            owner.setVehicleOwner(row.getCell(13).getStringCellValue());
                            owner.setCommercialEntity(commercialEntityService.getMyIdentity());
                            owner.setLicensePlate(row.getCell(9).getStringCellValue());
                            owner.setTitleNumber(row.getCell(8).getStringCellValue());


                            double numericDate = row.getCell(10).getNumericCellValue();
                            Date issueDate = getDateFromNumericValue(numericDate);

                            Instant instant = issueDate.toInstant();
                            Timestamp timestamp = Timestamp.newBuilder()
                                    .setSeconds(instant.getEpochSecond())
                                    .setNanos(instant.getNano())
                                    .build();
                            owner.setTitleIssueDate(timestamp);

                            String vehicleMetadata = Base64.getEncoder().encodeToString(owner.build().toByteArray());

                            CreateVehicleFlowRequest createVehicleFlowRequest = new CreateVehicleFlowRequest();
                            createVehicleFlowRequest.setEmail(row.getCell(15).getStringCellValue());
                            createVehicleFlowRequest.setOwner(ownerParty);

                            createVehicleFlowRequest.setVehicle_metadata(vehicleMetadata);
                            createVehicleFlowRequest.setVin(vehicle.getVin());


                        }
                    }


                    try {
                        attachmentStream.close();
                    } catch (IOException ioe) {
                        throw new TitleRuntimeException("Unable to close attachment stream [" + hash + "]", ioe);
                    }

                } catch (IOException ioe) {
                    throw new TitleRuntimeException("Vehicle Excel Sheet Parsing Error [" + hash + "]", ioe);
                }
            }
        }

        return false;
    }

    private class Owner {
        private Vehicle vehicle;
        private String email;
        

    }
    private class Vehicle {
        private String ownerMail;
        private String VIN;
        private String title;
        private String issueDate;
        private int weight;
        private int number;
        private String body;
        private String make;
        private String model;
        private int year;
        private int mileage;
        private String email;
        private boolean lien;
    }
}
