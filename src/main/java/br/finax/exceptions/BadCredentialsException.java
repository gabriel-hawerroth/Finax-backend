package br.finax.exceptions;

public class BadCredentialsException extends RuntimeException {
    public BadCredentialsException() {
        super();
    }

    public BadCredentialsException(String msg) {
        super(msg);
    }
}
