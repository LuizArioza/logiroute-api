package com.logiroute.api.service;

import com.logiroute.api.domain.Entregador;
import com.logiroute.api.domain.Pacote;
import com.logiroute.api.domain.enums.StatusPacote;
import com.logiroute.api.exception.BusinessException;
import com.logiroute.api.exception.ResourceNotFoundException;
import com.logiroute.api.repository.EntregadorRepository;
import com.logiroute.api.repository.PacoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * Serviço responsável pela lógica de roteamento e despacho de entregas.
 *
 * <p>Implementa o algoritmo de priorização de pacotes seguindo as regras:</p>
 * <ul>
 *   <li>Pacotes {@code PRIME} têm prioridade máxima</li>
 *   <li>Pacotes {@code EXPRESSO} têm prioridade intermediária</li>
 *   <li>Pacotes {@code NORMAL} têm menor prioridade</li>
 *   <li>Empates são resolvidos pela {@code dataEntradaGalpao} (FIFO)</li>
 * </ul>
 *
 * @author LuizArioza
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class RoteamentoService {

    private final PacoteRepository pacoteRepository;
    private final EntregadorRepository entregadorRepository;

    /**
     * Calcula e retorna a fila de entrega ordenada por prioridade para um CEP de destino.
     *
     * <p>Filtra apenas pacotes com status {@code AGUARDANDO} cujo CEP de destino
     * corresponda aos 5 primeiros dígitos do CEP informado. A ordenação segue
     * a prioridade do tipo de frete e, em caso de empate, a data de entrada no galpão.</p>
     *
     * @param cepDestino CEP de destino para filtrar os pacotes (mínimo 5 dígitos)
     * @return lista de pacotes ordenada por prioridade de entrega
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
     * Despacha um pacote vinculando-o a um entregador, após validar a capacidade de peso.
     *
     * <p>Altera o status do pacote para {@code DESPACHADO} e persiste a associação
     * com o entregador informado.</p>
     *
     * @param pacoteId     ID do pacote a ser despachado
     * @param entregadorId ID do entregador responsável pela entrega
     * @return pacote atualizado com status {@code DESPACHADO} e entregador vinculado
     * @throws ResourceNotFoundException se o pacote ou entregador não forem encontrados
     * @throws BusinessException         se o peso do pacote exceder a capacidade do entregador
     */
    public Pacote despacharPacote(Long pacoteId, Long entregadorId) {
        Pacote pacote = pacoteRepository.findById(pacoteId)
                .orElseThrow(() -> new ResourceNotFoundException("Pacote", pacoteId));

        Entregador entregador = entregadorRepository.findById(entregadorId)
                .orElseThrow(() -> new ResourceNotFoundException("Entregador", entregadorId));

        validarCapacidadePeso(pacote, entregador);

        pacote.setEntregador(entregador);
        pacote.setStatus(StatusPacote.DESPACHADO);

        return pacoteRepository.save(pacote);
    }

    /**
     * Cadastra um novo pacote no sistema com status inicial {@code AGUARDANDO}.
     *
     * @param pacote objeto {@link Pacote} a ser persistido
     * @return pacote salvo com ID gerado e status {@code AGUARDANDO}
     */
    public Pacote cadastrarPacote(Pacote pacote) {
        pacote.setStatus(StatusPacote.AGUARDANDO);
        return pacoteRepository.save(pacote);
    }

    /**
     * Determina o valor numérico de prioridade de um pacote com base no tipo de frete.
     *
     * <p>Valores menores indicam maior prioridade na fila de entrega.</p>
     *
     * @param pacote pacote a ser avaliado
     * @return {@code 0} para PRIME, {@code 1} para EXPRESSO, {@code 2} para NORMAL
     */
    private int prioridadeFrete(Pacote pacote) {
        return switch (pacote.getTipoFrete()) {
            case PRIME    -> 0;
            case EXPRESSO -> 1;
            case NORMAL   -> 2;
        };
    }

    /**
     * Valida se o peso do pacote não excede a capacidade máxima do entregador.
     *
     * @param pacote     pacote com o peso a ser validado
     * @param entregador entregador com a capacidade máxima definida
     * @throws BusinessException se {@code pacote.pesoKg > entregador.capacidadeMaximaKg}
     */
    private void validarCapacidadePeso(Pacote pacote, Entregador entregador) {
        if (pacote.getPesoKg() > entregador.getCapacidadeMaximaKg()) {
            throw new BusinessException(
                    "Peso do pacote (%.1fkg) excede a capacidade do entregador (%.1fkg)"
                            .formatted(pacote.getPesoKg(), entregador.getCapacidadeMaximaKg())
            );
        }
    }
}