package com.example.studysupportproject;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.bumptech.glide.Glide;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.CommentViewHolder> {
    private List<Comment> comments;

    public CommentsAdapter(List<Comment> comments) {
        this.comments = comments != null ? comments : new ArrayList<>();
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        holder.bind(comments.get(position));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void updateComments(List<Comment> newComments) {
        this.comments = newComments;
        notifyDataSetChanged();
    }

    class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCommenterAvatar;
        TextView tvCommenterName, tvCommentContent, tvCommentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCommenterAvatar = itemView.findViewById(R.id.imgCommenterAvatar);
            tvCommenterName = itemView.findViewById(R.id.tvCommenterName);
            tvCommentContent = itemView.findViewById(R.id.tvCommentContent);
            tvCommentTime = itemView.findViewById(R.id.tvCommentTime);
        }

        public void bind(Comment comment) {
            if (comment.getCommenterUsername() != null && !comment.getCommenterUsername().isEmpty()) {
                tvCommenterName.setText(comment.getCommenterUsername());
            } else {
                tvCommenterName.setText("User " + comment.getCommenterId());
            }
            if (comment.getCommenterAvatar() != null && !comment.getCommenterAvatar().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(comment.getCommenterAvatar())
                        .placeholder(R.drawable.ic_avatar_placeholder)
                        .error(R.drawable.ic_avatar_placeholder)
                        .into(imgCommenterAvatar);
            }
            tvCommentContent.setText(comment.getContent());
            tvCommentTime.setText(formatDate(comment.getCreatedAt()));
        }

        private String formatDate(String dateString) {
            try {
                if (dateString.contains(".")) {
                    dateString = dateString.substring(0, dateString.indexOf("."));
                }

                SimpleDateFormat inputFormat;
                if (dateString.contains("T")) {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                }

                Date date = inputFormat.parse(dateString);
                Date now = new Date();

                long diffInMillis = now.getTime() - date.getTime();
                long diffInMinutes = diffInMillis / (60 * 1000);
                long diffInHours = diffInMillis / (60 * 60 * 1000);
                long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);

                if (diffInMinutes < 1) {
                    return "Vừa xong";
                } else if (diffInMinutes < 60) {
                    return diffInMinutes + " phút trước";
                } else if (diffInHours < 24) {
                    return diffInHours + " giờ trước";
                } else if (diffInDays < 7) {
                    return diffInDays + " ngày trước";
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                return dateString;
            }
        }
    }
}