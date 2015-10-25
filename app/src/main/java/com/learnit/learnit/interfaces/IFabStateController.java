package com.learnit.learnit.interfaces;

public interface IFabStateController {
    void showFab();
    void hideFab();
    void addFabEventHandler(int position, IFabEventHandler handler);
}
