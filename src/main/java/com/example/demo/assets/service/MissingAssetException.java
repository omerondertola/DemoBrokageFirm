package com.example.demo.assets.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(
        value = HttpStatus.NOT_FOUND,
        code = HttpStatus.NOT_FOUND,
        reason = "Missing Asset Exception")
public class MissingAssetException extends AssetNotFoundException {
}