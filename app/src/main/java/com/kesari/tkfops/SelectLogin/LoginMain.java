package com.kesari.tkfops.SelectLogin;

/**
 * Created by kesari on 26/04/17.
 */

public class LoginMain {
    private String message;

    private String status;

    private User user;


    public String getMessage ()
    {
        return message;
    }

    public void setMessage (String message)
    {
        this.message = message;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public User getUser ()
    {
        return user;
    }

    public void setUser (User user)
    {
        this.user = user;
    }
}
