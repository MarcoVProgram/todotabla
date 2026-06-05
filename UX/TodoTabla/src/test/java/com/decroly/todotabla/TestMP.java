package com.decroly.todotabla;


import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import com.decroly.todotabla.model.Integrante;
import com.decroly.todotabla.model.sql.IntegrantesBDD;
import org.junit.jupiter.api.*;

import com.decroly.todotabla.*;

public class TestMP {

    public static boolean main(){
        boolean estado = false;

        try {
            for (Object i : IntegrantesBDD.getIntegrantes().values()) {
                assertNotNull(i, "Los datos van mal");
                assertEquals(i.getClass(), Integrante.class);
            }
            estado = true;
        } catch (Exception e) {
            return false;
        }

        return estado;
    }
}
