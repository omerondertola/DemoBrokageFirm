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

/**
 * REST Controller for managing customer assets.
 * <p>
 * This controller provides endpoints to view, deposit, and withdraw funds from asset accounts.
 * <p>
 * Authentication: Basic Authentication with username `admin@gmail.com` and password `admin`.
 */
@RestController // Marks this class as a REST controller in Spring
@RequiredArgsConstructor // Automatically generates a constructor with required dependencies
@RequestMapping("/apis/v1/assets") // Base URL mapping for all endpoints in this controller
@Log // Enables Java logging for this class
public class AssetController {

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    // Service to handle asset-related operations
    private final AssetService assetService;

    /**
     * Retrieves all assets.
     * Accessible only to users with ADMIN authority.
     *
     * @return ResponseEntity containing a list of all assets.
     *
     * Example `curl` command:
     * ```bash
     * curl -u admin@gmail.com:admin -X GET "http://localhost:8080/apis/v1/assets"
     * ```
     */
    @GetMapping // Maps HTTP GET requests to this method
    @PreAuthorize("hasAuthority('ADMIN')") // Access restricted to users with ADMIN authority
    @Observed(name = "getAllAssets", contextualName = "get-all-assets") // For monitoring metrics with Micrometer
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.info("Get All Assets Called..");
        try {
            Thread.sleep(200); // Simulates delay; remove in production
        } catch (Throwable e) {
            // Log or handle exception
        }
        return ResponseEntity.ok(assetService.getAllAssets()); // Returns a list of all assets
    }

    /**
     * Retrieves all assets belonging to a specific customer.
     * Accessible to users with ADMIN authority or the specific customer.
     *
     * @param customerId The ID of the customer whose assets are requested.
     * @return ResponseEntity containing a list of the customer’s assets.
     * @throws CustomerNotFoundException if the customer is not found.
     *
     * Example `curl` command:
     * ```bash
     * curl -u admin@gmail.com:admin -X GET "http://localhost:8080/apis/v1/assets/{customerId}"
     * ```
     */
    @GetMapping("/{customerId}") // Maps HTTP GET requests with a customerId path variable to this method
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id") // Access restricted to admin or the customer
    @Observed(name = "getAssetsOfCustomer", contextualName = "get-assets-of-customer") // For tracking with Micrometer
    public ResponseEntity<List<Asset>> getAssetsOfCustomer(
            @PathVariable("customerId") long customerId // Binds customerId path variable to this parameter
    ) throws CustomerNotFoundException {
        log.info("Getting assets for customer ID: " + customerId);
        try {
            Thread.sleep(200); // Simulates delay; remove in production
        } catch (Throwable e) {
            // Log or handle exception
        }
        return ResponseEntity.ok(assetService.getAssetsOfCustomer(customerId)); // Returns the customer's assets
    }

    /**
     * Deposits money into a customer’s asset account.
     * Accessible to users with ADMIN authority or the specific customer.
     *
     * @param customerId The ID of the customer whose account will be credited.
     * @param depositMoneyDto The deposit amount and other deposit details.
     * @param bindingResult Used for validating the request body.
     * @return ResponseEntity containing the updated balance or BAD_REQUEST status if validation fails.
     * @throws AssetNotFoundException if the asset is not found.
     *
     * Example `curl` command:
     * ```bash
     * curl -u admin@gmail.com:admin -X POST "http://localhost:8080/apis/v1/assets/deposit/{customerId}" \
     *      -H "Content-Type: application/json" \
     *      -d '{"amount": 100.0}'
     * ```
     */
    @PostMapping("/deposit/{customerId}") // Maps HTTP POST requests with a customerId path variable to this method
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id") // Access restricted to admin or the customer
    public ResponseEntity<Double> depositMoney(
            @PathVariable("customerId") long customerId, // Binds customerId path variable to this parameter
            @Valid @RequestBody DepositMoneyDto depositMoneyDto, // Validates incoming JSON request data
            BindingResult bindingResult // Holds validation errors, if any
    ) throws AssetNotFoundException {
        if (bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult); // Logs validation errors
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Returns a 400 Bad Request if validation fails
        } else {
            return ResponseEntity.ok(assetService.depositMoney(customerId, depositMoneyDto)); // Returns updated balance
        }
    }

    /**
     * Withdraws money from a customer’s asset account.
     * Accessible to users with ADMIN authority or the specific customer.
     *
     * @param customerId The ID of the customer whose account will be debited.
     * @param withdrawMoneyDto The withdrawal amount and other withdrawal details.
     * @param bindingResult Used for validating the request body.
     * @return ResponseEntity containing the updated balance or BAD_REQUEST status if validation fails.
     * @throws NotEnoughMoneyException if there is insufficient balance.
     * @throws AssetNotFoundException if the asset is not found.
     *
     * Example `curl` command:
     * ```bash
     * curl -u admin@gmail.com:admin -X POST "http://localhost:8080/apis/v1/assets/withdraw/{customerId}" \
     *      -H "Content-Type: application/json" \
     *      -d '{"amount": 50.0}'
     * ```
     */
    @PostMapping("/withdraw/{customerId}") // Maps HTTP POST requests with a customerId path variable to this method
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id") // Access restricted to admin or the customer
    public ResponseEntity<Double> withdrawMoney(
            @PathVariable("customerId") long customerId, // Binds customerId path variable to this parameter
            @Valid @RequestBody WithdrawMoneyDto withdrawMoneyDto, // Validates incoming JSON request data
            BindingResult bindingResult // Holds validation errors, if any
    ) throws NotEnoughMoneyException, AssetNotFoundException {
        if (bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult); // Logs validation errors
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST); // Returns a 400 Bad Request if validation fails
        } else {
            return ResponseEntity.ok(assetService.withdrawMoney(customerId, withdrawMoneyDto)); // Returns updated balance
        }
    }
}
