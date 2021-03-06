package com.njamb.geodrink.activities;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.nitrico.lastadapter.LastAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.njamb.geodrink.BR;
import com.njamb.geodrink.R;
import com.njamb.geodrink.models.User;

import java.util.Collections;

public class ScoreboardActivity extends AppCompatActivity {

    private ObservableList<User> listOfUsers = new ObservableArrayList<>();
    private ChildEventListener mChildEventListener;
    private LastAdapter mLastAdapter;
    private RecyclerView mRecyclerView;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);

        mChildEventListener = getChildEventListener();

        FirebaseDatabase.getInstance().getReference("users")
                .addChildEventListener(mChildEventListener);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_scoreboard);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mLastAdapter = new LastAdapter(listOfUsers, BR.user)
                .map(User.class, R.layout.list_item_user)
                .into(mRecyclerView);
    }

    @BindingAdapter("android:src")
    public static void setImageUri(ImageView view, String uri){
        Glide.with(view.getContext())
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .apply(RequestOptions.errorOf(R.mipmap.geodrink_blue_logo))
                .into(view);
    }

    private ChildEventListener getChildEventListener() {
        return new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                listOfUsers.add(dataSnapshot.getValue(User.class));
                Collections.sort(listOfUsers);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                int i = listOfUsers.indexOf(user);
                if (listOfUsers.get(i).points != user.points) {
                    listOfUsers.set(i, user);
                    Collections.sort(listOfUsers);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                listOfUsers.remove(listOfUsers.indexOf(user));
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                toast("onChildMoved");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                toast("onCancelled");
            }
        };
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
