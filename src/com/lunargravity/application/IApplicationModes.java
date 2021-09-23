package com.lunargravity.application;

import com.lunargravity.engine.scene.ISceneBuilderObserver;

import java.io.IOException;

public interface IApplicationModes {
    void startMenu(ISceneBuilderObserver sceneBuilderObserver) throws IOException, InterruptedException;
    void startCampaignGame(ISceneBuilderObserver sceneBuilderObserver, String fileName) throws IOException, InterruptedException;
    void startCampaignGame(ISceneBuilderObserver sceneBuilderObserver, int numPlayers) throws IOException, InterruptedException;
    void startRaceGame(ISceneBuilderObserver sceneBuilderObserver, int numPlayers) throws IOException, InterruptedException;
    void startDogfightGame(ISceneBuilderObserver sceneBuilderObserver, int numPlayers) throws IOException, InterruptedException;
}
