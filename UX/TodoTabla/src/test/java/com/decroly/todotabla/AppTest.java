package com.decroly.todotabla;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.beans.Transient;
import java.sql.Connection;

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
        System.out.println(patata);
        assertTrue(patata);
    }

    
    @BeforeAll
    public static void getConnectioIsCorrect() {
        boolean estado = false;
        try {
            Connection conexion = BDD.getConnection();
            estado = conexion.isValid(120);
        } catch (Exception e) {
            assertTrue(false);
        }
        assertTrue(estado);
    }

    // mayo test
    @Test
    public void macarrones() {
        // TODO MARIO tiene que comprobar algo aqui. Repito MARIO
    }

    // ko test
    @Test
    public void wow() {
        // TODO MARCO tiene que comprobar algo aqui si quiere hacerlo aqui. Repito MARCO
    }
}
