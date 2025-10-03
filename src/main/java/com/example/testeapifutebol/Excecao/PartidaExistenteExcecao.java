package com.example.testeapifutebol.Excecao;


//Exceção lançada quando tentamos cadastrar uma partida duplicada
public class PartidaExistenteExcecao extends RuntimeException {
    public PartidaExistenteExcecao(String message) {
        super(message);
    }
}
