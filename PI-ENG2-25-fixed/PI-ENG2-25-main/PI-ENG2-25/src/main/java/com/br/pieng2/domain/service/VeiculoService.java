package com.br.pieng2.domain.service;

import com.br.pieng2.domain.dao.VeiculoDAO;
import com.br.pieng2.domain.model.Veiculo;

import java.util.List;
import java.util.Optional;

public class VeiculoService {
    private final VeiculoDAO dao;

    public VeiculoService(VeiculoDAO dao) {
        this.dao = dao;
    }

    public List<Veiculo> listar() { return dao.listar(); }

    public Optional<Veiculo> buscar(int id) { return dao.buscar(id); }

    public void salvar(Veiculo v) { dao.salvar(v); }

    public void remover(int id) { dao.remover(id); }
}
