package com.br.pieng2.domain.dao;

import com.br.pieng2.domain.model.Veiculo;
import com.br.pieng2.domain.util.PersistenciaArquivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VeiculoDAO extends BaseDAO {
    public VeiculoDAO(PersistenciaArquivo p) {
        super(p, "veiculos.txt");
    }

    public List<Veiculo> listar() {
        List<Veiculo> out = new ArrayList<>();
        for (String l : persist.lerLinhas(filename)) {
            Veiculo v = Veiculo.fromString(l);
            if (v != null) out.add(v);
        }
        return out;
    }

    public Optional<Veiculo> buscar(int id) {
        return listar().stream().filter(v -> v.getId() == id).findFirst();
    }

    public void salvar(Veiculo v) {
        if (v.getId() == 0) {
            v.setId(getNextId());
            persist.appendLinha(filename, v.toString());
        } else {
            List<String> lines = new ArrayList<>();
            for (Veiculo exist : listar()) {
                if (exist.getId() == v.getId()) lines.add(v.toString());
                else lines.add(exist.toString());
            }
            persist.escreverLinhas(filename, lines);
        }
    }

    public void remover(int id) {
        List<String> lines = new ArrayList<>();
        for (Veiculo exist : listar()) {
            if (exist.getId() != id) lines.add(exist.toString());
        }
        persist.escreverLinhas(filename, lines);
    }
}
