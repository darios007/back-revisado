package com.br.pieng2.domain.view;

import com.br.pieng2.domain.dao.DespesaDAO;
import com.br.pieng2.domain.dao.MovimentacaoDAO;
import com.br.pieng2.domain.dao.VeiculoDAO;
import com.br.pieng2.domain.model.Despesa;
import com.br.pieng2.domain.model.Movimentacao;
import com.br.pieng2.domain.model.Veiculo;
import com.br.pieng2.domain.service.*;
import com.br.pieng2.domain.util.PersistenciaArquivo;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MainFrame extends JFrame {
    private final VeiculoService veiculoService;
    private final DespesaService despesaService;
    private final MovimentacaoService movimentacaoService;
    private final RelatorioService relatorioService;

    private final DefaultTableModel veiculosModel = new DefaultTableModel(new String[]{"id","placa","marca","modelo","ano","ativo"}, 0);
    private final DefaultTableModel movModel = new DefaultTableModel(new String[]{"id","idVeic","idTipo","desc","data","valor"}, 0);

    public MainFrame() {
        PersistenciaArquivo persist = new PersistenciaArquivo("data");
        VeiculoDAO veicDao = new VeiculoDAO(persist);
        DespesaDAO despDao = new DespesaDAO(persist);
        MovimentacaoDAO movDao = new MovimentacaoDAO(persist);
        veiculoService = new VeiculoService(veicDao);
        despesaService = new DespesaService(despDao);
        movimentacaoService = new MovimentacaoService(movDao);
        relatorioService = new RelatorioService(movimentacaoService, despesaService, veiculoService);

        setTitle("Controle de Gastos da Frota - Backend Test UI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900,600);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        tabs.add("Veículos", buildVeiculosPanel());
        tabs.add("Tipos Despesa", buildTiposPanel());
        tabs.add("Movimentações", buildMovPanel());
        tabs.add("Relatórios", buildRelatorioPanel());

        add(tabs);

        loadVeiculos();
        loadMovimentacoes();
        loadTipos();
    }

    private JPanel buildVeiculosPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(veiculosModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout());
        JTextField placa = new JTextField(8);
        JTextField marca = new JTextField(8);
        JTextField modelo = new JTextField(8);
        JTextField ano = new JTextField(4);
        JCheckBox ativo = new JCheckBox("Ativo", true);
        JButton salvar = new JButton("Salvar");
        JButton remover = new JButton("Remover selecionado");

        form.add(new JLabel("Placa")); form.add(placa);
        form.add(new JLabel("Marca")); form.add(marca);
        form.add(new JLabel("Modelo")); form.add(modelo);
        form.add(new JLabel("Ano")); form.add(ano);
        form.add(ativo); form.add(salvar); form.add(remover);
        p.add(form, BorderLayout.SOUTH);

        salvar.addActionListener(ev -> {
            try {
                Veiculo v = new Veiculo();
                v.setPlaca(placa.getText());
                v.setMarca(marca.getText());
                v.setModelo(modelo.getText());
                v.setAnoFabricacao(Integer.parseInt(ano.getText()));
                v.setAtivo(ativo.isSelected());
                veiculoService.salvar(v);
                loadVeiculos();
                placa.setText(""); marca.setText(""); modelo.setText(""); ano.setText(""); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar veículo: " + ex.getMessage());
            }
        });

        remover.addActionListener(ev -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int id = Integer.parseInt(veiculosModel.getValueAt(r,0).toString());
                veiculoService.remover(id);
                loadVeiculos();
            }
        });

        return p;
    }

    private JPanel buildTiposPanel() {
        JPanel p = new JPanel(new BorderLayout());
        DefaultTableModel tiposModel = new DefaultTableModel(new String[]{"id","descricao"},0);
        JTable t = new JTable(tiposModel);
        p.add(new JScrollPane(t), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout());
        JTextField desc = new JTextField(12);
        JButton salvar = new JButton("Salvar Tipo");
        JButton remover = new JButton("Remover selecionado");

        form.add(new JLabel("Descrição")); form.add(desc); form.add(salvar); form.add(remover);
        p.add(form, BorderLayout.SOUTH);

        salvar.addActionListener(ev -> {
            try {
                Despesa d = new Despesa();
                d.setDescricao(desc.getText().toLowerCase().trim());
                despesaService.salvar(d);
                loadTipos();
                desc.setText(""); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar tipo: " + ex.getMessage());
            }
        });

        remover.addActionListener(ev -> {
            int r = t.getSelectedRow();
            if (r >= 0) {
                int id = Integer.parseInt(tiposModel.getValueAt(r,0).toString());
                despesaService.remover(id);
                loadTipos();
            }
        });

        return p;
    }

    private JPanel buildMovPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTable table = new JTable(movModel);
        p.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel form = new JPanel(new FlowLayout());
        JTextField idVeic = new JTextField(4);
        JTextField idTipo = new JTextField(4);
        JTextField desc = new JTextField(12);
        JTextField data = new JTextField(8); data.setText("2025-01-01");
        JTextField valor = new JTextField(6);
        JButton salvar = new JButton("Registrar");
        JButton remover = new JButton("Remover selecionado");

        form.add(new JLabel("idVeic")); form.add(idVeic);
        form.add(new JLabel("idTipoDesp")); form.add(idTipo);
        form.add(new JLabel("Descrição")); form.add(desc);
        form.add(new JLabel("Data(YYYY-MM-DD)")); form.add(data);
        form.add(new JLabel("Valor")); form.add(valor);
        form.add(salvar); form.add(remover);
        p.add(form, BorderLayout.SOUTH);

        salvar.addActionListener(ev -> {
            try {
                Movimentacao m = new Movimentacao();
                m.setIdVeiculo(Integer.parseInt(idVeic.getText()));
                m.setIdTipoDespesa(Integer.parseInt(idTipo.getText()));
                m.setDescricao(desc.getText());
                m.setData(LocalDate.parse(data.getText()));
                m.setValor(Double.parseDouble(valor.getText()));
                movimentacaoService.salvar(m);
                loadMovimentacoes();
                idVeic.setText(""); idTipo.setText(""); desc.setText(""); valor.setText(""); 
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro ao salvar movimentação: " + ex.getMessage());
            }
        });

        remover.addActionListener(ev -> {
            int r = table.getSelectedRow();
            if (r >= 0) {
                int id = Integer.parseInt(movModel.getValueAt(r,0).toString());
                movimentacaoService.remover(id);
                loadMovimentacoes();
            }
        });

        return p;
    }

    private JPanel buildRelatorioPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea output = new JTextArea();
        output.setEditable(false);
        p.add(new JScrollPane(output), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout());
        JButton btn1 = new JButton("Listar Veículos Inativos");
        JButton btn2 = new JButton("Somatório Geral Mês (2025-01)");
        JButton btn3 = new JButton("Total Combustível Mês (2025-01)");
        JButton btn4 = new JButton("Somatório IPVA Ano 2025");
        JButton btn5 = new JButton("Multas por Veículo Ano 2025");

        controls.add(btn1); controls.add(btn2); controls.add(btn3); controls.add(btn4); controls.add(btn5);
        p.add(controls, BorderLayout.SOUTH);

        btn1.addActionListener(ev -> {
            List<com.br.pieng2.domain.model.Veiculo> inativos = relatorioService.listarVeiculosInativos();
            StringBuilder sb = new StringBuilder("Veículos inativos:\n");
            for (var v : inativos) sb.append(v.getId()).append(" - ").append(v.getPlaca()).append("\n");
            output.setText(sb.toString());
        });

        btn2.addActionListener(ev -> {
            double s = relatorioService.somatorioGeralMes(2025,1);
            output.setText("Somatório geral (jan/2025): " + s);
        });

        btn3.addActionListener(ev -> {
            double s = relatorioService.totalCombustivelMes(2025,1);
            output.setText("Total combustível (jan/2025): " + s);
        });

        btn4.addActionListener(ev -> {
            double s = relatorioService.somatorioIPVAAno(2025);
            output.setText("Somatório IPVA (2025): " + s);
        });

        btn5.addActionListener(ev -> {
            Map<Integer, Double> m = relatorioService.multasPorVeiculoAno(2025);
            StringBuilder sb = new StringBuilder("Multas por veículo (2025):\n");
            for (var e : m.entrySet()) {
                sb.append("Veic ").append(e.getKey()).append(": ").append(e.getValue()).append("\n");
            }
            output.setText(sb.toString());
        });

        return p;
    }

    private void loadVeiculos() {
        veiculosModel.setRowCount(0);
        for (Veiculo v : veiculoService.listar()) {
            veiculosModel.addRow(new Object[]{v.getId(), v.getPlaca(), v.getMarca(), v.getModelo(), v.getAnoFabricacao(), v.isAtivo()});
        }
    }

    private void loadTipos() {
        // reload tipos by reading file and setting a table in tipos panel (kept simple)
        // read file and update table in the tipos tab if needed (for brevity left minimal)
    }

    private void loadMovimentacoes() {
        movModel.setRowCount(0);
        for (Movimentacao m : movimentacaoService.listar()) {
            movModel.addRow(new Object[]{m.getId(), m.getIdVeiculo(), m.getIdTipoDespesa(), m.getDescricao(), m.getData().toString(), m.getValor()});
        }
    }
}
