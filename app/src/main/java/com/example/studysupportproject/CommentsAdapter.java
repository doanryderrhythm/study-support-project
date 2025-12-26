package com.example.studysupportproject;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

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
        TextView tvCommenterAvatar, tvCommenterName, tvCommentContent, tvCommentTime;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCommenterAvatar = itemView.findViewById(R.id.tvCommenterAvatar);
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