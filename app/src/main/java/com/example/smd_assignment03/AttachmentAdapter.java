package com.example.smd_assignment03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;

import java.util.ArrayList;
import java.util.Locale;

public class AttachmentAdapter extends BaseAdapter {

    public interface AttachmentActionListener {
        void onPreview(AttachmentLink link);
        void onDelete(AttachmentLink link);
    }

    private Context context;
    private ArrayList<AttachmentLink> shownLinks;
    private AttachmentActionListener listener;
    private LayoutInflater inflater;

    public AttachmentAdapter(Context context, ArrayList<AttachmentLink> shownLinks, AttachmentActionListener listener) {
        this.context = context;
        this.shownLinks = shownLinks;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shownLinks == null ? 0 : shownLinks.size();
    }

    @Override
    public Object getItem(int position) {
        return shownLinks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.item_attachment, parent, false);
            holder = new ViewHolder();
            holder.ivThumb = convertView.findViewById(R.id.iv_thumb);
            holder.tvFileName = convertView.findViewById(R.id.tv_file_name);
            holder.tvFileMeta = convertView.findViewById(R.id.tv_file_meta);
            holder.btnPreview = convertView.findViewById(R.id.btn_preview);
            holder.ivDelete = convertView.findViewById(R.id.iv_delete);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        AttachmentLink link = shownLinks.get(position);
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());

        if (item != null)
        {
            holder.tvFileName.setText(item.getDisplayName());
            if ("IMAGE".equals(item.getType()))
            {
                holder.ivThumb.setImageResource(item.getResId());
            }
            else
            {
                holder.ivThumb.setImageResource(R.drawable.ic_text_file);
            }

            String kind = "IMAGE".equals(item.getType()) ? "Image" : "Document";
            holder.tvFileMeta.setText(String.format(Locale.getDefault(), "%s • %s", kind, item.getTypeLabel()));
        }
        else
        {
            holder.tvFileName.setText("Unknown");
            holder.ivThumb.setImageResource(R.drawable.ic_text_file);
            holder.tvFileMeta.setText("Unknown");
        }

        holder.btnPreview.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onPreview(link);
            }
        });

        holder.ivDelete.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onDelete(link);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView ivThumb;
        TextView tvFileName;
        TextView tvFileMeta;
        Button btnPreview;
        ImageView ivDelete;
    }
}
