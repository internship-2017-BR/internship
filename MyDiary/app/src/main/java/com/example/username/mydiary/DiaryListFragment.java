package com.example.username.mydiary;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * 日記リストフラグメント
 *
 */
public class DiaryListFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private Realm mRealm;

    public DiaryListFragment() {
    }

    public static DiaryListFragment newInstance() {
        DiaryListFragment fragment = new DiaryListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { // フラグメントが作られるタイミングで呼ばれる
        super.onCreate(savedInstanceState);
        mRealm = Realm.getDefaultInstance(); // realm(データベース)インスタンス取得
    }

    @Override
    public void onDestroy() { // フラグメントが削除されるタイミングで呼ばれる
        super.onDestroy();
        mRealm.close();
    }

    /**
     * フラグメントが作成された時点で一度呼ばれる
     *日記リストフラグメントを縦方向に繰り返し表示する
     * @param inflater :xmlのレイアウトファイルを利用するためのもの
     * @param container :複数のviewを配置するためのもの
     * @param savedInstanceState :様々なオブジェクトの入れ物
     * @return 日記リストview
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_diary_list, container, false); // レイアウト情報をもとにviewオブジェクト作成
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recycler); // リスト部分のviewオブジェクト作成
        LinearLayoutManager llm = new LinearLayoutManager(getActivity()); //recyclerViewのレイアウトの設定を行うためのインスタンス
        llm.setOrientation(LinearLayoutManager.VERTICAL); // recyclerViewの並びを縦に設定
        recyclerView.setLayoutManager(llm); // recyclerViewに設定情報をセット

        // データベース関連の処理
        RealmResults<Diary> diaries = mRealm.where(Diary.class).findAll(); //  DBから日記データ全件取得
        DiaryRealmAdapter adapter = new DiaryRealmAdapter(getActivity(), diaries, true); // 日記データリストをrecyclerViewに表示するためのクラスをインスタンス化
        recyclerView.setAdapter(adapter); // recyclerViewにadapterをセット
        return v;
    }

    /**
     * Fragmentがアタッチされたとき呼ばれる
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(
                    context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * フラグメントのイベントリスナー実装クラス
     */
    public interface OnFragmentInteractionListener {
        void onAddDiarySelected();
    }

    /**
     * Fragmentが関連付いているActivityのonCreate()が呼ばれた直後に呼び出される
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true); // メニューを使用するための準備
    }

    /**
     * 使用するメニューの内容をここで作成する
     * @param menu
     * @param inflater
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_diary_list, menu);
        MenuItem addDiary = menu.findItem(R.id.menu_item_add_diary);
        MenuItem deleteAll = menu.findItem(R.id.menu_item_delete_all);
        MyUtils.tintMenuIcon(getContext(), addDiary, android.R.color.white);
        MyUtils.tintMenuIcon(getContext(), deleteAll, android.R.color.white);
    }


    /**
     * メニューにおいているボタンのイベントを実装
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_item_add_diary: // 日記新規作成ボタン押下時
                if (mListener != null) mListener.onAddDiarySelected();
                return true;
            case R.id.menu_item_delete_all: // 日記全件削除ボタン押下時
                final RealmResults<Diary> diaries =
                        mRealm.where(Diary.class).findAll();
                mRealm.executeTransaction(
                        new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                diaries.deleteAllFromRealm();
                            }
                        });
                return true;
        }
        return false;
    }
}
