package com.br.pieng2.domain.service;

import com.br.pieng2.domain.dao.DespesaDAO;
import com.br.pieng2.domain.model.Despesa;

import java.util.List;
import java.util.Optional;

public class DespesaService {
    private final DespesaDAO dao;

    public DespesaService(DespesaDAO dao) {
        this.dao = dao;
    }

    public List<Despesa> listar() { return dao.listar(); }
    public Optional<Despesa> buscar(int id) { return dao.buscar(id); }
    public Despesa buscarPorDescricao(String descricao) { return dao.buscarPorDescricao(descricao); }
    public void salvar(Despesa d) { dao.salvar(d); }
    public void remover(int id) { dao.remover(id); }
}
