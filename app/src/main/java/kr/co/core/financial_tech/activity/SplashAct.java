package kr.co.core.financial_tech.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivitySplashBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class SplashAct extends BaseAct {
    ActivitySplashBinding binding;
    Activity act;

    private Timer timer = new Timer();
    private String fcm_token, device_version;
    boolean isReady = false;

    private static final int PERMISSION = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash, null);
        act = this;

        Glide.with(act)
                .load(R.raw.splash_1440x2560)
                .into(binding.splash);

        // get device version
        try {
            device_version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // get fcm token
        getFcmToken();

        checkTimer();

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkVersion();
            }
        }, 1500);
    }

    private void checkVersion() {
        ReqBasic server = new ReqBasic(this, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                final String res = resultData.getResult();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (!StringUtil.isNull(res)) {
                                JSONObject jo = new JSONObject(res);

                                if(StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                                    String[] version = StringUtil.getStr(jo, "MEMCODE").split("\\.");
                                    String[] version_me = device_version.split("\\.");

                                    for (int i = 0; i < 3; i++) {
                                        int tmp1 = Integer.parseInt(version[i]);
                                        int tmp2 = Integer.parseInt(version_me[i]);

                                        if (tmp2 < tmp1) {
                                            android.app.AlertDialog.Builder alertDialogBuilder =
                                                    new android.app.AlertDialog.Builder(new ContextThemeWrapper(act, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert));
                                            alertDialogBuilder.setTitle("업데이트");
                                            alertDialogBuilder.setMessage("새로운 버전이 있습니다.")
                                                    .setPositiveButton("업데이트 바로가기", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            Intent intent = new Intent(Intent.ACTION_VIEW);
                                                            intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=kr.co.core.financial_tech"));
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                            android.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                            alertDialog.setCanceledOnTouchOutside(false);
                                            alertDialog.show();

                                            return;
                                        }
                                    }

                                    startProgram();
                                } else {
                                    startProgram();
                                }

                            } else {
                                startProgram();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };
        server.setTag("Version Check");
        server.addParams("dbControl", NetUrls.VERSION);
        server.addParams("thisVer", device_version);
        server.execute(true, false);
    }

    private void startProgram() {
        if (!checkPermission()) {
            startActivityForResult(new Intent(act, PermissionAct.class), PERMISSION);
        } else {
            isReady = true;
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (
                    checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
            ) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(StringUtil.TAG, "resultCode: " + resultCode);

        if (resultCode != RESULT_OK && resultCode != RESULT_CANCELED)
            return;

        switch (requestCode) {
            case PERMISSION:
                if (resultCode == RESULT_CANCELED) {
                    finish();
                } else {
                    startProgram();
                }
                break;
        }
    }

    private void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d(StringUtil.TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        fcm_token = task.getResult().getToken();
                        AppPreference.setPrefString(act, AppPreference.PREF_FCM, fcm_token);
                        Log.i(StringUtil.TAG, "fcm_token: " + fcm_token);
                    }
                });
    }

    //로딩중 텍스트 애니메이션
    public void checkTimer() {
        TimerTask adTask = new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isReady && !StringUtil.isNull(fcm_token)) {
                            isReady = false;
                            if(!AppPreference.getPrefBoolean(act, AppPreference.AUTO_LOGIN)) {
                                AppPreference.setPrefBoolean(act, AppPreference.LOGIN_STATE, false);

                                Intent intent = new Intent(act, MainAct.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            } else {
                                doLogin();
                            }
                            timer.cancel();
                        }
                    }
                }, 0);
            }
        };
        timer.schedule(adTask, 0, 1000);
    }

    private void doLogin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONObject job = jo.getJSONObject("data");
                            AppPreference.setPrefString(act, AppPreference.PREF_MIDX, StringUtil.getStr(job, "m_idx"));
                            AppPreference.setPrefString(act, AppPreference.PREF_NICK, StringUtil.getStr(job, "m_nick"));
                            AppPreference.setPrefBoolean(act, AppPreference.LOGIN_STATE, true);
                            Intent intent = new Intent(act, MainAct.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                            AppPreference.setPrefBoolean(act, AppPreference.AUTO_LOGIN, false);
                            Intent intent = new Intent(act, MainAct.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Login");
        server.addParams("dbControl", NetUrls.LOGIN);
        server.addParams("m_uniq", Common.getDeviceId(act));
        server.addParams("fcm", fcm_token);
        server.addParams("m_id", AppPreference.getPrefString(act, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getPrefString(act, AppPreference.PREF_PW));
        server.execute(true, false);
    }
}