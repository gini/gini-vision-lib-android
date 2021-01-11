package net.gini.android.vision.component;

import net.gini.android.vision.example.shared.BaseExampleApp;

/**
 * Created by Alpar Szotyori on 01.12.2017.
 *
 * Copyright (c) 2017 Gini GmbH.
 */

public class ComponentApiExampleApp extends BaseExampleApp {

    @Override
    protected String getClientId() {
        return getString(R.string.gini_api_client_id);
    }

    @Override
    protected String getClientSecret() {
        return getString(R.string.gini_api_client_secret);
    }
}
