package kr.co.core.financial_tech.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import kr.co.core.financial_tech.R;
import kr.co.core.financial_tech.databinding.ActivityBoardWriteBinding;
import kr.co.core.financial_tech.dialog.GalleryDlg;
import kr.co.core.financial_tech.server.ReqBasic;
import kr.co.core.financial_tech.server.netUtil.HttpResult;
import kr.co.core.financial_tech.server.netUtil.NetUrls;
import kr.co.core.financial_tech.util.AppPreference;
import kr.co.core.financial_tech.util.Common;
import kr.co.core.financial_tech.util.CustomSpinner;
import kr.co.core.financial_tech.util.StatusBarUtil;
import kr.co.core.financial_tech.util.StringUtil;
import kr.co.core.financial_tech.util.WriteSpinnerAdapter;

public class BoardWriteAct extends BaseAct implements View.OnClickListener {
    ActivityBoardWriteBinding binding;
    Activity act;

    int currentPosition = -1;
    private Uri photoUri;
    private String mImgFilePath;
    ArrayList<File> image_list = new ArrayList<>();
    ArrayList<TextView> textViews = new ArrayList<>();

    private static final int PICK_DIALOG = 1000;
    private static final int PHOTO_GALLERY = 1001;
    private static final int PHOTO_TAKE = 1002;
    private static final int PHOTO_CROP = 1003;

    private String[] board_list;
    private String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_write, null);
        act = this;


        StatusBarUtil.setStatusBarColor(this, StatusBarUtil.StatusBarColorType.WHITE_STATUS_BAR);

        board_list = getResources().getStringArray(R.array.board_list);

        textViews.add(binding.fileText01);
        textViews.add(binding.fileText02);
        textViews.add(binding.fileText03);
        textViews.add(binding.fileText04);
        textViews.add(binding.fileText05);

        binding.btnFileSelect01.setOnClickListener(this);
        binding.btnFileSelect02.setOnClickListener(this);
        binding.btnFileSelect03.setOnClickListener(this);
        binding.btnFileSelect04.setOnClickListener(this);
        binding.btnFileSelect05.setOnClickListener(this);

        binding.btnFileSelect01.setTag(0);
        binding.btnFileSelect02.setTag(1);
        binding.btnFileSelect03.setTag(2);
        binding.btnFileSelect04.setTag(3);
        binding.btnFileSelect05.setTag(4);

        binding.btnBack.setOnClickListener(this);
        binding.btnCancel.setOnClickListener(this);
        binding.btnWrite.setOnClickListener(this);

        for (int i = 0; i < 5; i++) {
            image_list.add(null);
        }

        setSpinner();

        binding.spinner.setSelection(getSelectedPosition(getIntent().getStringExtra("cate")));
    }


    private int getSelectedPosition(String cate) {
        switch (cate) {
            case "wise":
                return 1;
            case "humor":
                return 2;

            case "case":
            default:
                return 0;
        }
    }

    private void setSpinner() {
        // set spinner
        WriteSpinnerAdapter adapter_area = new WriteSpinnerAdapter(act, android.R.layout.simple_spinner_item, board_list);
        adapter_area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_height_set(binding.spinner, 1000);
        binding.spinner.setAdapter(adapter_area);

        // set spinner open/close listener
        binding.spinner.setSpinnerEventsListener(new CustomSpinner.OnSpinnerEventsListener() {
            @Override
            public void onSpinnerOpened(Spinner spinner) {
                // 현재 focus 되어있는 view 가 있으면 키보드를 내리고, focus 제거
                View view = act.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    view.clearFocus();
                } else {
                    view = binding.title;
                    InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }

                binding.arrow.setSelected(true);
            }

            @Override
            public void onSpinnerClosed(Spinner spinner) {
                binding.arrow.setSelected(false);
            }
        });
    }

    //Spinner 길이설정
    private void spinner_height_set(Spinner spinner, int height) {
        try {
            Field popup = Spinner.class.getDeclaredField("mPopup");
            popup.setAccessible(true);

            // Get private mPopup member variable and try cast to ListPopupWindow
            android.widget.ListPopupWindow popupWindow = (android.widget.ListPopupWindow) popup.get(spinner);

            // Set popupWindow height to 500px
            popupWindow.setHeight(height);
        } catch (NoClassDefFoundError | ClassCastException | NoSuchFieldException | IllegalAccessException e) {
            // silently fail...
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_file_select_01:
            case R.id.btn_file_select_02:
            case R.id.btn_file_select_03:
            case R.id.btn_file_select_04:
            case R.id.btn_file_select_05:
                currentPosition = (int) view.getTag();
                startActivityForResult(new Intent(act, GalleryDlg.class), PICK_DIALOG);
                break;

            case R.id.btn_back:
            case R.id.btn_cancel:
                finish();
                break;

            case R.id.btn_write:
                if(binding.title.length() == 0) {
                    Common.showToast(act, "제목을 입력해주세요");
                } else if(binding.contents.length() == 0) {
                    Common.showToast(act, "내용을 입력해주세요");
                } else {
                    doWrite();
                }
                break;
        }
    }

    private String getSelection() {
        switch ((String) binding.spinner.getSelectedItem()) {
            case "사례":
                return "4";
            case "명언":
                return "5";
            case "유머":
                return "6";
            default:
                return null;
        }
    }

    private void doWrite() {
        ReqBasic server = new ReqBasic(act, NetUrls.DOMAIN) {
            @Override
            public void onAfter(int resultCode, HttpResult resultData) {
                if (resultData.getResult() != null) {
                    try {
                        JSONObject jo = new JSONObject(resultData.getResult());

                        if( StringUtil.getStr(jo, "result").equalsIgnoreCase("Y")) {
                            Common.showToast(act, "정상적으로 등록되었습니다");
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

        server.setTag("Write");
        server.addParams("dbControl", NetUrls.BOARD_WRITE);
        server.addParams("BCODE", getSelection());
        server.addParams("m_idx", AppPreference.getPrefString(act, AppPreference.PREF_MIDX));
        server.addParams("b_title", binding.title.getText().toString());
        server.addParams("b_contents", binding.contents.getText().toString());

        for (int i = 0; i < 5; i++) {
            if(image_list.get(i) != null) {
                server.addFileParams("bf_file_" + (i+1), image_list.get(i));
            }
        }

        server.execute(true, false);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case PICK_DIALOG:
                    String type = data.getStringExtra("result");

                    if (type.equalsIgnoreCase("camera")) {
                        //촬영하기
                        Common.takePhoto(null, act, PHOTO_TAKE, new Common.OnPhotoAfterAction() {
                            @Override
                            public void doPhotoUri(Uri uri) {
                                photoUri = uri;
                            }
                        });
                    } else if (type.equalsIgnoreCase("gallery")) {
                        //갤러리
                        Common.getAlbum(null, act, PHOTO_GALLERY);
                    }
                    break;

                //사진 갤러리 결과
                case PHOTO_GALLERY:
                    if (data == null) {
                        Common.showToast(act, "사진불러오기 실패! 다시 시도해주세요.");
                        return;
                    }

                    photoUri = data.getData();

                    Common.cropImage(null, act, photoUri, PHOTO_CROP, new Common.OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 촬영 결과
                case PHOTO_TAKE:
                    Common.cropImage(null, act, photoUri, PHOTO_CROP, new Common.OnPhotoAfterAction() {
                        @Override
                        public void doPhotoUri(Uri uri) {
                            photoUri = uri;
                        }
                    });
                    break;

                //사진 크롭 결과
                case PHOTO_CROP:
                    mImgFilePath = photoUri.getPath();

                    Log.i(StringUtil.TAG, "mImgFilePath: " + mImgFilePath);
                    if (StringUtil.isNull(mImgFilePath)) {
                        Common.showToast(act, "사진자르기 실패! 다시 시도해주세요.");
                        return;
                    }

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 2;
                    Bitmap bm = BitmapFactory.decodeFile(mImgFilePath, options);

                    Bitmap resize = null;
                    try {
                        File resize_file = new File(mImgFilePath);
                        FileOutputStream out = new FileOutputStream(resize_file);

                        int width = bm.getWidth();
                        int height = bm.getHeight();

                        if (width > 1024) {
                            int resizeHeight = 0;
                            if (height > 768) {
                                resizeHeight = 768;
                            } else {
                                resizeHeight = height / (width / 1024);
                            }

                            resize = Bitmap.createScaledBitmap(bm, 1024, resizeHeight, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } else {
                            resize = Bitmap.createScaledBitmap(bm, width, height, true);
                            resize.compress(Bitmap.CompressFormat.PNG, 100, out);
                        }
                        Log.e("TEST_HOME", "mImgFilePath: " + mImgFilePath);


                        File imageFile = new File(mImgFilePath);
                        if (imageFile.length() > 10000000) {
                            Common.showToast(act, "파일 용량이 초과되었습니다. 다른사진을 선택해주세요");
                            mImgFilePath = "";
                            if(currentPosition >= 0) {
                                Log.i(StringUtil.TAG, "currentPosition 111: ");
                                textViews.get(currentPosition).setVisibility(View.GONE);
                                textViews.get(currentPosition).setText("");
                            }
                        } else {
                            // 사진 추가
                            if(currentPosition >= 0) {
                                Log.i(StringUtil.TAG, "currentPosition 222: ");
                                image_list.set(currentPosition, imageFile);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        textViews.get(currentPosition).setVisibility(View.VISIBLE);
                                        textViews.get(currentPosition).setText(imageFile.getName());
                                    }
                                });
                            }
                        }

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    MediaScannerConnection.scanFile(act, new String[]{photoUri.getPath()}, null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                    break;
            }
        }
    }
}