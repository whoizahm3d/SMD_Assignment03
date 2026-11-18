package com.example.smd_assignment03;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smd_assignment03.models.AttachmentLink;
import com.example.smd_assignment03.models.CatalogItem;

import java.util.Locale;
import java.util.Objects;

public class AttachmentAdapter extends ListAdapter<AttachmentLink, AttachmentAdapter.AttachmentViewHolder> {

    public interface AttachmentActionListener {
        void onPreview(AttachmentLink link);
        void onDelete(AttachmentLink link);
    }

    private final AttachmentActionListener listener;

    public AttachmentAdapter(AttachmentActionListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    public static final DiffUtil.ItemCallback<AttachmentLink> DIFF_CALLBACK = new DiffUtil.ItemCallback<AttachmentLink>() {
        @Override
        public boolean areItemsTheSame(@NonNull AttachmentLink oldItem, @NonNull AttachmentLink newItem) {
            return Objects.equals(oldItem.getId(), newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull AttachmentLink oldItem, @NonNull AttachmentLink newItem) {
            return Objects.equals(oldItem.getCatalogId(), newItem.getCatalogId())
                    && oldItem.getResId() == newItem.getResId()
                    && oldItem.getLinkedAt() == newItem.getLinkedAt();
        }
    };

    @NonNull
    @Override
    public AttachmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_attachment, parent, false);
        return new AttachmentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentViewHolder holder, int position) {
        AttachmentLink link = getItem(position);
        holder.bind(link, listener);
    }

    public AttachmentLink getItemAt(int position) {
        if (position < 0 || position >= getItemCount()) {
            return null;
        }
        return getItem(position);
    }

    public static class AttachmentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumb;
        TextView tvFileName;
        TextView tvFileMeta;
        Button btnPreview;
        ImageView ivDelete;

        public AttachmentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumb = itemView.findViewById(R.id.iv_thumb);
            tvFileName = itemView.findViewById(R.id.tv_file_name);
            tvFileMeta = itemView.findViewById(R.id.tv_file_meta);
            btnPreview = itemView.findViewById(R.id.btn_preview);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }

        public void bind(AttachmentLink link, AttachmentActionListener listener) {
            if (link == null)
            {
                return;
            }

            CatalogItem item = AttachmentCatalog.findById(link.getCatalogId());

            if (item != null)
            {
                tvFileName.setText(item.getDisplayName());
                if ("IMAGE".equals(item.getType()))
                {
                    ivThumb.setImageResource(item.getResId());
                }
                else
                {
                    ivThumb.setImageResource(R.drawable.ic_text_file);
                }

                String kind = "IMAGE".equals(item.getType()) ? "Image" : "Document";
                tvFileMeta.setText(String.format(Locale.getDefault(), "%s • %s", kind, item.getTypeLabel()));
            }
            else
            {
                tvFileName.setText("Unknown");
                ivThumb.setImageResource(R.drawable.ic_text_file);
                tvFileMeta.setText("Unknown");
            }

            btnPreview.setOnClickListener(v -> {
                if (listener != null)
                {
                    listener.onPreview(link);
                }
            });

            ivDelete.setOnClickListener(v -> {
                if (listener != null)
                {
                    listener.onDelete(link);
                }
            });
        }
    }
}
