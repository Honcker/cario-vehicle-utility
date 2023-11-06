package com.cario.ownerData.controller;
import com.cario.ownerData.generation.GenerationService;
//import com.cario.ownerData.generation.GenerationHelper;
import com.cario.ownerData.model.VehicleGenerationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/generate")
    public ResponseEntity<String> uploadExcel(@RequestBody VehicleGenerationRequest request) {
        if(service.generate_and_append(request.getOwner(), request.getVehicles())) {
            return new ResponseEntity<>("Generation Success", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Failed to Generate", HttpStatus.BAD_REQUEST);
        }
    }
}