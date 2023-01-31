package com.guuda.sheep.activity.game.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.guuda.sheep.R;


public class GamePassDialog extends Dialog {

    public GamePassDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private TextView titleTextView;
        private TextView btn_one;
        private ImageView iv_gif;

        public Builder(Context context) {
            this.context = context;
        }

        public TextView getTitleTextView() {
            return titleTextView;
        }

        public TextView getBtn_one() {
            return btn_one;
        }

        public ImageView getIv_gif() {
            return iv_gif;
        }

        public GamePassDialog create() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final GamePassDialog dialog = new GamePassDialog(context, R.style.SunDialog);
            View layout = inflater.inflate(R.layout.dialog_onebutton_notitle, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
                    android.view.ViewGroup.LayoutParams.WRAP_CONTENT
            ));
            dialog.setContentView(layout);
            titleTextView = (TextView)layout.findViewById(R.id.dialog_title);
            btn_one = (TextView)layout.findViewById(R.id.btn_one);
            iv_gif = (ImageView) layout.findViewById(R.id.iv_gif);
            return dialog;
        }
    }
}
