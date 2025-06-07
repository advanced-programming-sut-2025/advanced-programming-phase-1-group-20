package org.example.views;

import org.example.models.common.Result;

public interface AppMenu {

    void updateMenu(String input);

    void handleResult(Result result, Object command);
}