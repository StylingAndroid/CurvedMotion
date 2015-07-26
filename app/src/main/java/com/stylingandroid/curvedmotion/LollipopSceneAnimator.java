package com.stylingandroid.curvedmotion;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.TransitionRes;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
final class LollipopSceneAnimator implements SceneAnimator {
    private final TransitionManager transitionManager;
    private Scene scene1;
    private Scene scene2;

    public static LollipopSceneAnimator newInstance(@NonNull Context context, @NonNull ViewGroup container,
                                            @LayoutRes int layout1Id, @LayoutRes int layout2Id, @TransitionRes int transitionId) {
        TransitionManager transitionManager = new TransitionManager();
        LollipopSceneAnimator sceneAnimator = new LollipopSceneAnimator(transitionManager);
        Scene scene1 = createScene(sceneAnimator, context, container, layout1Id);
        Scene scene2 = createScene(sceneAnimator, context, container, layout2Id);
        Transition transition = TransitionInflater.from(context).inflateTransition(transitionId);
        transitionManager.setTransition(scene1, scene2, transition);
        transitionManager.setTransition(scene2, scene1, transition);
        transitionManager.transitionTo(scene1);
        sceneAnimator.scene1 = scene1;
        sceneAnimator.scene2 = scene2;
        return sceneAnimator;
    }

    private static Scene createScene(@NonNull LollipopSceneAnimator sceneAnimator, @NonNull Context context,
                                     @NonNull ViewGroup container, @LayoutRes int layoutId) {
        Scene scene = Scene.getSceneForLayout(container, layoutId, context);
        scene.setEnterAction(new EnterAction(sceneAnimator, scene));
        return scene;
    }

    private LollipopSceneAnimator(TransitionManager transitionManager) {
        this.transitionManager = transitionManager;
    }

    private void sceneTransition(Scene from) {
        if (from == scene1) {
            transitionManager.transitionTo(scene2);
        } else {
            transitionManager.transitionTo(scene1);
        }
    }

    private static final class EnterAction implements Runnable, View.OnClickListener {
        private final LollipopSceneAnimator sceneAnimator;
        private final Scene scene;

        private EnterAction(@NonNull LollipopSceneAnimator sceneAnimator, @NonNull Scene scene) {
            this.sceneAnimator = sceneAnimator;
            this.scene = scene;
        }

        @Override
        public void run() {
            ViewGroup sceneRoot = scene.getSceneRoot();
            View view = sceneRoot.findViewById(R.id.view);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            sceneAnimator.sceneTransition(scene);
        }
    }
}
