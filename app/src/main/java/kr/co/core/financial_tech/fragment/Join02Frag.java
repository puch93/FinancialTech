package kr.co.core.financial_tech.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.activity.JoinAct;
import kr.co.core.financial_tech.databinding.FragmentJoin02Binding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StringUtil;

public class Join02Frag extends BaseFrag implements View.OnClickListener {
    FragmentJoin02Binding binding;
    Activity act;

    String checked_id = "";
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,16}$"); // 영문 대소문자 + 8자리 ~ 16자리까지 가능
    public static final Pattern VALID_NAME_REGOX = Pattern.compile("^[가-힣]{2,4}$"); // 한글이름 2~4자이내

    Matcher matcher_id;
    Matcher matcher_pw;
    Matcher matcher_name;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_join_02, container, false);
        act = getActivity();

        binding.btnDoubleCheck.setOnClickListener(this);
        binding.btnJoin.setOnClickListener(this);

        binding.pw.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.warningPw.setVisibility(View.INVISIBLE);
            }
        });

        binding.pwConfirm.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                binding.warningPwConfirm.setVisibility(View.INVISIBLE);
            }
        });
        return binding.getRoot();
    }

    private void check_id(String id) {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            checked_id = id;
                            Common.showToast(act, "사용가능한 이메일 입니다");
                        } else {
                            checked_id = "";
                            Common.showToast(act, "이미 사용중인 이메일 입니다");
                        }
//                        Common.showToast(act, StringUtil.getStr(jo, "message"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Common.showToastNetwork(act);
                    }
                } else {
                    Common.showToastNetwork(act);
                }
            }
        };

        server.setTag("Double Check Id");
        server.addParams("dbControl", NetUrls.JOIN);
        server.addParams("m_id", id);
        server.addParams("jointype", "findid");
        server.execute(true, false);
    }

    private void nextProcess() {
        BaseFrag fragment = new Join03Frag();
        ((JoinAct) act).replaceFragment(fragment, 3);
    }

    private void doJoin() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            AppPreference.setPrefString(act, AppPreference.PREF_ID, binding.id.getText().toString());
                            AppPreference.setPrefString(act, AppPreference.PREF_PW, binding.pw.getText().toString());
                            nextProcess();
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

        server.setTag("Join");
        server.addParams("dbControl", NetUrls.JOIN);
        server.addParams("m_id", binding.id.getText().toString());
        server.addParams("m_pass", binding.pw.getText().toString());
        server.addParams("m_name", binding.name.getText().toString());
        server.addParams("m_nick", binding.nick.getText().toString());
        server.addParams("m_hp", binding.phone.getText().toString());
        server.addParams("m_uniq", Common.getDeviceId(act));
        server.addParams("fcm", AppPreference.getPrefString(act, AppPreference.PREF_FCM));
        server.addParams("jointype", "join");
        server.execute(true, false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_double_check:
                matcher_id = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.id.getText().toString());
                matcher_pw = VALID_PASSWORD_REGEX.matcher(binding.pw.getText().toString());
                matcher_name = VALID_NAME_REGOX.matcher(binding.name.getText().toString());

                if (binding.id.length() == 0 || !matcher_id.matches()) {
                    Common.showToast(act, "이메일을 확인해주세요");
                } else {
                    check_id(binding.id.getText().toString());
                }
                break;

            case R.id.btn_join:
                binding.warningPwConfirm.setVisibility(View.INVISIBLE);
                binding.warningPw.setVisibility(View.INVISIBLE);

                matcher_id = VALID_EMAIL_ADDRESS_REGEX.matcher(binding.id.getText().toString());
                matcher_pw = VALID_PASSWORD_REGEX.matcher(binding.pw.getText().toString());
                matcher_name = VALID_NAME_REGOX.matcher(binding.name.getText().toString());

                if (binding.id.length() == 0 || !matcher_id.matches()) {
                    Common.showToast(act, "이메일을 확인해주세요");
                } else if (StringUtil.isNull(checked_id) || !checked_id.equalsIgnoreCase(binding.id.getText().toString())) {
                    Common.showToast(act, "이메일 중복확인을 진행해주세요");
                } else if (binding.pw.length() == 0 || !matcher_pw.matches()) {
                    binding.warningPw.setVisibility(View.VISIBLE);
                    Common.showToast(act, "비밀번호를 확인해주세요");
                } else if (binding.pwConfirm.length() == 0 || !binding.pwConfirm.getText().toString().equalsIgnoreCase(binding.pw.getText().toString())) {
                    binding.warningPwConfirm.setVisibility(View.VISIBLE);
                    Common.showToast(act, "비밀번호를 정확하게 입력해주세요");
                } else if (binding.name.length() == 0 || !matcher_name.matches()) {
                    Common.showToast(act, "이름을 확인해주세요");
                } else if (binding.nick.length() == 0) {
                    Common.showToast(act, "닉네임을 확인해주세요");
                } else if (binding.phone.length() == 0 || !Common.checkCellnum(binding.phone.getText().toString())) {
                    Common.showToast(act, "전화번호를 확인해주세요");
                } else {
                    doJoin();
                }
                break;
        }
    }
}
