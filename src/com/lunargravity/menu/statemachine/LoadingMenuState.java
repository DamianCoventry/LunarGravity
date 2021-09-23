package com.lunargravity.menu.statemachine;

import com.lunargravity.application.*;
import com.lunargravity.menu.view.MenuBuilderObserver;

import java.io.IOException;

public class LoadingMenuState extends StateBase {
    public LoadingMenuState(IStateMachineContext context) {
        super(context);
    }

    @Override
    public void begin() throws IOException, InterruptedException {
        MenuBuilderObserver menuBuilderObserver = new MenuBuilderObserver(getRenderer(), getManualFrameUpdater());

        getContext().startMenu(menuBuilderObserver);

        changeState(new RunningMenuState(getContext()));

        menuBuilderObserver.freeResources();
    }
}
