package com.ifpb.cp.controller;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.enums.TipoPrescricao;
import com.ifpb.cp.service.PrescricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/prescricao")
@RequiredArgsConstructor
public class PrescricaoController {

    @Autowired
    private PrescricaoService service;

    @PostMapping("/calcular")
    public ResponseEntity<PrescricaoResponseDTO> calcular(
            @RequestBody @Valid PrescricaoRequestDTO dto) {

        TipoPrescricao tipo = dto.getTipoPrescricao();
        PrescricaoResponseDTO resp = service.calcularPrescricao(dto);
        return ResponseEntity.ok(resp);
    }
}
