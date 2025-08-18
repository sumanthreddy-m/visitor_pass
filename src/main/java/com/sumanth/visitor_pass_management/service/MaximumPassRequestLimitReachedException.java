package com.sumanth.visitor_pass_management.service;

public class MaximumPassRequestLimitReachedException extends Exception {

    private static final long serialVersionUID = 1L; 

    public MaximumPassRequestLimitReachedException(String message) {
        super(message);
    }
}