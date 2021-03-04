package com.example.mobilestore.reviews_questions;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobilestore.R;

import java.util.List;

//Adapter used for handling Reviews and Questions in the OpenProductActivity
public class ReviewsQuestionsAdapter extends RecyclerView.Adapter<ReviewsQuestionsAdapter.QuestionViewHolder> {
    public List<Question> questionList;
    public List<Review> reviewList;
    public int listPick;
    private OnItemClickListener Listener;
    public Context context;
    private int size = 5;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public ReviewsQuestionsAdapter(List<Review> reviewList, List<Question> questionList, int listPick, Context _context) {
        this.reviewList = reviewList;
        this.questionList = questionList;
        this.listPick = listPick;
        this.context = _context;
    }

    public static class QuestionViewHolder extends RecyclerView.ViewHolder {
        public TextView reviewText;
        public TextView rUsername;

        public QuestionViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);

            reviewText = itemView.findViewById(R.id.cCommentText);
            rUsername = itemView.findViewById(R.id.cUsername);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAbsoluteAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {

                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        return new QuestionViewHolder(view, Listener);
    }

    @Override
    public void onBindViewHolder(@NonNull final QuestionViewHolder holder, int position) {
        if (listPick == 0) {
            final Review currentReview = reviewList.get(position);
            holder.rUsername.setText(currentReview.getUserName());
            holder.reviewText.setText(currentReview.getReviewText());
        } else if (listPick == 1) {
            final Question currentQuestion = questionList.get(position);
            holder.rUsername.setText(currentQuestion.getUserName());
            holder.reviewText.setText(currentQuestion.getQuestionText());
        }
    }


    public int loadMore() {
        //Loads the next 5 reviews or questions from the list
        int difference = 0;
        if (listPick == 0) {
            difference = reviewList.size() - size;
            size += Math.min(difference, 5);
        } else if (listPick == 1) {
            difference = questionList.size() - size;
            size += Math.min(difference, 5);
        }
        notifyDataSetChanged();
        return size;
    }

    @Override
    public int getItemCount() {
        if (listPick == 0) {
            if (reviewList.size() >= 5) {
                return size;
            }
        } else if (listPick == 1) {
            if (questionList.size() >= 5) {
                return size;
            }
        }
        return size;
    }
}
