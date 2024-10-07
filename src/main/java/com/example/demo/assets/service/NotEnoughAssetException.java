package com.example.demo.assets.service;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.BAD_REQUEST,
        code = HttpStatus.BAD_REQUEST,
        reason = "Not Enough usableSize(ASSET)")
public class NotEnoughAssetException extends Exception {

    public NotEnoughAssetException() {
        super("Not Enough usableSize(ASSET)");
    }
}

