package com.lunargravity.campaign.statemachine;

import com.lunargravity.application.IStateMachineContext;
import com.lunargravity.application.StateBase;

public class MissionPausedState extends StateBase {
    public MissionPausedState(IStateMachineContext context) {
        super(context);
    }
}