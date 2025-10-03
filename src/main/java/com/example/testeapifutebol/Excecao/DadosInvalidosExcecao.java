package com.example.testeapifutebol.Excecao;



//Campos obrigatórios ausentes → 400 BAD REQUEST
//Nome menos de 2 letras → 400 BAD REQUEST
//Estado inexistente → 400 BAD REQUEST
//Data no futuro → 400 BAD REQUEST
//Gols negativos → 400 BAD REQUEST

public class DadosInvalidosExcecao extends RuntimeException{
    public DadosInvalidosExcecao(String message) {
        super(message);
    }

}
