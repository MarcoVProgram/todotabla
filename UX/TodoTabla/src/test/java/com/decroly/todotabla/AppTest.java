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
        boolean patata = false;
        System.out.println(patata);
        assertTrue(patata);
    }

    /* @Test
    public void getConnectioIsCorrect() {
        boolean estado = false;
        try {
            Connection conexion = BDD.getConnection();
            assertNotNull(conexion, "Error");
            estado = conexion.isValid(120);
        } catch (Exception e) {
            estado = false;
        }

        Assertions.assertEquals(true, estado, "Error");

        
    }

    // mayo test
    @Test
    public void macarrones() {
        boolean estado = false;
        // TODO MARIO tiene que comprobar algo aqui. Repito MARIO
        assertTrue(estado);
    }

    // ko test
    @Test
    public void wow() {
        boolean estado = false;
        // TODO MARCO tiene que comprobar algo aqui si quiere hacerlo aqui. Repito MARCO
        assertTrue(estado);
    } */
}
