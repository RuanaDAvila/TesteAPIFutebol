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

    @ExceptionHandler(RegraDeInvalidosExcecao400.class)
    public ResponseEntity<String> handleDadosInvalidos(RegraDeInvalidosExcecao400 ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler (RegraDeExcecao409.class)
    public ResponseEntity<String> handleConflito409(RegraDeExcecao409 ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(RegraDoNaoEncontradoExcecao404.class)
    public ResponseEntity<Map<String, String>> handleNaoEncontrado404(RegraDoNaoEncontradoExcecao404 ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("message", ex.getMessage());
        errorResponse.put("status", HttpStatus.NOT_FOUND.toString());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}
