package com.br.pieng2.domain.dao;

import com.br.pieng2.domain.model.Despesa;
import com.br.pieng2.domain.util.PersistenciaArquivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DespesaDAO extends BaseDAO {
    public DespesaDAO(PersistenciaArquivo p) {
        super(p, "tipos_despesa.txt");
    }

    public List<Despesa> listar() {
        List<Despesa> out = new ArrayList<>();
        for (String l : persist.lerLinhas(filename)) {
            Despesa d = Despesa.fromString(l);
            if (d != null) out.add(d);
        }
        return out;
    }

    public Optional<Despesa> buscar(int id) {
        return listar().stream().filter(d -> d.getId() == id).findFirst();
    }

    public Despesa buscarPorDescricao(String descricao) {
        return listar().stream()
                .filter(d -> d.getDescricao().equalsIgnoreCase(descricao))
                .findFirst().orElse(null);
    }

    public void salvar(Despesa d) {
        if (d.getId() == 0) {
            d.setId(getNextId());
            persist.appendLinha(filename, d.toString());
        } else {
            List<String> lines = new ArrayList<>();
            for (Despesa exist : listar()) {
                if (exist.getId() == d.getId()) lines.add(d.toString());
                else lines.add(exist.toString());
            }
            persist.escreverLinhas(filename, lines);
        }
    }

    public void remover(int id) {
        List<String> lines = new ArrayList<>();
        for (Despesa exist : listar()) {
            if (exist.getId() != id) lines.add(exist.toString());
        }
        persist.escreverLinhas(filename, lines);
    }
}
