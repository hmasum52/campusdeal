package com.codervai.campusdeal.util;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

public class MyDialog {
    private Dialog dialog;

    public MyDialog(Context context, int layout_id) {
        dialog = new Dialog(context);
        dialog.setContentView(layout_id);
        dialog.setCancelable(false);
    }

    public void showDialog(){
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.show();
    }

    public void showDialog(String message, int view_id){
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        TextView textView = dialog.findViewById(view_id);
        if(textView!=null)
            textView.setText(message);
        dialog.show();
    }

    public void hideDialog(){
        dialog.dismiss();
    }

    public <T extends View> T findViewById(int id){
        return dialog.findViewById(id);
    }

}
