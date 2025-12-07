package com.br.pieng2.domain.dao;

import com.br.pieng2.domain.util.PersistenciaArquivo;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseDAO {
    protected PersistenciaArquivo persist;
    protected final String filename;
    protected final AtomicInteger nextId = new AtomicInteger(1);

    protected BaseDAO(PersistenciaArquivo persist, String filename) {
        this.persist = persist;
        this.filename = filename;
        initNextId();
    }

    private void initNextId() {
        int max = 0;
        for (String l : persist.lerLinhas(filename)) {
            try {
                String[] p = l.split(",", 2);
                int id = Integer.parseInt(p[0]);
                if (id > max) max = id;
            } catch (Exception ignored) {}
        }
        nextId.set(max + 1);
    }

    protected int getNextId() {
        return nextId.getAndIncrement();
    }
}
