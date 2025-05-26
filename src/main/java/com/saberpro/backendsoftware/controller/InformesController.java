package com.saberpro.backendsoftware.controller;

import com.saberpro.backendsoftware.Utils.UploadArchive;
import com.saberpro.backendsoftware.dto.EvidenciaAccionDeMejoraDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/SaberPro/informes")
@RequiredArgsConstructor
public class InformesController {
    private final UploadArchive uploadArchive;

    /*@PostMapping("/upload")
    public ResponseEntity<String> getUploadArchive() {

    }*/
}
