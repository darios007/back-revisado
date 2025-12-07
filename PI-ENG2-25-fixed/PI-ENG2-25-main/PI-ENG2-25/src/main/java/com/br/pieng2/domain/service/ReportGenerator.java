package com.br.pieng2.domain.service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

public class ReportGenerator {

    private static final Path DATA_DIR = Paths.get("data");
    private static final Path REPORT_DIR = DATA_DIR.resolve("reports");

    public static void main(String[] args) {
        try {
            Files.createDirectories(REPORT_DIR);

            List<Veiculo> veiculos = readVeiculos();
            Map<Integer, String> tipos = readTiposDespesa();
            List<Movimentacao> movs = readMovimentacoes();

            exportVeiculosInativos(veiculos);
            exportDespesasPorVeiculo(movs, veiculos);
            exportSomatorioGeralPorMes(movs);
            exportCombustivelPorMes(movs, tipos);
            exportIPVAPorAno(movs, tipos);
            exportMultasPorAno(movs, tipos, 2025);

            System.out.println("Relatórios gerados em: " + REPORT_DIR.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ----------------------- LEITURA -----------------------

    private static List<Veiculo> readVeiculos() throws IOException {
        Path p = DATA_DIR.resolve("veiculos.txt");
        List<Veiculo> list = new ArrayList<>();
        if (!Files.exists(p)) return list;

        for (String line : Files.readAllLines(p)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");

            int id = Integer.parseInt(parts[0]);
            String placa = parts[1];
            String marca = parts[2];
            String modelo = parts[3];
            int ano = Integer.parseInt(parts[4]);
            boolean ativo = parts[5].trim().equals("1");

            list.add(new Veiculo(id, placa, marca, modelo, ano, ativo));
        }

        return list;
    }

    private static Map<Integer, String> readTiposDespesa() throws IOException {
        Path p = DATA_DIR.resolve("despesas.txt");
        Map<Integer, String> map = new HashMap<>();
        if (!Files.exists(p)) return map;

        for (String line : Files.readAllLines(p)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            map.put(Integer.parseInt(parts[0]), parts[1]);
        }

        return map;
    }

    private static List<Movimentacao> readMovimentacoes() throws IOException {
        Path p = DATA_DIR.resolve("movimentacoes.txt");
        List<Movimentacao> list = new ArrayList<>();
        if (!Files.exists(p)) return list;

        for (String line : Files.readAllLines(p)) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");

            list.add(new Movimentacao(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2]),
                    parts[3],
                    LocalDate.parse(parts[4]),
                    Double.parseDouble(parts[5])
            ));
        }

        return list;
    }

    // ----------------------- RELATÓRIOS -----------------------

    private static void exportVeiculosInativos(List<Veiculo> veiculos) throws IOException {
        Path out = REPORT_DIR.resolve("veiculos_inativos.csv");

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("id,placa,marca,modelo,ano,ativo\n");
            for (Veiculo v : veiculos) {
                if (!v.ativo) {
                    w.write(v.id + "," + v.placa + "," + v.marca + "," + v.modelo + "," + v.ano + ",false\n");
                }
            }
        }
    }

    private static void exportDespesasPorVeiculo(List<Movimentacao> movs,
                                                 List<Veiculo> veiculos) throws IOException {

        Path out = REPORT_DIR.resolve("despesas_por_veiculo.csv");

        Map<Integer, String> placaMap = veiculos.stream()
                .collect(Collectors.toMap(v -> v.id, v -> v.placa));

        Map<Integer, Double> valores = movs.stream()
                .collect(Collectors.groupingBy(
                        m -> m.idVeiculo,
                        Collectors.summingDouble(m -> m.valor)
                ));

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("idVeiculo,placa,total\n");
            for (var e : valores.entrySet()) {
                w.write(e.getKey() + "," +
                        placaMap.getOrDefault(e.getKey(), "") + "," +
                        e.getValue() + "\n");
            }
        }
    }

    private static void exportSomatorioGeralPorMes(List<Movimentacao> movs) throws IOException {
        Path out = REPORT_DIR.resolve("somatorio_geral_por_mes.csv");

        Map<YearMonth, Double> valores = movs.stream()
                .collect(Collectors.groupingBy(
                        m -> YearMonth.from(m.data),
                        Collectors.summingDouble(m -> m.valor)
                ));

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("ano-mes,total\n");
            for (var e : valores.entrySet()) {
                w.write(e.getKey() + "," + e.getValue() + "\n");
            }
        }
    }

    private static void exportCombustivelPorMes(List<Movimentacao> movs,
                                                Map<Integer, String> tipos) throws IOException {

        Path out = REPORT_DIR.resolve("combustivel_por_mes.csv");

        Set<Integer> combustiveis = tipos.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().toLowerCase().contains("combust"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<YearMonth, Double> valores = movs.stream()
                .filter(m -> combustiveis.contains(m.idTipoDespesa))
                .collect(Collectors.groupingBy(
                        m -> YearMonth.from(m.data),
                        Collectors.summingDouble(m -> m.valor)
                ));

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("ano-mes,total_combustivel\n");
            for (var e : valores.entrySet()) {
                w.write(e.getKey() + "," + e.getValue() + "\n");
            }
        }
    }

    private static void exportIPVAPorAno(List<Movimentacao> movs,
                                         Map<Integer, String> tipos) throws IOException {

        Path out = REPORT_DIR.resolve("ipva_por_ano.csv");

        Set<Integer> ipvas = tipos.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().toLowerCase().contains("ipva"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<Integer, Double> valores = movs.stream()
                .filter(m -> ipvas.contains(m.idTipoDespesa))
                .collect(Collectors.groupingBy(
                        m -> m.data.getYear(),
                        Collectors.summingDouble(m -> m.valor)
                ));

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("ano,total_ipva\n");
            for (var e : valores.entrySet()) {
                w.write(e.getKey() + "," + e.getValue() + "\n");
            }
        }
    }

    private static void exportMultasPorAno(List<Movimentacao> movs,
                                           Map<Integer, String> tipos,
                                           int ano) throws IOException {

        Path out = REPORT_DIR.resolve("multas_por_veiculo_" + ano + ".csv");

        Set<Integer> multas = tipos.entrySet().stream()
                .filter(e -> e.getValue() != null && e.getValue().toLowerCase().contains("multa"))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<Integer, Double> valores = movs.stream()
                .filter(m -> multas.contains(m.idTipoDespesa) && m.data.getYear() == ano)
                .collect(Collectors.groupingBy(
                        m -> m.idVeiculo,
                        Collectors.summingDouble(m -> m.valor)
                ));

        try (BufferedWriter w = Files.newBufferedWriter(out)) {
            w.write("idVeiculo,total_multas\n");
            for (var e : valores.entrySet()) {
                w.write(e.getKey() + "," + e.getValue() + "\n");
            }
        }
    }

    // -----------------------
    // CLASSES INTERNAS
    // -----------------------

    static class Veiculo {
        int id, ano;
        String placa, marca, modelo;
        boolean ativo;

        Veiculo(int id, String placa, String marca, String modelo, int ano, boolean ativo) {
            this.id = id;
            this.placa = placa;
            this.marca = marca;
            this.modelo = modelo;
            this.ano = ano;
            this.ativo = ativo;
        }
    }

    static class Movimentacao {
        int id, idVeiculo, idTipoDespesa;
        String descricao;
        LocalDate data;
        double valor;

        Movimentacao(int id, int idVeiculo, int idTipoDespesa,
                     String descricao, LocalDate data, double valor) {
            this.id = id;
            this.idVeiculo = idVeiculo;
            this.idTipoDespesa = idTipoDespesa;
            this.descricao = descricao;
            this.data = data;
            this.valor = valor;
        }
    }

}
