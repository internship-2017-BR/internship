package com.example.username.mydiary;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * 日記モデル
 */
public class Diary extends RealmObject {
    // 日記がもつデータの一塊
    @PrimaryKey
    public long id;// ID
    public String title;// 日記タイトル
    public String bodyText;// 日記本文
    public String date;// 作成日
    public byte[] image;// 貼り付けた画像の情報
}
