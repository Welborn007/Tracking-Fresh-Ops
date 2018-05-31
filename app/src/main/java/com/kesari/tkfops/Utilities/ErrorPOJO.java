package com.kesari.tkfops.Utilities;

/**
 * Created by kesari on 01/06/17.
 */

public class ErrorPOJO {

    String message;

    private String[] errors;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getErrors() {
        return errors;
    }

    public void setErrors(String[] errors) {
        this.errors = errors;
    }
}
