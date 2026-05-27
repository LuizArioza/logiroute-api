package com.logiroute.api;

import com.logiroute.api.domain.Entregador;
import com.logiroute.api.domain.Pacote;
import com.logiroute.api.domain.enums.StatusPacote;
import com.logiroute.api.domain.enums.TipoFrete;
import com.logiroute.api.exception.BusinessException;
import com.logiroute.api.exception.ResourceNotFoundException;
import com.logiroute.api.repository.EntregadorRepository;
import com.logiroute.api.repository.PacoteRepository;
import com.logiroute.api.service.RoteamentoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do RoteamentoService")
class LogirouteApiApplicationTests {

	@Mock
	private PacoteRepository pacoteRepository;

	@Mock
	private EntregadorRepository entregadorRepository;

	@InjectMocks
	private RoteamentoService roteamentoService;

	private Pacote pacoteNormal;
	private Pacote pacotePrime;
	private Pacote pacoteExpresso;
	private Entregador entregador;

	@BeforeEach
	void setUp() {
		entregador = Entregador.builder()
				.id(1L)
				.nome("Carlos Silva")
				.veiculo("MOTO")
				.capacidadeMaximaKg(15.0)
				.cepAtendimento("01310-100")
				.build();

		pacoteNormal = Pacote.builder()
				.id(1L).destinatario("Ana Paula")
				.cepDestino("01310-100").pesoKg(2.0)
				.tipoFrete(TipoFrete.NORMAL)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.of(2026, 5, 27, 8, 0))
				.build();

		pacotePrime = Pacote.builder()
				.id(2L).destinatario("Bruno Costa")
				.cepDestino("01310-200").pesoKg(5.0)
				.tipoFrete(TipoFrete.PRIME)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.of(2026, 5, 27, 9, 0))
				.build();

		pacoteExpresso = Pacote.builder()
				.id(3L).destinatario("Carla Mendes")
				.cepDestino("01310-300").pesoKg(3.0)
				.tipoFrete(TipoFrete.EXPRESSO)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.of(2026, 5, 27, 7, 0))
				.build();
	}

	@Test
	@DisplayName("Deve ordenar pacotes: PRIME primeiro, EXPRESSO segundo, NORMAL por último")
	void deveOrdenarPacotesPorPrioridade() {
		when(pacoteRepository.findAll())
				.thenReturn(List.of(pacoteNormal, pacotePrime, pacoteExpresso));

		List<Pacote> fila = roteamentoService.calcularFilaDeEntrega("01310");

		assertThat(fila).hasSize(3);
		assertThat(fila.get(0).getTipoFrete()).isEqualTo(TipoFrete.PRIME);
		assertThat(fila.get(1).getTipoFrete()).isEqualTo(TipoFrete.EXPRESSO);
		assertThat(fila.get(2).getTipoFrete()).isEqualTo(TipoFrete.NORMAL);
	}

	@Test
	@DisplayName("Deve retornar apenas pacotes com status AGUARDANDO")
	void deveRetornarApenasAguardando() {
		Pacote despachado = Pacote.builder()
				.id(4L).cepDestino("01310-400")
				.tipoFrete(TipoFrete.NORMAL)
				.status(StatusPacote.DESPACHADO)
				.dataEntradaGalpao(LocalDateTime.now())
				.build();

		when(pacoteRepository.findAll())
				.thenReturn(List.of(pacoteNormal, despachado));

		List<Pacote> fila = roteamentoService.calcularFilaDeEntrega("01310");

		assertThat(fila).hasSize(1);
		assertThat(fila.get(0).getStatus()).isEqualTo(StatusPacote.AGUARDANDO);
	}

	@Test
	@DisplayName("Deve lançar ResourceNotFoundException para pacote inexistente")
	void deveLancarExcecaoParaPacoteInexistente() {
		when(pacoteRepository.findById(999L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> roteamentoService.despacharPacote(999L, 1L))
				.isInstanceOf(ResourceNotFoundException.class)
				.hasMessageContaining("999");
	}

	@Test
	@DisplayName("Deve lançar BusinessException quando peso excede capacidade")
	void deveLancarExcecaoQuandoPesoExcede() {
		Pacote pacotePesado = Pacote.builder()
				.id(5L).pesoKg(20.0)
				.tipoFrete(TipoFrete.NORMAL)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.now())
				.build();

		when(pacoteRepository.findById(5L)).thenReturn(Optional.of(pacotePesado));
		when(entregadorRepository.findById(1L)).thenReturn(Optional.of(entregador));

		assertThatThrownBy(() -> roteamentoService.despacharPacote(5L, 1L))
				.isInstanceOf(BusinessException.class)
				.hasMessageContaining("excede");
	}

	@Test
	@DisplayName("Deve desemparar por dataEntradaGalpao quando tipo de frete é igual")
	void deveDesempatarPorDataEntrada() {
		Pacote prime1 = Pacote.builder()
				.id(6L).cepDestino("01310-100")
				.tipoFrete(TipoFrete.PRIME)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.of(2026, 5, 27, 10, 0))
				.build();

		Pacote prime2 = Pacote.builder()
				.id(7L).cepDestino("01310-100")
				.tipoFrete(TipoFrete.PRIME)
				.status(StatusPacote.AGUARDANDO)
				.dataEntradaGalpao(LocalDateTime.of(2026, 5, 27, 8, 0))
				.build();

		when(pacoteRepository.findAll()).thenReturn(List.of(prime1, prime2));

		List<Pacote> fila = roteamentoService.calcularFilaDeEntrega("01310");

		assertThat(fila.get(0).getId()).isEqualTo(7L);
		assertThat(fila.get(1).getId()).isEqualTo(6L);
	}
}