package com.example.testeapifutebol.Excecao;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

//Handler global que captura todas as exceções da aplicação
//Centraliza o tratamento de erros e padroniza as respostas HTTP
//@ControllerAdvice = captura exceções de toda a aplicação
//@ExceptionHandler = especifica qual exceção capturar

@ControllerAdvice
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DadosInvalidosExcecao.class)
    public ResponseEntity<String> handleDadosInvalidos(DadosInvalidosExcecao ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }





    //Trata conflitos de estádios duplicados
    @ExceptionHandler(EstadioExistenteExcecao.class)
    public ResponseEntity<String> handleEstadioExistente(EstadioExistenteExcecao ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //Trata conflitos de partidas duplicadas
    @ExceptionHandler(PartidaExistenteExcecao.class)
    public ResponseEntity<String> handlePartidaExistente(PartidaExistenteExcecao ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //metodo para tratar a excecao de clube em duplicidade e retornar 409
    @ExceptionHandler(ClubeExistenteExcecao.class)
    public ResponseEntity<Object> handleClubeExistente(ClubeExistenteExcecao ex) {
        Map<String, Object> body = new HashMap<>();
        body.put ("status", HttpStatus.CONFLICT.value());//409
        body.put ("erro", "ConflitoDeDados");
        body.put("mensagem", ex.getMessage());

        //retorna a resposta HTTP com o status 409
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }




}
