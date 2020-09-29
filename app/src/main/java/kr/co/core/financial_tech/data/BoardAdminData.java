package kr.co.core.financial_tech.data;

import java.io.Serializable;

import lombok.Data;

@Data
public class BoardAdminData implements Serializable {
    private String type;
    private String contents;

    public BoardAdminData(String type, String contents) {
        this.type = type;
        this.contents = contents;
    }
}
