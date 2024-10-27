package com.example.demo.assets;

import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.AssetService;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.DepositMoneyDto;
import com.example.demo.assets.model.WithdrawMoneyDto;
import com.example.demo.utils.ControllerUtils;
import com.example.demo.customers.service.CustomerNotFoundException;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1/assets")
@Log
public class AssetController {

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    private final AssetService assetService;


    // TODO - LIST ALL ASSETS REQUIREMENT
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Observed(name = "getAllAssets",
            contextualName = "get-all-assets")
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.info("Get All Assets Called..");
        try {
            Thread.sleep(200);
        } catch (Throwable e) {
        }
        return ResponseEntity.ok(assetService.getAllAssets());
    }


    // TODO - LIST ASSETS OF A CUSTOMER REQUOREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    @Observed(name = "getAllAssets",
            contextualName = "get-all-assets")
    public ResponseEntity<List<Asset>> getAssetsOfCustomer(
            @PathVariable("customerId")
            long customerId
    ) throws CustomerNotFoundException {
        log.info("getting customer information for: "+customerId);
        try {
            Thread.sleep(200);
        } catch (Throwable e) {
        }
        return ResponseEntity.ok(assetService.getAssetsOfCustomer(customerId));
    }


    // TODO - DEPOSIT MONEY REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id ")
    @PostMapping("/deposit/{customerId}")
    public ResponseEntity<Double> depositMoney(
            @PathVariable("customerId")
            long customerId,

            @Valid @RequestBody
            DepositMoneyDto depositMoneyDto,

            BindingResult bindingResult
    ) throws AssetNotFoundException {
        if(bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(
                    assetService.depositMoney(customerId, depositMoneyDto)
            );
        }
    }


    // TODO - WITHDRAW MONEY REQUIREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @PostMapping("/withdraw/{customerId}")
    public ResponseEntity<Double> withdrawMoney(
            @PathVariable("customerId")
            long customerId,

            @Valid @RequestBody
            WithdrawMoneyDto depositMoneyDto,

            BindingResult bindingResult
    ) throws NotEnoughMoneyException, AssetNotFoundException {
        if(bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult);
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(
                    assetService.withdrawMoney(customerId, depositMoneyDto));
        }
    }



}
