package com.example.demo.assets;

import com.example.demo.assets.model.Asset;
import com.example.demo.assets.model.DepositMoneyDto;
import com.example.demo.assets.model.WithdrawMoneyDto;
import com.example.demo.assets.service.AssetNotFoundException;
import com.example.demo.assets.service.AssetService;
import com.example.demo.assets.service.NotEnoughMoneyException;
import com.example.demo.customers.service.CustomerNotFoundException;
import com.example.demo.utils.ControllerUtils;
import io.micrometer.observation.annotation.Observed;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing customer assets.
 * <p>
 * This provides endpoints to view, deposit, and withdraw funds.
 * <p>
 * Authentication: Basic with username `admin@gmail.com`, password `admin`.
 */
@RestController // Allows handling HTTP requests
@RequiredArgsConstructor // Auto-generates constructor
@RequestMapping("/apis/v1/assets") // Base URL for endpoints
@Log // Enables logging for this class
public class AssetController {

    private final AssetService assetService; // Service to manage assets

    /**
     * Retrieves all assets. Only ADMINs can access.
     * <p>
     * <b>Annotations:</b>
     * <ul>
     *   <li><code>@GetMapping</code>
     *     <ul><li>Maps GET requests.</li></ul>
     *   </li>
     *   <li><code>@PreAuthorize("hasAuthority('ADMIN')")</code>
     *     <ul><li>Limits to ADMIN access.</li></ul>
     *   </li>
     *   <li><code>@Observed</code>
     *     <ul><li>Tracks metrics.</li></ul>
     *   </li>
     * </ul>
     *
     * @return list of all assets.
     *
     * Example:
     * ```bash
     * curl -u admin@gmail.com:admin -X GET "http://localhost:8080/apis/v1/assets"
     * ```
     */
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    @Observed(name = "getAllAssets", contextualName = "get-all-assets")
    public ResponseEntity<List<Asset>> getAllAssets() {
        log.info("Get All Assets Called..");
        try {
            Thread.sleep(200); // Simulates delay
        } catch (Throwable e) {
            // Handle exception if necessary
        }
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    /**
     * Retrieves assets of a specific customer.
     * Admins or the customer can access.
     *
     * <p><b>Annotations:</b></p>
     * <ul>
     *   <li><code>@GetMapping("/{customerId}")</code>
     *     <ul><li>Maps GET requests with a customer ID.</li></ul>
     *   </li>
     *   <li><code>@PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")</code>
     *     <ul><li>Access limited to admin or specified customer.</li></ul>
     *   </li>
     *   <li><code>@Observed</code>
     *     <ul><li>Tracks metrics for observability.</li></ul>
     *   </li>
     * </ul>
     *
     * @param customerId ID of the customer.
     * @return list of assets for the customer.
     * @throws CustomerNotFoundException if customer is not found.
     *
     * Example:
     * ```bash
     * curl -u admin@gmail.com:admin -X GET "http://localhost:8080/apis/v1/assets/{customerId}"
     * ```
     */
    @GetMapping("/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    @Observed(name = "getAssetsOfCustomer", contextualName = "get-assets-of-customer")
    public ResponseEntity<List<Asset>> getAssetsOfCustomer(
            @PathVariable("customerId") long customerId
    ) throws CustomerNotFoundException {
        log.info("Getting assets for customer ID: " + customerId);
        try {
            Thread.sleep(200); // Simulates delay
        } catch (Throwable e) {
            // Handle exception if necessary
        }
        return ResponseEntity.ok(assetService.getAssetsOfCustomer(customerId));
    }

    /**
     * Deposits money into a customer's asset account.
     * Admins or the customer can access.
     *
     * <p><b>Annotations:</b></p>
     * <ul>
     *   <li><code>@PostMapping("/deposit/{customerId}")</code>
     *     <ul><li>Maps POST requests with customer ID.</li></ul>
     *   </li>
     *   <li><code>@PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")</code>
     *     <ul><li>Access limited to admin or specified customer.</li></ul>
     *   </li>
     *   <li><code>@Valid</code>
     *     <ul><li>Validates request body.</li></ul>
     *   </li>
     *   <li><code>@RequestBody</code>
     *     <ul><li>Binds request body to parameter.</li></ul>
     *   </li>
     * </ul>
     *
     * @param customerId ID of the customer.
     * @param depositMoneyDto deposit details.
     * @param bindingResult result of validation.
     * @return updated balance, or BAD_REQUEST if validation fails.
     * @throws AssetNotFoundException if the asset is not found.
     *
     * Example:
     * ```bash
     * curl -u admin@gmail.com:admin -X POST "http://localhost:8080/apis/v1/assets/deposit/{customerId}"
     * -H "Content-Type: application/json" -d '{"amount": 100.0}'
     * ```
     */
    @PostMapping("/deposit/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    public ResponseEntity<Double> depositMoney(
            @PathVariable("customerId") long customerId,
            @Valid @RequestBody DepositMoneyDto depositMoneyDto,
            BindingResult bindingResult
    ) throws AssetNotFoundException {
        if (bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult); // Log validation errors
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(assetService.depositMoney(customerId, depositMoneyDto));
        }
    }

    /**
     * Withdraws money from a customer's asset account.
     * Admins or the customer can access.
     *
     * <p><b>Annotations:</b></p>
     * <ul>
     *   <li><code>@PostMapping("/withdraw/{customerId}")</code>
     *     <ul><li>Maps POST requests with customer ID.</li></ul>
     *   </li>
     *   <li><code>@PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")</code>
     *     <ul><li>Access limited to admin or specified customer.</li></ul>
     *   </li>
     *   <li><code>@Valid</code>
     *     <ul><li>Validates request body.</li></ul>
     *   </li>
     *   <li><code>@RequestBody</code>
     *     <ul><li>Binds request body to parameter.</li></ul>
     *   </li>
     * </ul>
     *
     * @param customerId ID of the customer.
     * @param withdrawMoneyDto withdrawal details.
     * @param bindingResult result of validation.
     * @return updated balance, or BAD_REQUEST if validation fails.
     * @throws NotEnoughMoneyException if balance is insufficient.
     * @throws AssetNotFoundException if the asset is not found.
     *
     * Example:
     * ```bash
     * curl -u admin@gmail.com:admin -X POST
     *      "http://localhost:8080/apis/v1/assets/withdraw/{customerId}"
     *      -H "Content-Type: application/json" -d '{"amount": 50.0}'
     * ```
     */
    @PostMapping("/withdraw/{customerId}")
    @PreAuthorize("hasAuthority('ADMIN') || #customerId == principal.id")
    public ResponseEntity<Double> withdrawMoney(
            @PathVariable("customerId") long customerId,
            @Valid @RequestBody WithdrawMoneyDto withdrawMoneyDto,
            BindingResult bindingResult
    ) throws NotEnoughMoneyException, AssetNotFoundException {
        if (bindingResult.hasErrors()) {
            ControllerUtils.logErrors(bindingResult); // Log validation errors
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            return ResponseEntity.ok(assetService.withdrawMoney(customerId, withdrawMoneyDto));
        }
    }
}
