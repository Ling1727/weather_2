package com.example.weather.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class HintDialog extends Dialog {
    public HintDialog( @NonNull Context context) {
        super(context);
        init();
    }

    public HintDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init();
    }

    protected HintDialog(@NonNull Context context, boolean cancelable,  @Nullable DialogInterface.OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
    }
}
