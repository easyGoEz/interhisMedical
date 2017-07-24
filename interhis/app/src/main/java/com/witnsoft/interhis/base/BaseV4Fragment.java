package com.witnsoft.interhis.base;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;

/**
 * Created by zhengchengpeng on 2017/7/10.
 */

public class BaseV4Fragment extends Fragment {

    private ProgressDialog progressDialog;

    protected void showWaitingDialog() {
        BaseV4Fragment.this.showWaitingDialog(this.getResources().getString(com.witnsoft.libinterhis.R.string.please_wait), true);
    }

    protected void showWaitingDialogCannotCancel() {
        this.showWaitingDialog(this.getResources().getString(com.witnsoft.libinterhis.R.string.please_wait), false);
    }

    protected void showWaitingDialog(final String message, final boolean flag) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                BaseV4Fragment.this.dismissProgressDialog();

                try {
                    BaseV4Fragment.this.progressDialog = new ProgressDialog(getActivity());
                    BaseV4Fragment.this.progressDialog.setMessage(message);
                    BaseV4Fragment.this.progressDialog.setIndeterminate(true);
                    if (flag) {
                        BaseV4Fragment.this.progressDialog.setCanceledOnTouchOutside(true);
                        BaseV4Fragment.this.progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                                if (keyCode == 4) {
                                    BaseV4Fragment.this.dismissProgressDialog();
                                }

                                return false;
                            }
                        });
                    }

                    BaseV4Fragment.this.progressDialog.setCancelable(flag);
                    BaseV4Fragment.this.progressDialog.show();
                } catch (Exception var2) {
                    var2.printStackTrace();
                }

            }
        });
    }

    protected void hideWaitingDialog() {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                BaseV4Fragment.this.dismissProgressDialog();
            }
        });
    }

    private void dismissProgressDialog() {
        if (this.progressDialog != null) {
            try {
                this.progressDialog.dismiss();
            } catch (Exception var2) {
                var2.printStackTrace();
            }

            this.progressDialog = null;
        }

    }
}
