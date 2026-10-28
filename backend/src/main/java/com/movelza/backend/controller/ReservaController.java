
package com.movelza.backend.controller;

import com.movelza.backend.dto.SolicitacaoReserva;
import com.movelza.backend.model.Cliente;
import com.movelza.backend.model.Reserva;
import com.movelza.backend.model.Veiculo;
import com.movelza.backend.repository.ClienteRepository;
import com.movelza.backend.repository.ReservaRepository;
import com.movelza.backend.repository.VeiculoRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
@CrossOrigin(origins = {
        "http://localhost:5500", "http://127.0.0.1:5500",
        "http://localhost:63342", "http://127.0.0.1:63342"
})
public class ReservaController {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final VeiculoRepository veiculoRepository;
    private final EntityManager entityManager;

    public ReservaController(ReservaRepository reservaRepository,
                             ClienteRepository clienteRepository,
                             VeiculoRepository veiculoRepository,
                             EntityManager entityManager) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.veiculoRepository = veiculoRepository;
        this.entityManager = entityManager;
    }

    @GetMapping("/total")
    public long contarReservas() {
        return reservaRepository.count();
    }

    // Apenas demonstracao local: este endpoint ainda nao possui autenticacao.
    @GetMapping
    @Transactional(readOnly = true)
    public List<ReservaResumo> listarReservas() {
        return entityManager.createQuery(
                "select r from Reserva r join fetch r.cliente join fetch r.veiculo order by r.id desc",
                Reserva.class
        ).getResultList().stream().map(this::resumo).toList();
    }

    // Apenas demonstracao local: nao exponha esta API publicamente sem controle de acesso.
    @PatchMapping("/{id}/status")
    @Transactional
    public ReservaResumo atualizarStatus(@PathVariable Integer id,
                                         @RequestBody AtualizacaoStatus dados) {
        if (dados == null || vazio(dados.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Informe o status.");
        }

        String novoStatus = switch (dados.status().trim().toLowerCase(Locale.ROOT)) {
            case "pendente" -> "Pendente";
            case "confirmada", "confirmado" -> "Confirmada";
            case "cancelada", "cancelado" -> "Cancelada";
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status invalido.");
        };

        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Reserva nao encontrada."));

        Veiculo veiculo = reserva.getVeiculo();
        entityManager.lock(veiculo, LockModeType.PESSIMISTIC_WRITE);
        entityManager.refresh(reserva);

        if ("Confirmada".equals(novoStatus)) {
            if (!"Disponivel".equalsIgnoreCase(veiculo.getStatus())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Veiculo indisponivel.");
            }

            Long conflitos = entityManager.createQuery(
                            "select count(r) from Reserva r " +
                                    "where r.veiculo.id = :veiculoId and r.id <> :id " +
                                    "and lower(r.status) in ('pendente', 'confirmada') " +
                                    "and r.dataRetirada < :devolucao and r.dataDevolucao > :retirada",
                            Long.class
                    ).setParameter("veiculoId", veiculo.getId())
                    .setParameter("id", id)
                    .setParameter("retirada", reserva.getDataRetirada())
                    .setParameter("devolucao", reserva.getDataDevolucao())
                    .getSingleResult();

            if (conflitos > 0) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "Existe outra reserva no mesmo periodo.");
            }
        }

        reserva.setStatus(novoStatus);
        reservaRepository.saveAndFlush(reserva);

        return resumo(reserva);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Transactional
    public Map<String, Object> criarReserva(@RequestBody SolicitacaoReserva dados) {
        if (dados == null || vazio(dados.getNome()) || vazio(dados.getCpf())
                || vazio(dados.getEmail()) || vazio(dados.getTelefone())
                || vazio(dados.getVeiculoId()) || vazio(dados.getLocalRetirada())
                || vazio(dados.getLocalDevolucao()) || dados.getRetirada() == null
                || dados.getDevolucao() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Preencha todos os campos obrigatorios.");
        }

        LocalDateTime retirada = dados.getRetirada();
        LocalDateTime devolucao = dados.getDevolucao();

        if (!retirada.isAfter(LocalDateTime.now()) || !devolucao.isAfter(retirada)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Periodo de reserva invalido.");
        }

        String cpf = dados.getCpf().replaceAll("\\D", "");

        if (!cpfValido(cpf)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "CPF invalido.");
        }

        Veiculo veiculo = veiculoRepository.findById(dados.getVeiculoId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Veiculo nao encontrado."));

        entityManager.lock(veiculo, LockModeType.PESSIMISTIC_WRITE);

        if (!"Disponivel".equalsIgnoreCase(veiculo.getStatus())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Veiculo indisponivel.");
        }

        long conflitos = reservaRepository.contarConflitos(
                veiculo.getId(), retirada, devolucao);

        if (conflitos > 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Ja existe uma reserva nesse periodo.");
        }

        long minutos = Duration.between(retirada, devolucao).toMinutes();
        long dias = (minutos + 1439) / 1440;

        BigDecimal total = veiculo.getValorDiaria()
                .multiply(BigDecimal.valueOf(dias));

        Cliente cliente = clienteRepository.findByCpf(cpf).orElseGet(() -> {
            Cliente novo = new Cliente();
            novo.setNome(dados.getNome().trim());
            novo.setCpf(cpf);
            novo.setEmail(dados.getEmail().trim());
            novo.setTelefone(dados.getTelefone().trim());
            return clienteRepository.save(novo);
        });

        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setVeiculo(veiculo);
        reserva.setDataRetirada(retirada);
        reserva.setDataDevolucao(devolucao);
        reserva.setLocalRetirada(dados.getLocalRetirada().trim());
        reserva.setLocalDevolucao(dados.getLocalDevolucao().trim());
        reserva.setValorTotal(total);
        reserva.setStatus("Pendente");

        Reserva salva = reservaRepository.saveAndFlush(reserva);

        return Map.of(
                "id", salva.getId(),
                "status", salva.getStatus(),
                "veiculoId", veiculo.getId(),
                "diarias", dias,
                "valorTotal", total,
                "mensagem", "Solicitacao registrada com sucesso."
        );
    }

    // Evita repetir a marca quando ela ja faz parte do modelo.
    private String nomeCompletoVeiculo(Veiculo veiculo) {
        String marca = veiculo.getMarca() == null
                ? "" : veiculo.getMarca().trim();

        String modelo = veiculo.getModelo() == null
                ? "" : veiculo.getModelo().trim();

        if (marca.isEmpty()) {
            return modelo;
        }

        if (modelo.isEmpty()) {
            return marca;
        }

        String marcaMinuscula = marca.toLowerCase(Locale.ROOT);
        String modeloMinusculo = modelo.toLowerCase(Locale.ROOT);

        if (modeloMinusculo.equals(marcaMinuscula)
                || modeloMinusculo.startsWith(marcaMinuscula + " ")) {
            return modelo;
        }

        return marca + " " + modelo;
    }

    private ReservaResumo resumo(Reserva r) {
        long minutos = Duration.between(
                r.getDataRetirada(), r.getDataDevolucao()).toMinutes();

        long dias = (minutos + 1439) / 1440;

        return new ReservaResumo(
                r.getId(),
                r.getDataReserva(),
                r.getCliente().getNome(),
                nomeCompletoVeiculo(r.getVeiculo()),
                r.getVeiculo().getId(),
                r.getDataRetirada(),
                r.getDataDevolucao(),
                r.getLocalRetirada(),
                r.getLocalDevolucao(),
                dias,
                r.getVeiculo().getValorDiaria(),
                r.getValorTotal(),
                r.getStatus()
        );
    }

    public record AtualizacaoStatus(String status) {}

    public record ReservaResumo(
            Integer id,
            LocalDateTime criadoEm,
            String nome,
            String modelo,
            String unidade,
            LocalDateTime retirada,
            LocalDateTime devolucao,
            String localRetirada,
            String localDevolucao,
            long dias,
            BigDecimal diaria,
            BigDecimal total,
            String status
    ) {}

    private boolean vazio(String valor) {
        return valor == null || valor.isBlank();
    }

    private boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        for (int tamanho = 9; tamanho <= 10; tamanho++) {
            int soma = 0;

            for (int i = 0; i < tamanho; i++) {
                soma += Character.digit(cpf.charAt(i), 10)
                        * (tamanho + 1 - i);
            }

            int digito = (soma * 10) % 11;

            if (digito == 10) {
                digito = 0;
            }

            if (digito != Character.digit(cpf.charAt(tamanho), 10)) {
                return false;
            }
        }

        return true;
    }
}
