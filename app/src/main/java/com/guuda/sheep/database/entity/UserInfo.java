package com.guuda.sheep.database.entity;

import android.text.TextUtils;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "user", indices = {
        @Index(
                value = { "username" },
                unique = true
        )
})
public class UserInfo {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "username")
    public String username = "";

    @ColumnInfo(name = "password")
    public String password = "";

    @ColumnInfo(name = "nickname")
    public String nickname = "";

    @ColumnInfo(name = "avatar")
    public String avatar = "";

    @ColumnInfo(name = "high_score")
    public int highScore = 0;

    @ColumnInfo(name = "reach_level")
    public int reachLevel = 0;

    @ColumnInfo(name = "birthday")
    public long birthday;

    @ColumnInfo(name = "location")
    public String location = "";

    public String getDisplayName() {
        if (TextUtils.isEmpty(nickname)) {
            return username;
        }
        return nickname;
    }

}
