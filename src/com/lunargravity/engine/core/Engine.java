package com.lunargravity.engine.core;

import com.jme3.bullet.PhysicsSpace;
import com.jme3.system.NativeLibraryLoader;
import com.lunargravity.engine.desktopwindow.*;
import com.lunargravity.engine.graphics.*;
import com.lunargravity.engine.timeouts.TimeoutManager;
import org.joml.Matrix4f;
import org.lwjgl.glfw.*;

import java.io.File;
import java.io.IOException;

public class Engine implements IEngine {
    static private final float PHYSICS_TIME_STEP = 0.01666666f; // which is 60 FPS

    private final IFrameConsumer _frameConsumer;
    private final IInputConsumer _inputConsumer;
    private final IViewportSizeObserver _viewportSizeObserver;

    private final GlRenderer _renderer;
    private final TimeoutManager _timeoutManager;
    private final PhysicsSpace _physicsSpace;
    private GlfwWindow _window;

    private long _currentFrameNo;
    private long _frameCount;
    private long _frameLengthMs;
    private long _currentTimeMs;
    private long _previousTimeMs;
    private long _previousTimestampMs;
    private double _frameDelta;
    private long _fps;

    public Engine(IFrameConsumer frameConsumer, IInputConsumer inputConsumer, IViewportSizeObserver viewportSizeObserver,
                  GlfwWindowConfig windowConfig) throws IOException {

        loadBullet3RuntimeLibrary();

        _frameConsumer = frameConsumer;
        _inputConsumer = inputConsumer;
        _viewportSizeObserver = viewportSizeObserver;
        _timeoutManager = new TimeoutManager();
        _physicsSpace = new PhysicsSpace(PhysicsSpace.BroadphaseType.DBVT);

        resetFrameCounters();
        createDesktopWindow(windowConfig);

        _renderer = new GlRenderer(new GlViewportConfig[] {
                createDefaultViewportConfig()
        });
    }

    @Override
    public void freeResources() {
        _window.freeResources();
    }

    @Override
    public void run() {
        resetFrameCounters();
        while (!_window.shouldClose()) {
            _currentTimeMs = System.currentTimeMillis();
            _previousTimeMs = updateFrameTime(_currentTimeMs, _previousTimeMs);

            _timeoutManager.dispatchTimeouts(_currentTimeMs);
            _frameConsumer.onFrameBegin(_currentFrameNo, _currentTimeMs, _frameDelta);

            _physicsSpace.update(PHYSICS_TIME_STEP, 0);
            _frameConsumer.onFrameThink();

            _renderer.clearBuffers();
            for (int i = 0; i < _renderer.getNumViewports(); ++i) {
                GlViewport viewport = _renderer.getViewport(i);
                if (viewport != null) {
                    viewport.activate();
                    _frameConsumer.onFrameDraw3d(i, viewport.getPerspectiveMatrix());
                    _frameConsumer.onFrameDraw2d(i, viewport.getOrthographicMatrix());
                }
            }

            _window.flipPages();
            _frameConsumer.onFrameEnd();
            updateFps();
        }
    }

    @Override
    public void exit() {
        _window.close();
    }

    @Override
    public void setDefaultViewport() {
        _renderer.setViewports(new GlViewportConfig[] {
                createDefaultViewportConfig()
        });
    }

    @Override
    public void prepareNewFrame() {
        _renderer.clearBuffers();
    }

    @Override
    public void submitFrame() {
        _window.flipPages();
    }

    @Override
    public Matrix4f getPerspectiveProjectionMatrix() {
        if (_renderer.getNumViewports() == 0) {
            throw new RuntimeException("There are no viewports");
        }
        return _renderer.getViewport(0).getPerspectiveMatrix();
    }

    @Override
    public Matrix4f getOrthographicProjectionMatrix() {
        if (_renderer.getNumViewports() == 0) {
            throw new RuntimeException("There are no viewports");
        }
        return _renderer.getViewport(0).getOrthographicMatrix();
    }

    @Override
    public long getFps() {
        return _fps;
    }

    @Override
    public long getFrameLengthMs() {
        return _frameLengthMs;
    }

    @Override
    public TimeoutManager getTimeoutManager() {
        return _timeoutManager;
    }

    @Override
    public PhysicsSpace getPhysicsSpace() {
        return _physicsSpace;
    }

    @Override
    public GlRenderer getRenderer() {
        return _renderer;
    }

    @Override
    public boolean isSoundEnabled() {
        // TODO
        return false;
    }

    @Override
    public void enableSound() {
        // TODO
    }

    @Override
    public void disableSound() {
        // TODO
    }

    @Override
    public void setSoundVolume(int volume) {
        // TODO
    }

    @Override
    public int getSoundVolume() {
        // TODO
        return 0;
    }

    @Override
    public boolean isMusicEnabled() {
        // TODO
        return false;
    }

    @Override
    public void enableMusic() {
        // TODO
    }

    @Override
    public void disableMusic() {
        // TODO
    }

    @Override
    public void setMusicVolume(int volume) {
        // TODO
    }

    @Override
    public int getMusicVolume() {
        // TODO
        return 0;
    }

    private void loadBullet3RuntimeLibrary() {
        File directory = new File("lib");
        boolean success = NativeLibraryLoader.loadLibbulletjme(true, directory, "Release", "Dp");
        if (!success) {
            throw new RuntimeException("Failed to load the Bullet3 run-time library");
        }
    }

    private void resetFrameCounters() {
        _currentFrameNo = _frameCount = 0;
        _frameLengthMs = _currentTimeMs = 0;
        _previousTimeMs = _previousTimestampMs = 0;
        _fps = 0; _frameDelta = 0.0;
    }

    private void createDesktopWindow(GlfwWindowConfig windowConfig) throws IOException {
        _window = new GlfwWindow(windowConfig, new GLFWWindowSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (_renderer == null || _viewportSizeObserver == null) {
                    return;
                }
                for (int i = 0; i < _renderer.getNumViewports(); ++i) {
                    GlViewportConfig newConfig = _viewportSizeObserver.onViewportSizeChanged(
                            i, _renderer.getViewport(i).getConfig(),
                            (int)_window.getWidth(), (int)_window.getHeight());
                    if (newConfig != null) {
                        _renderer.getViewport(i).setConfig(newConfig);
                    }
                }
            }
        });

        _window.setKeyCallback(new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods) {
                if (_inputConsumer != null) {
                    _inputConsumer.onKeyboardKeyEvent(key, scancode, action, mods);
                }
            }
        });

        _window.setMouseButtonCallback(new GLFWMouseButtonCallback() {
            @Override
            public void invoke(long window, int button, int action, int mods) {
                if (_inputConsumer != null) {
                    _inputConsumer.onMouseButtonEvent(button, action, mods);
                }
            }
        });

        _window.setMouseCursorMovementCallback(new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xPos, double yPos) {
                if (_inputConsumer != null) {
                    _inputConsumer.onMouseCursorMovedEvent(xPos, yPos);
                }
            }
        });

        _window.setMouseWheelCallback(new GLFWScrollCallback() {
            @Override
            public void invoke(long window, double xOffset, double yOffset) {
                if (_inputConsumer != null) {
                    _inputConsumer.onMouseWheelScrolledEvent(xOffset, yOffset);
                }
            }
        });
    }

    private long updateFrameTime(long nowMs, long prevMs) {
        _frameLengthMs = nowMs - prevMs;
        return nowMs;
    }

    private void updateFps() {
        ++_frameCount; ++_currentFrameNo;
        if (_currentTimeMs - _previousTimestampMs >= 1000) {
            _previousTimestampMs = _currentTimeMs;
            _fps = _frameCount;
            _frameCount = 0;
        }
    }

    private GlViewportConfig createDefaultViewportConfig() {
        GlViewportConfig config = new GlViewportConfig();
        config._positionX = 0;
        config._positionY = 0;
        config._width = (int)_window.getWidth();
        config._height = (int)_window.getHeight();
        config._verticalFovDegrees = 60.0;
        config._perspectiveNcp = 1.0f;
        config._perspectiveFcp = 10000.0f;
        return config;
    }
}
