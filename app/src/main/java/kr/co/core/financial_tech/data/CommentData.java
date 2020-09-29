package kr.co.core.financial_tech.data;

import java.util.ArrayList;

import lombok.Data;

@Data
public class CommentData {
    String c_idx;
    String u_idx;
    String u_nick;
    String c_comment;
    String c_date;
    ArrayList<CommentDoubleData> list_double = new ArrayList<>();

    public CommentData(String c_idx, String u_idx, String u_nick, String c_comment, String c_date, ArrayList<CommentDoubleData> list_double) {
        this.c_idx = c_idx;
        this.u_idx = u_idx;
        this.u_nick = u_nick;
        this.c_comment = c_comment;
        this.c_date = c_date;

        this.list_double = list_double;
    }
}
