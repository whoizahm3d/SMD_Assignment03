package com.example.smd_assignment03;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AttachmentPreviewDialog {

    public static void show(Context context, AttachmentLink link) {
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());
        if (item == null)
        {
            return;
        }

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        if ("IMAGE".equals(item.getType()))
        {
            View view = LayoutInflater.from(context).inflate(R.layout.dialog_view_attachment, null);
            ImageView ivFull = view.findViewById(R.id.iv_attachment_full);
            ivFull.setAdjustViewBounds(true);
            ivFull.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ivFull.setImageResource(item.getResId());

            dialog.setContentView(view);
        }
        else
        {
            ScrollView scrollView = new ScrollView(context);
            scrollView.setPadding(24, 24, 24, 24);
            TextView tv = new TextView(context);
            tv.setText(readRawTextFile(context, item.getResId()));
            tv.setTextColor(ContextCompat.getColor(context, android.R.color.white));
            tv.setTextSize(16);
            scrollView.addView(tv);
            dialog.setContentView(scrollView);
            if (dialog.getWindow() != null)
            {
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.black);
            }
        }

        dialog.show();
    }

    private static String readRawTextFile(Context context, int resId) {
        try {
            InputStream inputStream = context.getResources().openRawResource(resId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            inputStream.close();
            return sb.toString().trim();
        } catch (Exception e) {
            return "Unable to load text file content.";
        }
    }
}
