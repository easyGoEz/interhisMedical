package com.witnsoft.interhis.tool;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.Keyboard.Key;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.witnsoft.interhis.R;
import com.witnsoft.interhis.adapter.Chinese_RecycleView_Adapter;

import java.util.List;

/**
 * Created by ${liyan} on 2017/5/18.
 */

public class KeyboardUtil {
    private Context ctx;
    private Activity act;
    private KeyboardView keyboardView;
    private Keyboard k1;//字母键盘
    private Keyboard k2;// 数字键盘
    public boolean isnun = false;// 是否数据键盘
    public boolean isupper = false;//是否大写
    private EditText ed;

    public KeyboardUtil(Activity act, Context ctx, EditText ed) {
        this.ctx = ctx;
        this.act = act;
        this.ed = ed;
        k1 = new Keyboard(ctx, R.xml.qwerty);
        k2 = new Keyboard(ctx, R.xml.symbols);
        keyboardView = (KeyboardView) act.findViewById(R.id.keyboard_view);
        keyboardView.setKeyboard(k1);
        keyboardView.setEnabled(true);
        keyboardView.setPreviewEnabled(true);
        keyboardView.setOnKeyboardActionListener(listener);

    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {

        }

        @Override
        public void onRelease(int primaryCode) {

        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Intent intent=new Intent("RUANJIANPAN");
            ctx.sendBroadcast(intent);
            Editable editable = ed.getText();
            int start = ed.getSelectionStart();
            if (primaryCode == Keyboard.KEYCODE_DELETE) {//删除
                if (editable != null && editable.length() > 0) {
                    if (start > 0) {
                        editable.delete(start - 1, start);
                    }
                }
            } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {//切换数字键盘
                if (isnun) {
                    isnun = false;
                    keyboardView.setKeyboard(k1);
                } else {
                    isnun = true;
                    keyboardView.setKeyboard(k2);
                }
            } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                changeKey();
                keyboardView.setKeyboard(k1);
            } else if (primaryCode==Keyboard.KEYCODE_CANCEL){
//                initData();
            }else {
                editable.insert(start, Character.toString((char) primaryCode));
            }
        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    public void showKeyboard() {
        int visibility = keyboardView.getVisibility();
        if (visibility == View.GONE || visibility == View.INVISIBLE) {
            keyboardView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 键盘大小写切换
     */
    private void changeKey() {
        List<Key> keylist = k1.getKeys();
        if (isupper) {//大写切换小写
            isupper = false;
            for (Keyboard.Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {//小写切换大写
            isupper = true;
            for (Key key : keylist) {
                if (key.label != null && isword(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }

//    private void initData() {
//        chinese_adapter = new Chinese_RecycleView_Adapter(ctx);
//        String content = ed.getText().toString();
//        if (!TextUtils.isEmpty(content)) {
//            chinese_adapter.addTextView(content);
//            chinese_adapter.notifyDataSetChanged();
//            ed.setText("");
//        }
//    }
}
