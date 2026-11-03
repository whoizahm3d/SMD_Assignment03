package com.example.smd_assignment03;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;

import java.util.ArrayList;
import java.util.Locale;

public class FileAdapter extends BaseAdapter {

    public interface FileActionListener {
        void onFileClicked(AttachmentLink link);
        void onFileDeleted(AttachmentLink link);
    }

    private Context context;
    private ArrayList<AttachmentLink> shownFiles;
    private FileActionListener listener;
    private LayoutInflater inflater;

    public FileAdapter(Context context, ArrayList<AttachmentLink> shownFiles, FileActionListener listener) {
        this.context = context;
        this.shownFiles = shownFiles;
        this.listener = listener;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return shownFiles == null ? 0 : shownFiles.size();
    }

    @Override
    public Object getItem(int position) {
        return shownFiles.get(position);
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
            convertView = inflater.inflate(R.layout.item_file, parent, false);
            holder = new ViewHolder();
            holder.ivFileIcon = convertView.findViewById(R.id.iv_file_icon);
            holder.tvFileName = convertView.findViewById(R.id.tv_file_name);
            holder.tvFileMeta = convertView.findViewById(R.id.tv_file_meta);
            holder.ivDeleteFile = convertView.findViewById(R.id.iv_delete_file);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        AttachmentLink link = shownFiles.get(position);
        CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());

        if (item != null)
        {
            holder.tvFileName.setText(item.getDisplayName());
            if ("IMAGE".equals(item.getType()))
            {
                holder.ivFileIcon.setImageResource(R.drawable.ic_image_file);
            }
            else
            {
                holder.ivFileIcon.setImageResource(R.drawable.ic_text_file);
            }
            String dateStr = DateUtils.format(link.getLinkedAt());
            holder.tvFileMeta.setText(String.format(Locale.getDefault(), "%s • Linked %s", item.getTypeLabel(), dateStr));
        }
        else
        {
            holder.tvFileName.setText("Unknown file");
            holder.ivFileIcon.setImageResource(R.drawable.ic_text_file);
            holder.tvFileMeta.setText("Unknown");
        }

        convertView.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onFileClicked(link);
            }
        });

        holder.ivDeleteFile.setOnClickListener(v -> {
            if (listener != null)
            {
                listener.onFileDeleted(link);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        ImageView ivFileIcon;
        TextView tvFileName;
        TextView tvFileMeta;
        ImageView ivDeleteFile;
    }
}
