package com.ifpb.cp.controller;

import com.ifpb.cp.dto.PrescricaoRequestDTO;
import com.ifpb.cp.dto.PrescricaoResponseDTO;
import com.ifpb.cp.service.PrescricaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/prescricao")
@RequiredArgsConstructor
public class PrescricaoController {
    private final PrescricaoService service;

    @PostMapping("/calcular")
    public ResponseEntity<PrescricaoResponseDTO> calcular(@RequestBody @Valid PrescricaoRequestDTO dto) {
        return ResponseEntity.ok(service.calcularPrescricao(dto));
    }

}
