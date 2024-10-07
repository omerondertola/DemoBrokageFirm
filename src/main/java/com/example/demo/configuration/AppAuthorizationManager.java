package com.example.demo.configuration;

import com.example.demo.customers.model.Customer;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.function.Supplier;

/*
    THIS CLASS IS NOT USED.
 */
public class AppAuthorizationManager implements AuthorizationManager<RequestAuthorizationContext> {

    private static final AuthorizationDecision accessGranted =
            new AuthorizationDecision(true);

    private static final AuthorizationDecision accessDenied =
            new AuthorizationDecision(false);

    @Override
    public AuthorizationDecision check(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext context
    ) {
        if( authentication != null &&
            authentication.get().getPrincipal() != null &&
            authentication.get().getPrincipal() instanceof Customer customer
        ) {

            if(customer.isAdmin()) {
                return accessGranted;
            }

            if(isRequestURLGranted(customer, context)) {
                return accessGranted;
            }
        }
        return accessDenied;
    }

    private static boolean isRequestURLGranted(
            Customer principal,
            RequestAuthorizationContext context
    ) {
        String requestURL = String.valueOf(context.getRequest().getRequestURL());
        URL requestURLObj = null;
        try {
            requestURLObj = new URL(requestURL);
        } catch (MalformedURLException e) {
            e.printStackTrace(System.out);
            return false;
        }
        String hostName = requestURLObj.getHost();
        String portName = String.valueOf(requestURLObj.getPort());

        long id = principal.getId();
        var grantedUrls = new LinkedList<String>();
        String urlPrefix = "http://" + hostName + ":" + portName;
        grantedUrls.add(urlPrefix +"/apis/v1/orders/"+id);
        grantedUrls.add(urlPrefix +"/apis/v1/assets/"+id);
        grantedUrls.add(urlPrefix +"/apis/v1/assets/"+id+"/deposit");
        grantedUrls.add(urlPrefix +"/apis/v1/assets/"+id+"/withdraw");
        grantedUrls.add(urlPrefix +"/apis/v1/customers/"+id);

        return grantedUrls.stream().anyMatch(requestURL::startsWith);
    }

    @Override
    public void verify(
            Supplier<Authentication> authentication,
            RequestAuthorizationContext object
    ) {
        AuthorizationManager.super.verify(authentication, object);
    }
}
