package com.guuda.sheep.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.guuda.sheep.R;
import com.guuda.sheep.customview.AwardView;
import com.guuda.sheep.utils.PUtil;

public class Dialog_succeed extends Dialog {

    public Dialog_succeed(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {private Context context;
        private AwardView view_award;

        public Builder(Context context) {
            this.context = context;
        }

        public AwardView getView_award() {
            return view_award;
        }

        public void setView_award(AwardView view_award) {
            this.view_award = view_award;
        }

        public Dialog_succeed create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final Dialog_succeed dialog = new Dialog_succeed(context, R.style.SucceedDialog);
            View layout = inflater.inflate(R.layout.dialog_succeed, null);
            dialog.addContentView(layout, new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT
                    ,   ViewGroup.LayoutParams.WRAP_CONTENT));
            dialog.setContentView(layout);
            RelativeLayout rl = (RelativeLayout) layout.findViewById(R.id.rl);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(PUtil.getScreenW(context), PUtil.getScreenH(context));
            rl.setLayoutParams(layoutParams);
            view_award = (AwardView)layout.findViewById(R.id.view_award);
            return dialog;
        }
    }
}
