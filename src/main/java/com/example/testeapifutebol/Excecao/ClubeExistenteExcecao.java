package com.example.testeapifutebol.Excecao;

public class ClubeExistenteExcecao extends  RuntimeException {
    public ClubeExistenteExcecao(String message) {
        super(message);
    }

}