package com.br.pieng2.domain.util;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenciaArquivo {
    private final Path folder;

    public PersistenciaArquivo(String folderName) {
        this.folder = Paths.get(folderName);
        try {
            if (!Files.exists(folder)) Files.createDirectories(folder);
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível criar pasta de dados: " + e.getMessage(), e);
        }
    }

    public List<String> lerLinhas(String filename) {
        Path f = folder.resolve(filename);
        if (!Files.exists(f)) return new ArrayList<>();
        try {
            return Files.readAllLines(f);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void escreverLinhas(String filename, List<String> linhas) {
        Path f = folder.resolve(filename);
        try {
            Files.write(f, linhas, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendLinha(String filename, String linha) {
        Path f = folder.resolve(filename);
        try (BufferedWriter bw = Files.newBufferedWriter(f, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            bw.write(linha);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
