package jp.co.stnet.cms.example.domain.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class DocumentHistoryBean {

    //datatablesのお作法
    private String DT_RowId;

    private String DT_RowClass;

    private Map<String, String> DT_RowAttr;

    @JsonProperty("DT_RowId")
    public String getDT_RowId() {
        return DT_RowId;
    }

    @JsonProperty("DT_RowClass")
    public String getDT_RowClass() {
        return DT_RowClass;
    }

    @JsonProperty("DT_RowAttr")
    public Map<String, String> getDT_RowAttr() {
        return DT_RowAttr;
    }


    private Long rid;

    //リンク用
    private String ridLabel;

    private Long version;

    private String reasonForChange;

    //ユーザID
    private String lastModifiedBy;

    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDateTime lastModifiedDate;

    /**
     * 最終更新者名
     */
    private String lastModifiedByLabel;
}
