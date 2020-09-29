package kr.co.core.financial_tech.data;

import lombok.Data;

@Data
public class CommentDoubleData {
    String c_idx;
    String u_idx;
    String u_pre_nick;
    String u_nick;
    String c_comment;
    String c_date;

    public CommentDoubleData(String c_idx, String u_idx, String u_pre_nick, String u_nick, String c_comment, String c_date) {
        this.c_idx = c_idx;
        this.u_idx = u_idx;
        this.u_pre_nick = u_pre_nick;
        this.u_nick = u_nick;
        this.c_comment = c_comment;
        this.c_date = c_date;
    }
}
