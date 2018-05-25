package com.vktest.app.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiUser;
import com.vk.sdk.api.model.VKList;
import com.vktest.app.R;
import com.vktest.app.SystemUtils;
import com.vktest.app.ui.Prefs;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by qati on 25.05.18.
 */

public class VkFragment extends BaseFragment {

    @BindView(R.id.btnAuth) TextView btnLogout;
    @BindView(R.id.tvProfileName) TextView tvProfileName;
    @BindView(R.id.tvEmail) TextView tvEmail;
    @BindView(R.id.ivPhoto) ImageView ivPhoto;


    @Override
    public int bindLayout() {
        return R.layout.fragment_vk;
    }

    @Override
    protected void init() {
        super.init();
        if (Prefs.userWasLogined(getActivity())) {
            showVkData(Prefs.getFullName(getActivity()), Prefs.getEmail(getActivity()),
                    Prefs.getPhotoUrl(getActivity()));

        } else {
            //force login
            //login();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkLoginButtonText();
    }

    @OnClick(R.id.btnAuth)
    public void btnExitClick(View v) {
        if (VKSdk.isLoggedIn()) {
            logout();
        } else {
            login();
        }
        checkLoginButtonText();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("VK onActivityResult");
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                log("vk auth: " + res.accessToken);
                log("vk auth: " + res.userId);
                // Пользователь успешно авторизовался
                vkRequest(res.email);
                checkLoginButtonText();
            }

            @Override
            public void onError(VKError error) {
                log("VK onError: " + error.errorMessage);
                // Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }

    }

    public void vkRequest(String email) {
        VKRequest request = VKApi.users().get(VKParameters.from(VKApiConst.FIELDS, "photo_200", "first_name", "last_name"));
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                VKApiUser user = ((VKList<VKApiUser>) response.parsedModel).get(0);
                String nameFormat = "%s %s";
                String name = String.format(nameFormat, user.first_name, user.last_name);
                Prefs.saveUser(getActivity(), email, name, user.photo_200);
                showVkData(name, email, user.photo_200);
                checkLoginButtonText();
            }

            @Override
            public void onError(VKError error) {
                SystemUtils.showToast(getActivity(), "VK Profile Error: " + error.errorReason);
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                SystemUtils.showToast(getActivity(), "VK attempt failed: ");
            }
        });
    }

    private void showVkData(String name, String email, String photo_200) {
        tvProfileName.setText(name);
        tvEmail.setText(email);
        showPhoto(photo_200, ivPhoto);
    }

    public void logout() {
        VKSdk.logout();
        ivPhoto.setImageDrawable(getDrawable(R.drawable.ic_settings_user));
        tvEmail.setText("Null Null");
        tvProfileName.setText("null");
    }

    private void login() {
        VKSdk.login(this, VKScope.EMAIL);
    }

    private void checkLoginButtonText() {
        if (VKSdk.isLoggedIn()) {
            btnLogout.setText(R.string.logout);
        } else {
            btnLogout.setText(R.string.enter);
        }
    }
}


