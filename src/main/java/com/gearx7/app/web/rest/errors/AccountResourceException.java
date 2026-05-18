package com.gearx7.app.web.rest.errors;

public class AccountResourceException extends BadRequestAlertException {

    private static final long serialVersionUID = 1L;

    public AccountResourceException(String message) {
        super(ErrorConstants.DEFAULT_TYPE, message, "accountManagement", "accountError");
    }
}
