package kr.co.core.financial_tech.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class BoardData implements Serializable {
    private String b_type;
    private String b_idx;
    private String nick;
    private String title;
    private String image;
    private String date;
    private String view_count;
    private String comment_count;

    public BoardData(String b_type, String b_idx, String nick, String title, String image, String date, String view_count, String comment_count) {
        this.b_type = b_type;
        this.b_idx = b_idx;
        this.nick = nick;
        this.title = title;
        this.image = image;
        this.date = date;
        this.view_count = view_count;
        this.comment_count = comment_count;
    }
}
