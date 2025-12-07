package com.br.pieng2.domain.model;

public class Veiculo {
    private int id;
    private String placa;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private boolean ativo;

    public Veiculo() {}

    public Veiculo(int id, String placa, String marca, String modelo, int anoFabricacao, boolean ativo) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = anoFabricacao;
        this.ativo = ativo;
    }

    // getters / setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getAnoFabricacao() { return anoFabricacao; }
    public void setAnoFabricacao(int anoFabricacao) { this.anoFabricacao = anoFabricacao; }
    public boolean isAtivo() { return ativo; }
    public void setAtivo(boolean ativo) { this.ativo = ativo; }

    @Override
    public String toString() {
        return id + "," + placa + "," + marca + "," + modelo + "," + anoFabricacao + "," + (ativo ? "1":"0");
    }

    public static Veiculo fromString(String line) {
        String[] p = line.split(",");
        if (p.length < 6) return null;
        try {
            int id = Integer.parseInt(p[0]);
            String placa = p[1];
            String marca = p[2];
            String modelo = p[3];
            int ano = Integer.parseInt(p[4]);
            boolean ativo = "1".equals(p[5]);
            return new Veiculo(id, placa, marca, modelo, ano, ativo);
        } catch (Exception e) {
            return null;
        }
    }
}
