package com.lunargravity.dogfight.statemachine;

import com.lunargravity.application.IStateMachineContext;
import com.lunargravity.application.StateBase;
import com.lunargravity.dogfight.view.IDogfightView;
import com.lunargravity.engine.timeouts.TimeoutManager;

import java.io.IOException;

public class GetReadyState extends StateBase {
    public static final int MIN_SECONDS = 1;
    public static final int MAX_SECONDS = 3;
    private int _timeoutId;

    public GetReadyState(IStateMachineContext context) {
        super(context);
        _timeoutId = 0;
    }

    @Override
    public void begin() throws IOException {
        getDogfightView().showGetReady(MAX_SECONDS);

        _timeoutId = addTimeout(1000, (callCount) -> {
            if (callCount < MAX_SECONDS) {
                try {
                    getDogfightView().showGetReady(MAX_SECONDS - callCount);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return TimeoutManager.CallbackResult.KEEP_CALLING;
            }

            changeState(new RunningDogfightState(getContext()));
            _timeoutId = 0;
            return TimeoutManager.CallbackResult.REMOVE_THIS_CALLBACK;
        });
    }

    @Override
    public void end() {
        if (_timeoutId != 0) {
            removeTimeout(_timeoutId);
            _timeoutId = 0;
        }
    }

    private IDogfightView getDogfightView() {
        return (IDogfightView)getContext().getLogicView();
    }
}
