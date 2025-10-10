package com.example.testeapifutebol.Excecao;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus (HttpStatus.NOT_FOUND)
public class RegraDoNaoEncontradoExcecao404 extends RuntimeException{
    public  RegraDoNaoEncontradoExcecao404(String message) {
        super(message);
    }



}
