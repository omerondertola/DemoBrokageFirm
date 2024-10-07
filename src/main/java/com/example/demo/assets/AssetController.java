package com.example.demo.assets;

import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.AssetService;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.DepositMoneyDto;
import com.example.demo.assets.model.WithdrawMoneyDto;
import com.example.demo.utils.ControllerUtils;
import com.example.demo.customers.service.CustomerNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/apis/v1/assets")
public class AssetController {

    private final AssetService assetService;


    // TODO - LIST ALL ASSETS REQUIREMENT
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<Asset>> getAllAssets() {
        return ResponseEntity.ok(assetService.getAllAssets());
    }


    // TODO - LIST ASSETS OF A CUSTOMER REQUOREMENT
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @GetMapping("/{customerId}")
    public ResponseEntity<List<Asset>> getAssetsOfCustomer(
            @PathVariable("customerId")
            long customerId
    ) throws CustomerNotFoundException {
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
