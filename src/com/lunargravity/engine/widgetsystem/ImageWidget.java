package com.lunargravity.engine.widgetsystem;

import org.joml.Matrix4f;

import java.io.IOException;

public class ImageWidget extends WidgetObserver {
    public ImageWidget(WidgetManager widgetManager) {
        super(widgetManager);
    }

    @Override
    protected void initialiseChildren(WidgetCreateInfo wci) throws IOException {
        super.initialiseChildren(wci);
        // anything to do?
    }

    @Override
    public void freeNativeResources() {
        super.freeNativeResources();
        // anything to do?
    }

    @Override
    public void widgetDraw(Matrix4f projectionMatrix) {
        super.widgetDraw(projectionMatrix);
        // anything to do?
    }
}
