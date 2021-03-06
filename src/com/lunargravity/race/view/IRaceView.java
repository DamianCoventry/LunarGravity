//
// Lunar Gravity
//
// This game is based upon the Amiga video game Gravity Force that was
// released in 1989 by Stephan Wenzler
//
// https://www.mobygames.com/game/gravity-force
// https://www.youtube.com/watch?v=m9mFtCvnko8
//
// This implementation is Copyright (c) 2021, Damian Coventry
// All rights reserved
// Written for Massey University course 159.261 Game Programming (Assignment 2)
//

package com.lunargravity.race.view;

import com.lunargravity.mvc.IView;

import java.io.IOException;

public interface IRaceView extends IView {
    void showResultsWidget() throws IOException;
    void showPausedWidget() throws IOException;
    void showGetReady(int i) throws IOException;
    void showCompletedWidget() throws IOException;
    void showLevelStatusBar();
}
