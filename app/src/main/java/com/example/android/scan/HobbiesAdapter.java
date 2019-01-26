package com.example.android.scan;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

class HobbiesAdapter extends RecyclerView.Adapter<HobbiesAdapter.InterestViewHolder> {

    private List<String> interestList;

    public HobbiesAdapter(List<String> interestList) {
        this.interestList = interestList;
    }

    public void updateList(List<String> newList) {
        interestList = new ArrayList<>();
        interestList.addAll(newList); ((TextView)TextView)
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public InterestViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView v = (TextView) LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.interest_name_holder, viewGroup, false);

        return new InterestViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull InterestViewHolder interestViewHolder, int i) {
        interestViewHolder.mTextView.setText(interestList.get(i));
    }

    @Override
    public int getItemCount() {
        return interestList.size();
    }

    public static class InterestViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mTextView;
        public InterestViewHolder(TextView v) {
            super(v);
            mTextView = v;
        }
    }
}
