package com.lunargravity.race.controller;

import com.lunargravity.race.model.IRaceModel;

public class RaceController implements IRaceController {
    private final IRaceControllerEvents _eventHandler;
    private final IRaceModel _model;

    public RaceController(IRaceControllerEvents eventHandler, IRaceModel model) {
        _eventHandler = eventHandler;
        _model = model;
    }

    @Override
    public void think() {

    }

    @Override
    public void temp() {

    }
}