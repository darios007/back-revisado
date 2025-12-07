package com.br.pieng2.domain.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Movimentacao {
    private int id;
    private int idVeiculo;
    private int idTipoDespesa;
    private String descricao;
    private LocalDate data;
    private double valor;

    private static final DateTimeFormatter F = DateTimeFormatter.ISO_LOCAL_DATE;

    public Movimentacao() {}

    public Movimentacao(int id, int idVeiculo, int idTipoDespesa, String descricao, LocalDate data, double valor) {
        this.id = id;
        this.idVeiculo = idVeiculo;
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
    }

    // getters/setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getIdVeiculo() { return idVeiculo; }
    public void setIdVeiculo(int idVeiculo) { this.idVeiculo = idVeiculo; }
    public int getIdTipoDespesa() { return idTipoDespesa; }
    public void setIdTipoDespesa(int idTipoDespesa) { this.idTipoDespesa = idTipoDespesa; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }
    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }

    @Override
    public String toString() {
        return id + "," + idVeiculo + "," + idTipoDespesa + "," + descricao.replace(",", " ") + "," + data.format(F) + "," + valor;
    }

    public static Movimentacao fromString(String line) {
        String[] p = line.split(",", 6);
        if (p.length < 6) return null;
        try {
            int id = Integer.parseInt(p[0]);
            int idVeiculo = Integer.parseInt(p[1]);
            int idTipo = Integer.parseInt(p[2]);
            String desc = p[3];
            LocalDate data = LocalDate.parse(p[4], F);
            double valor = Double.parseDouble(p[5]);
            return new Movimentacao(id, idVeiculo, idTipo, desc, data, valor);
        } catch (Exception e) {
            return null;
        }
    }
}
