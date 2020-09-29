package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.databinding.DataBindingUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityEditBinding;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;

public class EditAct extends BaseAct {
    ActivityEditBinding binding;
    Activity act;

    String m_pass = "";
    String m_send_pass = "";
    Matcher matcher_pw;
    Matcher matcher_name;
    public static final Pattern VALID_PASSWORD_REGEX = Pattern.compile("^(?=.*[A-Za-z])(?=.*[0-9])[A-Za-z0-9]{8,16}$"); // 영문 대소문자 + 8자리 ~ 16자리까지 가능
    public static final Pattern VALID_NAME_REGOX = Pattern.compile("^[가-힣]{2,4}$"); // 한글이름 2~4자이내

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit, null);
        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);
        act = this;

        getMyInfo();

        binding.btnBack.setOnClickListener(view -> {
            finish();
        });

        binding.btnEdit.setOnClickListener(view -> {
            binding.warningPwConfirm.setVisibility(View.INVISIBLE);
            binding.warningPw.setVisibility(View.INVISIBLE);

            matcher_pw = VALID_PASSWORD_REGEX.matcher(binding.pw.getText().toString());
            matcher_name = VALID_NAME_REGOX.matcher(binding.name.getText().toString());

            if (binding.pw.length() > 0) {
                if (!matcher_pw.matches()) {
                    binding.warningPw.setVisibility(View.VISIBLE);
                    Common.showToast(act, "비밀번호를 정확하게 입력해주세요");
                } else if (!binding.pw.getText().toString().equalsIgnoreCase(binding.pwConfirm.getText().toString())) {
                    binding.warningPwConfirm.setVisibility(View.VISIBLE);
                    Common.showToast(act, "비밀번호를 정확하게 입력해주세요");
                } else {
                    if (binding.pw.length() == 0)
                        m_send_pass = m_pass;
                    else
                        m_send_pass = binding.pw.getText().toString();

                    editInfo();
                }
            } else if (binding.name.length() == 0 || !matcher_name.matches()) {
                Common.showToast(act, "이름을 확인해주세요");
            } else if (binding.nick.length() == 0) {
                Common.showToast(act, "닉네임을 확인해주세요");
            } else {
                if (binding.pw.length() == 0 && binding.pwConfirm.length() > 0) {
                    binding.warningPw.setVisibility(View.VISIBLE);
                    binding.warningPwConfirm.setVisibility(View.VISIBLE);
                    Common.showToast(act, "비밀번호를 정확하게 입력해주세요");
                } else {
                    if (binding.pw.length() == 0)
                        m_send_pass = m_pass;
                    else
                        m_send_pass = binding.pw.getText().toString();

                    editInfo();
                }
            }
        });

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
    }

    private void getMyInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            JSONArray data = jo.getJSONArray("data");
                            JSONObject job = data.getJSONObject(0);
                            String m_id = StringUtil.getStr(job, "m_id");
                            String m_hp = StringUtil.getStr(job, "m_hp");
                            String m_name = StringUtil.getStr(job, "m_name");
                            String m_nick = StringUtil.getStr(job, "m_nick");
                            m_pass = StringUtil.getStr(job, "m_pass");

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    binding.id.setText(m_id);
                                    binding.phone.setText(m_hp);
                                    binding.name.setText(m_name);
                                    binding.nick.setText(m_nick);
                                }
                            });
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

        server.setTag("Get My Info");
        server.addParams("dbControl", NetUrls.GET_INFO);
        server.addParams("m_idx", AppPreference.getPrefString(act, AppPreference.PREF_MIDX));
        server.execute(true, false);
    }

    private void editInfo() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if (StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            AppPreference.setPrefString(act, AppPreference.PREF_NAME, binding.name.getText().toString());
                            AppPreference.setPrefString(act, AppPreference.PREF_NICK, binding.nick.getText().toString());
                            AppPreference.setPrefString(act, AppPreference.PREF_PW, m_pass);
                            Common.showToast(act, "성공적으로 수정되었습니다.");
                            finish();
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

        server.setTag("Edit Info");
        server.addParams("dbControl", NetUrls.EDIT_INFO);
        server.addParams("m_idx", AppPreference.getPrefString(act, AppPreference.PREF_MIDX));
        server.addParams("m_pass", m_send_pass);
        server.addParams("m_name", binding.name.getText().toString());
        server.addParams("m_nick", binding.nick.getText().toString());
        server.execute(true, false);
    }
}