package com.example.testeapifutebol.Excecao;



//Campos obrigatórios ausentes → 400 BAD REQUEST
//Nome menos de 2 letras → 400 BAD REQUEST
//Estado inexistente → 400 BAD REQUEST
//Data no futuro → 400 BAD REQUEST
//Gols negativos → 400 BAD REQUEST

import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
@ResponseStatus (HttpStatus.BAD_REQUEST)

public class RegraDeInvalidosExcecao400 extends RuntimeException{
    public RegraDeInvalidosExcecao400(String message) {
        super(message);
    }

}
