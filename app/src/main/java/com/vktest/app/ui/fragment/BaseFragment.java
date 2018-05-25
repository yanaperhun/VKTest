package com.vktest.app.ui.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ArrayRes;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.vktest.app.R;
import com.vktest.app.SystemUtils;
import com.vktest.app.ui.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by qati on 29.04.15.
 */
public class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    private Unbinder unbinder;
    private Toast currentToast;

    public void bindBaseUI(View view) {
        unbinder = ButterKnife.bind(this, view);

        init();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(bindLayout(), container, false);
        bindBaseUI(v);
        return v;
    }

    public int bindLayout() {
        return 0;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroyView();
    }


    protected void init() {

    }

    protected boolean haveBackStep() {
        return getFragmentManager().getBackStackEntryCount() > 0;
    }

    /**
     * Perform pop from fragments stack
     */
    protected void backStep() {
        if (getActivity() != null) {
            getActivity().getFragmentManager().popBackStack();
        }
    }

    public void showFragment(Fragment fragment, boolean addToBack) {
        ((BaseActivity) getActivity()).showFragment(fragment, addToBack, R.id.container);
    }



    public void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
    }

    public BaseActivity getBaseActivity() {
        return ((BaseActivity) getActivity());
    }

    public void runOnUIThread(Runnable runnable) {
        if (getActivity() != null && isAdded()) {
            getActivity().runOnUiThread(runnable);
        }
    }


    public void showErrorInSnack(String error, View v) {
        if (isResumed()) {
            String e = TextUtils.isEmpty(error) ? "Something was wrong. Please try again" : error;
            SystemUtils.showSnakeBar(v, e);
        }
    }


    public void clearStack() {
        getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    public static void log(String s) {
        if (s != null) {
            Log.d(TAG, s);
        }
    }

    protected void focus(final EditText editText) {
        editText.post(new Runnable() {
            @Override
            public void run() {
                editText.requestFocus();
                editText.setSelection(editText.getText().length());
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });
    }

    public static String getStringFromET(EditText editText) {
        if (editText == null) return "";
        return editText.getText().toString();
    }

    public static boolean emptyEt(EditText editText) {
        return editText != null && TextUtils.isEmpty(editText.getText().toString());
    }

    public static boolean stringEquals(String s1, String s2) {
        return (TextUtils.isEmpty(s1) && TextUtils.isEmpty(s2)) || (s1.equals(s2));
    }

    public void hideProgress() {
        ((BaseActivity) getActivity()).hideProgressView();
    }

    public void showProgress() {
        ((BaseActivity) getActivity()).showProgressView();
    }

    public void showToast(String text) {
        if (currentToast != null) {
            currentToast.cancel();
        }
        currentToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        currentToast.show();
    }

    public Drawable getDrawable(int id) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return getResources().getDrawable(id, getActivity().getTheme());
        } else {
            return getResources().getDrawable(id);
        }
    }

    public void showPhoto(String url, ImageView iv) {
        Glide.with(this).load(url).into(iv);
    }

    public ArrayList<String> getArray(@ArrayRes int arrayId) {
        return new ArrayList<>(Arrays.asList(getResources().getStringArray(arrayId)));
    }


    public void setEnableToView(View v, boolean isEnable) {
        v.setEnabled(isEnable);
        if (isEnable) {
            v.setAlpha(1f);
        } else {
            v.setAlpha(0.5f);
        }
    }

}