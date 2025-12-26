package com.example.studysupportproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PostsActivity extends AppCompatActivity {
    private RecyclerView rvPosts;
    private LinearLayout llEmptyState;
    private PostsAdapter postsAdapter;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        dbHelper = new DatabaseHelper();

        rvPosts = findViewById(R.id.rvPosts);
        llEmptyState = findViewById(R.id.llEmptyState);

        setupRecyclerView();
        loadPosts();
    }

    private void setupRecyclerView() {
        postsAdapter = new PostsAdapter(new ArrayList<>(), post -> {
            // Handle post click
            Toast.makeText(this, "Clicked: " + post.getTitle(), Toast.LENGTH_SHORT).show();
        });

        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        rvPosts.setAdapter(postsAdapter);
    }

    private void loadPosts() {
        List<Post> posts = dbHelper.getPostsByAuthor(-1);

        if (posts.isEmpty()) {
            rvPosts.setVisibility(View.GONE);
            llEmptyState.setVisibility(View.VISIBLE);
        } else {
            rvPosts.setVisibility(View.VISIBLE);
            llEmptyState.setVisibility(View.GONE);
            postsAdapter.updatePosts(posts);
        }
    }
}

class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {
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
        TextView tvTitle, tvContent, tvPostType;
        TextView tvAuthor, tvAdditionalInfo;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            postCardView = (CardView) itemView;
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvPostType = itemView.findViewById(R.id.tvPostType);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvAdditionalInfo = itemView.findViewById(R.id.tvAdditionalInfo);
        }

        public void bind(Post post) {
            tvTitle.setText(post.getTitle());
            tvContent.setText(post.getContent());
            tvPostType.setText(post.getPostType().toUpperCase());

            String authorUsername = dbHelper.getUsernameByAuthorId(post.getAuthorId());
            if (authorUsername != null) {
                String authorString = "Author: " + authorUsername;
                tvAuthor.setText(authorString);
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