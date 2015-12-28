package com.learnit.learnit.interfaces;

public interface IFabStateController {
    void showFab(int drawableId);
    void hideFab();
    void addFabEventHandler(int position, IFabEventHandler handler);
}
