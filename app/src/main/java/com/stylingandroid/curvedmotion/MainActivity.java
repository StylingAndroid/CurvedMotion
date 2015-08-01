package com.stylingandroid.curvedmotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private SceneAnimator sceneAnimator = null;
    private FrameLayout container;
    private Context themedContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        container = (FrameLayout) findViewById(R.id.container);

        setupToolbar();
        if (!setupModeSelection()) {
            setLegacyAnimator();
        }
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            themedContext = actionBar.getThemedContext();
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setLollipopAnimator() {
        if (!(sceneAnimator instanceof LollipopSceneAnimator)) {
            container.removeAllViews();
            sceneAnimator = LollipopSceneAnimator.newInstance(this, container, R.layout.scene1, R.layout.scene2, R.transition.arc1);
        }
    }

    private void setLegacyAnimator() {
        if (!(sceneAnimator instanceof LegacySceneAnimator)) {
            container.removeAllViews();
            sceneAnimator = LegacySceneAnimator.newInstance(this, container, R.layout.scene1, R.id.view);
        }
    }

    private boolean setupModeSelection() {
        Spinner spinner = (Spinner) findViewById(R.id.mode);
        if (spinner == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false;
        }
        String[] values = getResources().getStringArray(R.array.modes);
        SpinnerAdapter adapter = new ArrayAdapter<>(themedContext, android.R.layout.simple_spinner_dropdown_item, values);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(0);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0) {
            setLollipopAnimator();
        } else {
            setLegacyAnimator();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
