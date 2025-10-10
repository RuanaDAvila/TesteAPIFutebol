package com.example.testeapifutebol.Excecao;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)

public class RegraDeExcecao409 extends RuntimeException{
   public  RegraDeExcecao409(String message) {
       super(message);
   }




}
