package com.syamsundar.order_service.common.exception;

public class OrderNotFoundException extends RuntimeException{

    public OrderNotFoundException(String message){super(message);}
}
