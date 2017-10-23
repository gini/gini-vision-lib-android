package net.gini.android.vision.help;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import net.gini.android.vision.R;

public class HelpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gv_activity_help);
        setUpHelpItems();
    }

    private void setUpHelpItems() {
        final RecyclerView recyclerView = findViewById(R.id.gv_help_items);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new HelpItemsAdapter(
                new HelpItemsAdapter.HelpItemSelectedListener() {
                    @Override
                    public void onItemSelected(@NonNull final HelpItemsAdapter.HelpItem helpItem) {
                        launchHelpScreen(helpItem);
                    }
                }));
    }

    private void launchHelpScreen(final HelpItemsAdapter.HelpItem helpItem) {
        switch (helpItem) {
            case PHOTO_TIPS:
                break;
            case OPEN_WITH_GUIDE:
                break;
            case SUPPORTED_FORMATS:
                break;
        }
    }
}
