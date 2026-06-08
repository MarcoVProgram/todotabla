package com.decroly.todotabla;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.decroly.todotabla.model.sql.BDD;

/**
 * Unit test for simple App.
 */
public class AppTest {

    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() {
        boolean patata = true;
        assertTrue(patata, "¿La logica esta bien?");
    }

    @BeforeAll
    public static void getConnectioIsCorrect() {
        boolean estado = false;
        try {
            Connection conexion = BDD.getConnection();
            assertNotNull(conexion, "Error con la conexion");
            estado = conexion.isValid(120);
        } catch (Exception e) {
            estado = false;
        }

        Assertions.assertEquals(true, estado, "Error de la base de datos");

        
    }

    // mayo test
    @Test
    public void macarrones() {
        boolean estado = false;

        estado = TestMP.main();

        assertTrue(estado, "El tests de Mario salio mal");
    }

    // ko test
    @Test
    public void wow() {
        boolean estado = false;

        estado = TestMV.main();

        assertTrue(estado, "El tests de Marco salio mal");
    }
}
