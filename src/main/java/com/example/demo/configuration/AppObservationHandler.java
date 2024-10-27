package com.example.demo.configuration;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationHandler;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

@Log
@Component
public class AppObservationHandler implements ObservationHandler<Observation.Context> {


    @Override
    public void onStart(Observation.Context context) {
        log.info("Start on context [{}]" + context.getName());
    }

    @Override
    public void onError(Observation.Context context) {
        log.info("Error on context [{}]" + context.getName());
    }

    @Override
    public void onEvent(Observation.Event event, Observation.Context context) {
        log.info("Event on context [{}]" + context.getName()
         + " Event: " + event.toString());
    }

    @Override
    public void onScopeOpened(Observation.Context context) {
        log.info("Scope Opened on context [{}]" + context.getName());
    }

    @Override
    public void onScopeClosed(Observation.Context context) {
        log.info("Scope Closed on context [{}]" + context.getName());
    }

    @Override
    public void onScopeReset(Observation.Context context) {
        log.info("Scope Reset on context [{}]" + context.getName());
    }

    @Override
    public void onStop(Observation.Context context) {
        log.info("After on context [{}]" + context.getName());
    }

    @Override
    public boolean supportsContext(Observation.Context context) {
        return true;
    }
}
