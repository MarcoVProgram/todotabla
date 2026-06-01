package com.decroly.todotabla;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

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
        System.out.println(patata);
        assertTrue(patata);
    }

    // mayo test
    @Test
    public void getConnectioIsCorrect() {
        boolean estado = false;
        try {
            Connection conexion = BDD.getConnection();
            estado = conexion.isValid(120);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(estado);
    }

    
}
