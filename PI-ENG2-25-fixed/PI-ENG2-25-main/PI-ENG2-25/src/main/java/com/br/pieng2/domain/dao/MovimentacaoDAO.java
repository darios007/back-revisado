package com.br.pieng2.domain.dao;

import com.br.pieng2.domain.model.Movimentacao;
import com.br.pieng2.domain.util.PersistenciaArquivo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MovimentacaoDAO extends BaseDAO {
    public MovimentacaoDAO(PersistenciaArquivo p) {
        super(p, "movimentacoes.txt");
    }

    public List<Movimentacao> listar() {
        List<Movimentacao> out = new ArrayList<>();
        for (String l : persist.lerLinhas(filename)) {
            Movimentacao m = Movimentacao.fromString(l);
            if (m != null) out.add(m);
        }
        return out;
    }

    public Optional<Movimentacao> buscar(int id) {
        return listar().stream().filter(m -> m.getId() == id).findFirst();
    }

    public void salvar(Movimentacao m) {
        if (m.getId() == 0) {
            m.setId(getNextId());
            persist.appendLinha(filename, m.toString());
        } else {
            List<String> lines = new ArrayList<>();
            for (Movimentacao exist : listar()) {
                if (exist.getId() == m.getId()) lines.add(m.toString());
                else lines.add(exist.toString());
            }
            persist.escreverLinhas(filename, lines);
        }
    }

    public void remover(int id) {
        List<String> lines = new ArrayList<>();
        for (Movimentacao exist : listar()) {
            if (exist.getId() != id) lines.add(exist.toString());
        }
        persist.escreverLinhas(filename, lines);
    }
}
