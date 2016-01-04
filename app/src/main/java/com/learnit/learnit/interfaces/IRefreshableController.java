package com.learnit.learnit.interfaces;

public interface IRefreshableController {
    void refreshAllClients();
    void addRefreshableClient(int position, IRefreshable refreshable);
}
