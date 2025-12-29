package com.example.studysupportproject;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
    private List<Post> posts;
    private OnPostClickListener onPostClickListener;
    private DatabaseHelper dbHelper;

    public interface OnPostClickListener {
        void onPostClick(Post post);
    }

    public PostsAdapter(List<Post> posts, OnPostClickListener listener) {
        this.posts = posts != null ? posts : new ArrayList<>();
        this.onPostClickListener = listener;
        dbHelper = new DatabaseHelper();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        holder.bind(posts.get(position));
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void updatePosts(List<Post> newPosts) {
        this.posts = newPosts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder {
        CardView postCardView;
        TextView tvPrivacyStatus;
        TextView tvTitle, tvContent, tvPostType;
        TextView tvAuthor, tvAdditionalInfo;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postCardView = (CardView) itemView;
            tvPrivacyStatus = itemView.findViewById(R.id.tvPrivacyStatus);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvPostType = itemView.findViewById(R.id.tvPostType);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvAdditionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
        }

        public void bind(Post post) {
            tvTitle.setText(post.getTitle());
            tvContent.setText(post.getContent());

            String postType = post.getPostType();
            switch (postType) {
                case "general":
                    tvPostType.setText("CHUNG");
                    break;
                case "announcement":
                    tvPostType.setText("THÔNG BÁO");
                    break;
                case "grade":
                    tvPostType.setText("ĐIỂM");
                    break;
            }

            String authorUsername = post.getUsername();
            if (authorUsername != null) {
                String authorString = "Author: " + authorUsername;
                tvAuthor.setText(authorString);
            }

            boolean isPublished = post.isPublished();
            if (isPublished) {
                tvPrivacyStatus.setText("Công khai");
                tvPrivacyStatus.setTextColor(Color.parseColor("#4CAF50"));
            } else {
                tvPrivacyStatus.setText("Riêng tư");
                tvPrivacyStatus.setTextColor(Color.parseColor("#FF9800"));
            }

            // Change background color based on post type
            if (post.getPostType().equals("general")) {
                postCardView.setCardBackgroundColor(0xFFFFFFFF);
            }
            else if (post.getPostType().equals("announcement")) {
                postCardView.setCardBackgroundColor(0xFFFFFEE5);
            }
            else if (post.getPostType().equals("grade")) {
                postCardView.setCardBackgroundColor(0xFFC8F3C8);
            }

            // Format published date
            if (post.getPublishedAt() != null) {
                String postDate = post.getPublishedAt();
                if (post.getUpdatedAt() != null) {
                    postDate += (" - " + post.getUpdatedAt());
                }
                tvAdditionalInfo.setText(postDate);
            } else {
                tvAdditionalInfo.setText("Not published");
            }

            // Show created and updated dates
            tvAdditionalInfo.setText("Created: " + formatDate(post.getCreatedAt()) +
                    " • Updated: " + formatDate(post.getUpdatedAt()));

            // Click listener
            itemView.setOnClickListener(v -> {
                if (onPostClickListener != null) {
                    onPostClickListener.onPostClick(post);
                }
            });
        }

        private String formatDate(String dateString) {
            try {
                SimpleDateFormat inputFormat;

                if (dateString.contains(".")) {
                    inputFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss.SSSSSS", Locale.getDefault());
                } else if (dateString.contains("T")) {
                    inputFormat = new SimpleDateFormat(
                            "yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                } else {
                    inputFormat = new SimpleDateFormat(
                            "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                }

                SimpleDateFormat outputFormat = new SimpleDateFormat(
                        "MMM dd, yyyy", Locale.getDefault());
                Date date = inputFormat.parse(dateString);
                return outputFormat.format(date != null ? date : new Date());
            } catch (Exception e) {
                return dateString;
            }
        }
    }
}