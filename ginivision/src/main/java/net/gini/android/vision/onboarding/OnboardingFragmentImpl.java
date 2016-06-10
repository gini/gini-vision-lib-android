package net.gini.android.vision.onboarding;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import net.gini.android.vision.R;
import net.gini.android.vision.ui.FragmentImplCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class OnboardingFragmentImpl<T extends Fragment> {

    private final FragmentImplCallback mFragment;
    private final ArrayList<OnboardingPage> mPages;
    private ListView mListOnboardingPages;

    public OnboardingFragmentImpl(FragmentImplCallback fragment) {
        mFragment = fragment;
        mPages = DefaultPages.getPages();
    }

    public OnboardingFragmentImpl(FragmentImplCallback fragment, ArrayList<OnboardingPage> pages) {
        mFragment = fragment;
        mPages = pages != null ? pages : DefaultPages.getPages();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.gv_fragment_onboarding, container, false);
        bindViews(view);
        listOnboardingPages();
        return view;
    }

    private void bindViews(View view) {
        mListOnboardingPages = (ListView) view.findViewById(R.id.gv_list_onboarding_pages);
    }

    private void listOnboardingPages() {
        Activity activity = mFragment.getActivity();
        if (activity == null) {
            return;
        }

        List<Map<String, String>> rows = new ArrayList<>();
        for (OnboardingPage page : mPages) {
            Map<String, String> columns = new HashMap<>(2);
            columns.put("text", activity.getString(page.getTextResId()));
            columns.put("imageResId", "ImageResId: " + page.getImageResId());
            rows.add(columns);
        }
        mListOnboardingPages.setAdapter(new SimpleAdapter(mFragment.getActivity(), rows, android.R.layout.simple_list_item_2,
                new String[]{"text", "imageResId"}, new int[]{android.R.id.text1, android.R.id.text2}));
    }

}
