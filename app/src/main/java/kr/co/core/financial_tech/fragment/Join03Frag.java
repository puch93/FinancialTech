package kr.co.core.financial_tech.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.LoginAct;
import kr.co.core.financial_tech.activity.MainAct;
import kr.co.core.financial_tech.databinding.FragmentJoin03Binding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class Join03Frag extends BaseFrag  {
    FragmentJoin03Binding binding;
    Activity act;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_03, container,false);
        act = getActivity();

        binding.btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });


        return binding.getRoot();
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
                            AppPreference.setPrefString(act, AppPreference.PREF_ID, StringUtil.getStr(job, "m_id"));
                            AppPreference.setPrefBoolean(act, AppPreference.LOGIN_STATE, true);
                            Intent intent = new Intent(act, MainAct.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                            if(LoginAct.act != null)
                                LoginAct.act.finish();
                            act.finish();
                        } else {
                            Common.showToast(act, StringUtil.getStr(jo, "message"));
                        }

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
        server.addParams("m_id", AppPreference.getPrefString(act, AppPreference.PREF_ID));
        server.addParams("m_pass", AppPreference.getPrefString(act, AppPreference.PREF_PW));
        server.addParams("fcm", AppPreference.getPrefString(act, AppPreference.PREF_FCM));
        server.addParams("m_uniq", Common.getDeviceId(act));
        server.execute(true, false);
    }
}
