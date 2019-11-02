package com.example.weather.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.weather.R;

public final class WaitDialog extends Dialog {


    public WaitDialog( @NonNull Context context) {
        super(context);
        this.setContentView(R.layout.dialog_wait);

    }

    public WaitDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected WaitDialog(@NonNull Context context, boolean cancelable,@Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    public void show() {
        super.show();

    }
}