package com.decroly.todotabla;

import com.decroly.todotabla.model.sql.BDD;

import java.sql.Connection;

public class maintest {
    public static void main(String[] args) {
        try {
            Connection c = BDD.getConnection(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
