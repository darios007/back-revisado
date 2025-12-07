package com.br.pieng2.domain.service;

import com.br.pieng2.domain.dao.MovimentacaoDAO;
import com.br.pieng2.domain.model.Movimentacao;

import java.util.List;
import java.util.Optional;

public class MovimentacaoService {
    private final MovimentacaoDAO dao;

    public MovimentacaoService(MovimentacaoDAO dao) {
        this.dao = dao;
    }

    public List<Movimentacao> listar() { return dao.listar(); }

    public Optional<Movimentacao> buscar(int id) { return dao.buscar(id); }

    public void salvar(Movimentacao m) { dao.salvar(m); }

    public void remover(int id) { dao.remover(id); }
}
