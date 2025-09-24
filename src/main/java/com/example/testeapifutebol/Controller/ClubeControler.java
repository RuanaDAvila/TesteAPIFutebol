package com.example.testeapifutebol.Controller;

import com.example.testeapifutebol.DTO.ClubeDTO;
import com.example.testeapifutebol.Service.ClubeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clubes")

public class ClubeControler {
    @Autowired
    private ClubeService clubeService;

    @PostMapping
    public ResponseEntity<ClubeDTO> criarClubeEntity(@RequestBody ClubeDTO clubeDTO) {
        ClubeDTO clubeCriado = clubeService.saveClubeEntity(clubeDTO);
        return new ResponseEntity<>(clubeCriado, HttpStatus.CREATED);

    }

    @GetMapping
    public ResponseEntity<List<ClubeDTO>> findAllClubeEntity() {
        List<ClubeDTO> clubes = clubeService.findAllClubeEntity();
        return new ResponseEntity<>(clubes, HttpStatus.OK);
    }


}
