package com.br.pieng2.domain.service;

import com.br.pieng2.domain.model.Despesa;
import com.br.pieng2.domain.model.Movimentacao;
import com.br.pieng2.domain.model.Veiculo;

import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class RelatorioService {
    private final MovimentacaoService movService;
    private final DespesaService despService;
    private final VeiculoService veiculoService;

    public RelatorioService(MovimentacaoService m, DespesaService d, VeiculoService v) {
        this.movService = m;
        this.despService = d;
        this.veiculoService = v;
    }

    // 1. Despesas por veículo (lista de movimentações do veículo)
    public List<Movimentacao> despesasPorVeiculo(int idVeiculo) {
        return movService.listar().stream()
                .filter(m -> m.getIdVeiculo() == idVeiculo)
                .collect(Collectors.toList());
    }

    // 2. Somatório geral de todos as despesas da frota em um determinado mês
    public double somatorioGeralMes(int ano, int mes) {
        return movService.listar().stream()
                .filter(m -> m.getData().getYear() == ano && m.getData().getMonthValue() == mes)
                .mapToDouble(Movimentacao::getValor).sum();
    }

    // 3. Total de gastos da frota com combustível em um determinado mês
    public double totalCombustivelMes(int ano, int mes) {
        Despesa combustivel = despService.buscarPorDescricao("combustivel");
        if (combustivel == null) return 0.0;
        int idComb = combustivel.getId();
        return movService.listar().stream()
                .filter(m -> m.getIdTipoDespesa() == idComb)
                .filter(m -> m.getData().getYear() == ano && m.getData().getMonthValue() == mes)
                .mapToDouble(Movimentacao::getValor).sum();
    }

    // 4. Somatório do IPVA de um determinado ano de toda a frota
    public double somatorioIPVAAno(int ano) {
        Despesa ipva = despService.buscarPorDescricao("ipva");
        if (ipva == null) return 0.0;
        int idIpva = ipva.getId();
        return movService.listar().stream()
                .filter(m -> m.getIdTipoDespesa() == idIpva)
                .filter(m -> m.getData().getYear() == ano)
                .mapToDouble(Movimentacao::getValor).sum();
    }

    // 5. Listar todos os veículos inativos na frota
    public List<Veiculo> listarVeiculosInativos() {
        return veiculoService.listar().stream().filter(v -> !v.isAtivo()).collect(Collectors.toList());
    }

    // 6. Relatório das multas pagas por veículo em um determinado ano.
    // Considera "multa" no tipo de despesa
    public Map<Integer, Double> multasPorVeiculoAno(int ano) {
        Despesa multa = despService.buscarPorDescricao("multa");
        Map<Integer, Double> result = new HashMap<>();
        if (multa == null) return result;
        int idMulta = multa.getId();
        for (Movimentacao m : movService.listar()) {
            if (m.getIdTipoDespesa() == idMulta && m.getData().getYear() == ano) {
                result.put(m.getIdVeiculo(), result.getOrDefault(m.getIdVeiculo(), 0.0) + m.getValor());
            }
        }
        return result;
    }

    // adicional: despesas por mês agrupadas por veículo
    public Map<YearMonth, Double> gastosPorMes() {
        return movService.listar().stream()
                .collect(Collectors.groupingBy(m -> YearMonth.from(m.getData()), Collectors.summingDouble(Movimentacao::getValor)));
    }
}
