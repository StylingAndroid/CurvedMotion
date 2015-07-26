package com.stylingandroid.curvedmotion;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

final class LegacySceneAnimator implements SceneAnimator, View.OnClickListener, ViewTreeObserver.OnPreDrawListener {
    private static final float INTERPOLATOR_FACTOR = .75f;
    private static final String TRANSLATION_Y = "translationY";
    private static final String TRANSLATION_X = "translationX";

    private final FrameLayout parent;
    private final View view;
    private final int animationDuration;

    private float currentX;
    private float currentY;

    public static LegacySceneAnimator newInstance(@NonNull Context context, @NonNull ViewGroup container,
                                                  @LayoutRes int layoutId, @IdRes int viewId) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        FrameLayout root = (FrameLayout) layoutInflater.inflate(layoutId, container, false);
        container.addView(root);
        View view = root.findViewById(viewId);
        int animationDuration = context.getResources().getInteger(android.R.integer.config_longAnimTime);
        LegacySceneAnimator sceneAnimator = new LegacySceneAnimator(root, view, animationDuration);
        view.setOnClickListener(sceneAnimator);
        return sceneAnimator;
    }

    private LegacySceneAnimator(FrameLayout parent, View view, int animationDuration) {
        this.parent = parent;
        this.view = view;
        this.animationDuration = animationDuration;
    }

    @Override
    public void onClick(View v) {
        ViewTreeObserver viewTreeObserver = parent.getViewTreeObserver();
        viewTreeObserver.addOnPreDrawListener(this);
        currentX = v.getX();
        currentY = v.getY();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
        if (isTopAligned(layoutParams)) {
            layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
        } else {
            layoutParams.gravity = Gravity.TOP | Gravity.START;
        }
        view.setLayoutParams(layoutParams);
    }

    private boolean isTopAligned(FrameLayout.LayoutParams layoutParams) {
        return (layoutParams.gravity & Gravity.TOP) == Gravity.TOP;
    }

    @Override
    public boolean onPreDraw() {
        ViewTreeObserver viewTreeObserver = parent.getViewTreeObserver();
        viewTreeObserver.removeOnPreDrawListener(this);
        getAnimator().start();
        return true;
    }

    private Animator getAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        Animator xAnimator = getTranslationXAnimator(new DecelerateInterpolator(INTERPOLATOR_FACTOR));
        Animator yAnimator = getTranslationYAnimator(new AccelerateInterpolator(INTERPOLATOR_FACTOR));
        animatorSet.playTogether(xAnimator, yAnimator);
        animatorSet.setDuration(animationDuration);
        return animatorSet;
    }

    private Animator getTranslationXAnimator(Interpolator interpolator) {
        float newX = view.getX();
        Animator animator = ObjectAnimator.ofFloat(view, TRANSLATION_X, currentX - newX, 0);
        animator.setInterpolator(interpolator);
        return animator;
    }

    private Animator getTranslationYAnimator(Interpolator interpolator) {
        float newY = view.getY();
        Animator animator = ObjectAnimator.ofFloat(view, TRANSLATION_Y, currentY - newY, 0);
        animator.setInterpolator(interpolator);
        return animator;
    }

}
