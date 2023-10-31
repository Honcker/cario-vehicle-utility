package com.cario.ownerData.controller;
//import com.cario.ownerData.generation.GenerationService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class GenerationController {

    @GetMapping("/xlsx")
    public ResponseEntity<byte[]> downloadXlsxDocument() throws IOException {
        ClassPathResource resource = new ClassPathResource("sample.xlsx");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "VehicleDatabse.xlsx");
        return ResponseEntity.ok()
                .headers(headers)
                .body(resource.getInputStream().readAllBytes());
    }


    @PostMapping("/upload-xlsx")
    public ResponseEntity<String> uploadExcel(@RequestParam("file") MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {

                return new ResponseEntity<>("Excel file uploaded successfully!", HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>("Failed to process the Excel file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>("Please upload a valid Excel file.", HttpStatus.BAD_REQUEST);
        }
    }
}