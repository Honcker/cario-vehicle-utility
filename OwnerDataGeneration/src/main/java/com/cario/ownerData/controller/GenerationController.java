package com.cario.ownerData.controller;
import com.cario.ownerData.generation.GenerationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
@RestController
public class GenerationController {
    @PostMapping("/upload-excel")
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