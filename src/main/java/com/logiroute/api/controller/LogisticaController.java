package com.logiroute.api.controller;

import com.logiroute.api.domain.Pacote;
import com.logiroute.api.dto.DespacharPacoteRequestDTO;
import com.logiroute.api.dto.PacoteRequestDTO;
import com.logiroute.api.dto.PacoteResponseDTO;
import com.logiroute.api.service.RoteamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LogisticaController {

    private final RoteamentoService roteamentoService;

    /**
     * Cadastra um novo pacote no sistema.
     * POST /api/pacotes
     */
    @PostMapping("/pacotes")
    public ResponseEntity<PacoteResponseDTO> cadastrarPacote(
            @Valid @RequestBody PacoteRequestDTO request) {

        Pacote novoPacote = Pacote.builder()
                .destinatario(request.destinatario())
                .cepDestino(request.cepDestino())
                .pesoKg(request.pesoKg())
                .tipoFrete(request.tipoFrete())
                .dataEntradaGalpao(request.dataEntradaGalpao())
                .build();

        Pacote salvo = roteamentoService.cadastrarPacote(novoPacote);
        return ResponseEntity.status(HttpStatus.CREATED).body(PacoteResponseDTO.fromEntity(salvo));
    }

    /**
     * Retorna a fila de entrega ordenada por prioridade para um CEP.
     * GET /api/logistica/rotas?cep=01310
     */
    @GetMapping("/logistica/rotas")
    public ResponseEntity<List<PacoteResponseDTO>> calcularRotas(
            @RequestParam String cep) {

        List<PacoteResponseDTO> fila = roteamentoService
                .calcularFilaDeEntrega(cep)
                .stream()
                .map(PacoteResponseDTO::fromEntity)
                .toList();

        return ResponseEntity.ok(fila);
    }

    /**
     * Despacha um pacote vinculando a um entregador.
     * PUT /api/pacotes/{id}/despachar
     */
    @PutMapping("/pacotes/{id}/despachar")
    public ResponseEntity<PacoteResponseDTO> despacharPacote(
            @PathVariable Long id,
            @Valid @RequestBody DespacharPacoteRequestDTO request) {

        Pacote despachado = roteamentoService.despacharPacote(id, request.entregadorId());
        return ResponseEntity.ok(PacoteResponseDTO.fromEntity(despachado));
    }
}
