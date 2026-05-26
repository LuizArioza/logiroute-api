package com.logiroute.api.service;

import com.logiroute.api.domain.Entregador;
import com.logiroute.api.domain.Pacote;
import com.logiroute.api.domain.enums.StatusPacote;
import com.logiroute.api.domain.enums.TipoFrete;
import com.logiroute.api.repository.EntregadorRepository;
import com.logiroute.api.repository.PacoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoteamentoService {

    private final PacoteRepository pacoteRepository;
    private final EntregadorRepository entregadorRepository;

    /**
     * Retorna a fila de pacotes ordenada por prioridade para um CEP de destino.
     * Ordem: PRIME/EXPRESSO antes de NORMAL, desempate por dataEntradaGalpao.
     */
    public List<Pacote> calcularFilaDeEntrega(String cepDestino) {

        return pacoteRepository.findAll()
                .stream()
                .filter(p -> p.getStatus() == StatusPacote.AGUARDANDO)
                .filter(p -> p.getCepDestino().startsWith(cepDestino.substring(0, 5)))
                .sorted(Comparator
                        .comparingInt(this::prioridadeFrete)
                        .thenComparing(Pacote::getDataEntradaGalpao))
                .toList();
    }

    /**
     * Despacha um pacote, vinculando ao entregador e validando capacidade de peso.
     */
    public Pacote despacharPacote(Long pacoteId, Long entregadorId) {

        Pacote pacote = pacoteRepository.findById(pacoteId)
                .orElseThrow(() -> new RuntimeException("Pacote não encontrado: " + pacoteId));

        Entregador entregador = entregadorRepository.findById(entregadorId)
                .orElseThrow(() -> new RuntimeException("Entregador não encontrado: " + entregadorId));

        validarCapacidadePeso(pacote, entregador);

        pacote.setEntregador(entregador);
        pacote.setStatus(StatusPacote.DESPACHADO);

        return pacoteRepository.save(pacote);
    }

    /**
     * Cadastra um novo pacote no sistema com status AGUARDANDO.
     */
    public Pacote cadastrarPacote(Pacote pacote) {
        pacote.setStatus(StatusPacote.AGUARDANDO);
        return pacoteRepository.save(pacote);
    }

    // ---- Métodos privados de apoio ----

    private int prioridadeFrete(Pacote pacote) {
        return switch (pacote.getTipoFrete()) {
            case PRIME    -> 0;
            case EXPRESSO -> 1;
            case NORMAL   -> 2;
        };
    }

    private void validarCapacidadePeso(Pacote pacote, Entregador entregador) {
        if (pacote.getPesoKg() > entregador.getCapacidadeMaximaKg()) {
            throw new RuntimeException(
                    "Peso do pacote (%.1fkg) excede a capacidade do entregador (%.1fkg)"
                            .formatted(pacote.getPesoKg(), entregador.getCapacidadeMaximaKg())
            );
        }
    }
}