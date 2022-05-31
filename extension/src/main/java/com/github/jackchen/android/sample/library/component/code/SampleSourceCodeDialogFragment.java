package com.github.jackchen.android.sample.library.component.code;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.jackchen.android.sample.library.R;
import com.github.jackchen.android.sample.library.component.code.view.SourceCodeView;
import com.github.jackchen.android.sample.library.view.WebViewProgressBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author Created by cz
 * @date 2020-01-28 19:48
 * @email bingo110@126.com
 */
public class SampleSourceCodeDialogFragment extends BottomSheetDialogFragment {
    private final static String SAMPLE_FILE_PATH = "filePath";
    private BottomSheetBehavior bottomSheetBehavior;

    public static BottomSheetDialogFragment newInstance(String filePath) {
        Bundle argument = new Bundle();
        argument.putString(SAMPLE_FILE_PATH, filePath);
        BottomSheetDialogFragment fragment = new SampleSourceCodeDialogFragment();
        fragment.setArguments(argument);
        return fragment;
    }

    private SampleSourceCodeDialogFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.sample_fragment_source_code, container, false);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.height = Resources.getSystem().getDisplayMetrics().heightPixels;
        contentView.setLayoutParams(layoutParams);
        return contentView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();
        String filePath = arguments.getString(SAMPLE_FILE_PATH);
        SourceCodeView sampleSourceCodeView = view.findViewById(R.id.sampleSourceCodeView);
        sampleSourceCodeView.loadSourceCodeFromUrl(filePath);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View view = getView();
        final SourceCodeView sampleSourceCodeView = view.findViewById(R.id.sampleSourceCodeView);
        final WebViewProgressBar sampleProgressBar = view.findViewById(R.id.sampleProgressBar);
        sampleProgressBar.startProgressAnim();
        sampleProgressBar.setOnProgressListener(v -> v.animate().alpha(0f));
        sampleSourceCodeView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (null != sampleProgressBar && newProgress >= sampleProgressBar.getFirstProgress()) {
                    sampleProgressBar.passAnimation();
                }
            }
        });

        bottomSheetBehavior = BottomSheetBehavior.from((View) (view.getParent()));
        bottomSheetBehavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (BottomSheetBehavior.STATE_EXPANDED == i) {
                    sampleSourceCodeView.setNestedScrollingEnabled(false);
                }
                if (BottomSheetBehavior.STATE_COLLAPSED == i) {
                }
                if (BottomSheetBehavior.STATE_HIDDEN == i) {
                    dismiss();
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }
}
