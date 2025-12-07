package com.br.pieng2.domain.model;

public class Despesa {
    private int id;
    private String descricao; // ex: combustivel, multa, ipva, manutencao

    public Despesa() {}
    public Despesa(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    @Override
    public String toString() {
        return id + "," + descricao;
    }

    public static Despesa fromString(String line) {
        String[] p = line.split(",", 2);
        if (p.length < 2) return null;
        try {
            int id = Integer.parseInt(p[0]);
            String desc = p[1];
            return new Despesa(id, desc);
        } catch (Exception e) {
            return null;
        }
    }
}
