package com.example.username.mydiary;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import io.realm.OrderedRealmCollection;
import io.realm.Realm;
import io.realm.RealmRecyclerViewAdapter;
import io.realm.RealmResults;

/**
 * Created by hiroaki on 2017/04/18.
 */

public class DiaryRealmAdapter extends
        RealmRecyclerViewAdapter<Diary, DiaryRealmAdapter.DiaryViewHolder> {
    Context context;

    private Realm mRealm;

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        protected long id;
        protected TextView
                title;
        protected TextView bodyText;
        protected TextView date;
        protected ImageView photo;
        protected FloatingActionButton floatingActionButton;

        public DiaryViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title);
            bodyText = (TextView) itemView.findViewById(R.id.body);
            date = (TextView) itemView.findViewById(R.id.date);
            photo = (ImageView) itemView.findViewById(R.id.diary_photo);
            floatingActionButton = (FloatingActionButton) itemView.findViewById(R.id.floatingActionButton);

        }
    }

    public DiaryRealmAdapter(
            @NonNull Context context,
            @Nullable OrderedRealmCollection<Diary> data,
            boolean autoUpdate) {
        super(data, autoUpdate);
        this.context = context;
    }

    @Override
    public DiaryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.card_layout, parent, false);
        final DiaryViewHolder holder = new DiaryViewHolder(itemView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Diary diary = getData().get(position);
                long diaryId = diary.id;
                Intent
                        intent = new Intent(context, ShowDiaryActivity.class);
                intent.putExtra(ShowDiaryActivity.DIARY_ID, diaryId);
                context.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(
             final DiaryViewHolder holder,
             final int position) {

        Diary diary = getData().get(position);
        holder.id = diary.id;
        holder.title.setText(diary.title);
        holder.bodyText.setText(diary.bodyText);
        holder.date.setText(diary.date);
        if (diary.image != null && diary.image.length != 0) {
            Bitmap bmp = MyUtils.getImageFromByte(diary.image);
            holder.photo.setImageBitmap(bmp);
        }
        holder.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                mRealm = Realm.getDefaultInstance();
                mRealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        Diary wDairy = realm.where(Diary.class).equalTo("id", holder.id).findFirst();
                        wDairy.deleteFromRealm();
                    }
                });

            }
        });

    }

}