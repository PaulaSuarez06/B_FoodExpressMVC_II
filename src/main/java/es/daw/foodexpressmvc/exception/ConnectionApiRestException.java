package es.daw.foodexpressmvc.exception;

public class ConnectionApiRestException extends RuntimeException{
    public ConnectionApiRestException(String message){
        super(message);
    }
}
