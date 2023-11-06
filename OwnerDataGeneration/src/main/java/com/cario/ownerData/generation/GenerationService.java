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
import java.util.concurrent.ThreadLocalRandom;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

@Service
public class GenerationService {

    private int year;

    private static boolean lien;

    private static String[] carBodyTypes = {
            "Sedan",
            "SUV",
            "Hatchback",
            "Convertible",
            "Coupe",
            "Minivan",
            "Truck",
            "Crossover",
            "Wagon",
            "Van"
    };

    private static String[] carMakes = {
            "Jeep",
            "Nissan",
            "Saturn",
            "Ford",
            "Lexus",
            "Dodge",
            "Tesla",
            "Audi",
            "Hyundai",
            "Volkswagen"
    };

    private static String[] carModels = {
            "Cherokee",
            "Xterra",
            "Vue",
            "Edge",
            "IS",
            "Journey",
            "Model S",
            "R8",
            "Accent",
            "Passat"
    };


    private static String[] carColors = {
            "Red",
            "Blue",
            "Green",
            "Silver",
            "White",
            "Black",
            "Gray",
            "Yellow",
            "Orange",
            "Purple"
    };

    private static String[] generateCompleteData(String name) {
        // Call previous generators for specific fields
        String vin = generateRandomVin();
        String[] carData = generateData();
        String randomCarMake = generateRandomCarMake();
        String randomCarModel = generateRandomCarModel();
        String randomCarBodyType = generateRandomCarBodyType();
        String randomCarColor = selectRandomCarColor();
        String [] ownerData = generateOwnerData(name);
        String[] lienData = generateLien(generateRandomNumber(0,1));
        // Combine all data into a single string array
        String[] completeData = {
                vin,
                randomCarMake,
                randomCarModel,
                Integer.toString(generateRandomYear()),
                randomCarBodyType,
                randomCarColor,
                carData[0],  // GMW
                carData[1],  // Title No.
                generateLicensePlate(),
                String.valueOf(generateRandomDateAsDouble()/34619045.4858
                ),  // Issuing Date
                carData[3],  // State
                carData[4],  // Previous Mileage
                ownerData[0],  // name
                ownerData[1],  // phone
                ownerData[2],  // email
                ownerData[3],  // address 1
                ownerData[4],  // address 2
                ownerData[5], //city
                ownerData[6], //state
                ownerData[7], //zip
                "-",
                lienData[0],  //Lien Status
                lienData[1],  // Lien Name
                lienData[2],  // Lien Address Line 1
                lienData[3],  // Lien Address Line 2
                lienData[4],  // Lien City
                lienData[5],  // Lien State
                lienData[6]   // Lien Zip
        };
        return completeData;
    }


//    @Autowired
    private DebugLogger logger;

    public boolean generate_and_append( String newOwner, int numberOfVehicles) {
        String outputFileName = newOwner + "_vehicle_data.xlsx";
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream outputStream = new FileOutputStream(outputFileName)) {
            Sheet sheet = workbook.createSheet("Sheet1");
            for (int i = 0; i < numberOfVehicles; i++) {
                Row row = sheet.createRow(i);
                String[] completeData = generateCompleteData(newOwner);
                for (int j = 0; j <= completeData.length; j++) {
                    if (j == 0) {
                        Cell cell = row.createCell(0);
                        cell.setCellValue(i + 1);
                    } else if(j == 4 || j == 7 || j == 12 || j == 20 || (j == 28 && lien)) {
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

    private static String generateLicensePlate() {
        StringBuilder licensePlate = new StringBuilder();
        // Generate the first two letters (LL)
        licensePlate.append(generateRandomLetter());
        licensePlate.append(generateRandomLetter());
        // Add a space
        licensePlate.append(" ");
        // Generate the five-digit number (DDDDD)
        for (int i = 0; i < 5; i++) {
            licensePlate.append(generateRandomDigit());
        }
        return licensePlate.toString();
    }
    private static char generateRandomLetter() {
        Random random = new Random();
        char letter = (char) (random.nextInt(26) + 'A'); // Generates a random uppercase letter (A-Z)
        return letter;
    }
    private static int generateRandomDigit() {
        Random random = new Random();
        int digit = random.nextInt(10); // Generates a random digit (0-9)
        return digit;
    }

    private static int generateRandomYear() {
        int minYear = 2010;
        int maxYear = 2022;
        Random random = new Random();
        return random.nextInt(maxYear - minYear + 1) + minYear;
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

    private static int getCellIndex(Row row, String cellValue) {
        for (int cellIndex = 0; cellIndex < row.getLastCellNum(); cellIndex++) {
            Cell cell = row.getCell(cellIndex);
            if (cell != null && cellValue.equals(cell.getStringCellValue())) {
                return cellIndex;
            }
        }
        return -1; // Not found
    }

    private static String[] generateLien(int l) {
        String lienStatus = "No";
        if (l == 1) {
            // Lien exists
            lien = true;
            lienStatus = "Yes";
            String lienName = "Chase Auto Finance NY";
            String addressLine1 = "100 Broadway Street";
            String addressLine2 = "Suite 456";
            String city = "New York CIty";
            String state = "NY";
            String zip = "10005";
            return new String[] { lienStatus, lienName, addressLine1, addressLine2, city, state, zip };
        } else if (l == 0) {
            lien = false;
            //no lien exists
            String lienName = "-";
            String addressLine1 = "-";
            String addressLine2 = "-";
            String city = "-";
            String state = "-";
            String zip = "-";
            return new String[] { lienStatus, lienName, addressLine1, addressLine2, city, state, zip };
        } else {
            return null;
        }
    }

    private static String[] generateData() {
        String GMW = Integer.toString(generateRandomNumber(3500, 6500));
        String issuingDate = generateRandomDate();
        String state = generateRandomState();
        String titleNo = state + generateRandomNumber(100000, 999999);
        String previousMileage = String.valueOf(generateRandomNumber(1000, 3500));
        return new String[] { GMW, titleNo, issuingDate, state, previousMileage };
    }
    private static int generateRandomNumber(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }
    private static String generateRandomDate() {
        int day = generateRandomNumber(1, 28);
        int month = generateRandomNumber(1, 12);
        int year = generateRandomNumber(2000, 2023);
        return String.format("%02d/%02d/%04d", month, day, year);
    }

    public static double generateRandomDateAsDouble() {
        // Generate a random time in milliseconds within a specified range (e.g., from 2000 to 2023)
        long minTimeMillis = new Date(100, 0, 1).getTime(); // January 1, 2000
        long maxTimeMillis = new Date(123, 11, 31).getTime(); // December 31, 2023
        long randomTimeMillis = ThreadLocalRandom.current().nextLong(minTimeMillis, maxTimeMillis);
        // Convert the time in milliseconds to a double
        double dateAsDouble = (double) randomTimeMillis;
        return dateAsDouble;
    }
    private static String generateRandomState() {
        String[] states = { "AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY" };
        Random random = new Random();
        int randomIndex = random.nextInt(states.length);
        return states[randomIndex];
    }

    private static String generateRandomCarBodyType() {
        Random random = new Random();
        int randomIndex = random.nextInt(carBodyTypes.length);
        return carBodyTypes[randomIndex];
    }

    private static String selectRandomCarColor() {
        Random random = new Random();
        int randomIndex = random.nextInt(carColors.length);
        return carColors[randomIndex];
    }

    private static String[] generateOwnerData(String name) {
        // Fixed values
        String number = "1114578825";
        String addressLine1 = "123 Main Street";
        String addressLine2 = "Apt 4B";
        String city = "New York City";
        String state = "NY";
        String zip = "10008";
        return new String[] { name, number, name, addressLine1, addressLine2, city, state, zip };
    }






//    public String[] decodeVin(String vin) {
//            if (vin.length() != 17) {
//                System.out.println("Invalid VIN length. A VIN must be 17 characters.");
//                return null;
//            }
//
//            String country = vin.substring(0, 3);
//            String manufacturer = vin.substring(3, 8);
//            String yearCode = vin.charAt(9) == 'X' ? "2019 or later" : "20" + vin.charAt(9);
//            int assemblyPlant = vin.charAt(10) - 'A' + 1;
//            String serialNumber = vin.substring(11);
//
//            return new String[] {yearCode, serialNumber};
//    }

    private static String generateRandomCarMake() {
        Random random = new Random();
        int randomIndex = random.nextInt(carMakes.length);
        return carMakes[randomIndex];
    }

    private static String generateRandomCarModel() {
        Random random = new Random();
        int randomIndex = random.nextInt(carModels.length);
        return carModels[randomIndex];
    }



}
