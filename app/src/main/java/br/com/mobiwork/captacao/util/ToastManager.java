package br.com.mobiwork.captacao.util;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.mobiwork.captacao.R;

/**
 * Created by LuisGustavo on 01/06/2015.
 */
public class ToastManager {
    public static final int INFORMATION = 0;
    public static final int WARNING     = 1;
    public static final int ERROR       = 2;

    public static void show(Context context, String text,int toastType) {

        LayoutInflater inflater =(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View layout = inflater.inflate(R.layout.toast_layout, null);

        TextView tv = (TextView) layout.findViewById(R.id.tvTexto);
        tv.setText(text);

        LinearLayout llRoot = (LinearLayout) layout.findViewById(R.id.llRoot);
        Toast toast = new Toast(context);
        Drawable img = null;
        int bg = 0;

        switch (toastType) {

            case ERROR:
                img = context.getResources().getDrawable(R.drawable.btn_dialog_pressed);
                bg  = R.drawable.toast_background_red;
                toast.setDuration(Toast.LENGTH_LONG);
                break;

            case INFORMATION:
                img = context.getResources().getDrawable(R.drawable.ic_confirma2);
                toast.setDuration(Toast.LENGTH_LONG);
                bg  = R.drawable.toast_background_blue;
                break;

            case WARNING:
                img = context.getResources().getDrawable(R.drawable.ic_sincronizar);
                bg  = R.drawable.toast_background_blue;
                toast.setDuration(Toast.LENGTH_SHORT);
                break;

        }

        tv.setCompoundDrawablesWithIntrinsicBounds(img, null, null, null);
        llRoot.setBackgroundResource(bg);


        toast.setGravity(Gravity.CLIP_HORIZONTAL, 0, 120);
        toast.setView(layout);
        toast.show();
    }

}
