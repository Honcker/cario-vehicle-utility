package com.cario.ownerData.controller;
//import com.cario.ownerData.generation.GenerationService;
import com.cario.ownerData.generation.GenerationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/ownerGen")
public class GenerationController {

    @Autowired
    private GenerationService service;

//    @GetMapping("/xlsx")
//    public ResponseEntity<byte[]> downloadXlsxDocument() throws IOException {
//        ClassPathResource resource = new ClassPathResource("sample.xlsx");
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
//        headers.setContentDispositionFormData("attachment", "VehicleDatabse.xlsx");
//        return ResponseEntity.ok()
//                .headers(headers)
//                .body(resource.getInputStream().readAllbytes());
//    }


    @PostMapping("/generate/{email}/{vehicles}")
    public ResponseEntity<String> uploadExcel(@PathVariable("email") String email, @PathVariable("vehicles") int vehicles ) {
        if(service.generate_and_append(email, vehicles)) {
            return new ResponseEntity<>("Generation Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to Generate", HttpStatus.BAD_REQUEST);
        }
    }
}