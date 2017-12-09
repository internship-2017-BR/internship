package com.example.username.mydiary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.realm.Realm;


public class MainActivity extends AppCompatActivity
        implements DiaryListFragment.OnFragmentInteractionListener {
    private Realm mRealm;

    /**
     * activity作成時に呼ばれる
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mRealm = Realm.getDefaultInstance();// realm(データベース)インスタンス取得

        //createTestData();
        showDiaryList();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealm.close();
    }

    private void createTestData() {
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // idフィールドの最大値を取得
                Number maxId = mRealm.where(Diary.class).max("id");
                long nextId = 0;
                if (maxId != null) nextId = maxId.longValue() + 1;
                // createObjectではIDを渡してオブジェクトを生成する
                Diary diary = realm.createObject(Diary.class, new Long(nextId));
                diary.title = "テストタイトル";
                diary.bodyText = "テスト本文です。";
                diary.date = "Feb 22";
            }
        });
    }

    /**
     * 日記リスト表示
     */
    private void showDiaryList() {
        FragmentManager manager = getSupportFragmentManager(); //フラグメントのサポート機能
        Fragment fragment = manager.findFragmentByTag("DiaryListFragment"); // 日記リストフラグメントインスタンス化
        if (fragment == null) {
            fragment = new DiaryListFragment();
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.add(R.id.content, fragment, "DiaryListFragment");
            transaction.commit();
        }
    }

    /**
     * 日記作成ボタンを選択したときの処理
     */
    @Override
    public void onAddDiarySelected() {
        mRealm.beginTransaction(); // トランザクション開始
        Number maxId = mRealm.where(Diary.class).max("id"); //登録されている日記データのID最大値を取得
        long nextId = 0;
        if (maxId != null) nextId = maxId.longValue() + 1;
        Diary diary = mRealm.createObject(Diary.class, new Long(nextId)); // 新規登録用のrealmオブジェクト作成
        diary.date = new SimpleDateFormat("MMM d", Locale.US).format(new Date());
        mRealm.commitTransaction(); // トランザクション処理終了
        InputDiaryFragment inputDiaryFragment = InputDiaryFragment.newInstance(nextId); // 日記作成画面フラグメントのインスタンス作成
        FragmentManager manager = getSupportFragmentManager(); // フラグメントのサポート機能を持っている
        FragmentTransaction transaction = manager.beginTransaction(); // トランザクション作成
        transaction.replace(R.id.content, inputDiaryFragment, "InputDiaryFragment");// 日記作成画面
        transaction.addToBackStack(null);
        transaction.commit();
    }
}
