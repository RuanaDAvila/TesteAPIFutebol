package com.example.testeapifutebol.Excecao;





import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
@ResponseStatus (HttpStatus.BAD_REQUEST)

public class RegraDeInvalidosExcecao400 extends RuntimeException{
    public RegraDeInvalidosExcecao400(String message) {
        super(message);
    }

}
