package jp.co.stnet.cms.example.domain.model.pageidx;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jp.co.stnet.cms.example.domain.model.document.Document;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * ページ索引管理の一覧の行のBean
 * @author Automatically generated
 *
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageIdxListRow implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 651673036813239365L;

    /**
     * 操作
     */
    private String operations;

    /**
     * DataTables RowID
     */
    private String DT_RowId;

    /**
     * DataTables RowClass
     */
    private String DT_RowClass;

    /**
     * DataTables RT_RowAttr
     */
    private Map<String, String> DT_RowAttr;

    /**
     * 最終更新日
     */
    @JsonFormat(pattern = "yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lastModifiedDate;

    @JsonProperty("DT_RowId")
    public String getDT_RowId() {
        return id.toString();
    }

    @JsonProperty("DT_RowClass")
    public String getDT_RowClass() {
        return DT_RowClass;
    }

    @JsonProperty("DT_RowAttr")
    public Map<String, String> getDT_RowAttr() {
        return DT_RowAttr;
    }

    /**
     * 内部ID
     */
    private Long id;

    /**
     * ステータス
     */
    private String statusLabel;

    /**
     * お客さま番号
     */
    private String customerNumber;

    /**
     * お客さま名
     */
    private String customerName;

    /**
     * 開始ページ番号
     */
    private Integer startPage;

    /**
     * 添付ファイル
     */
    private String attachedFileUuid;

    /**
     * ドキュメントID
     */
    private Long documentId;


    private Document document;

    /**
     * キーワード1
     */
    private String keyword1;

    /**
     * キーワード2
     */
    private String keyword2;

    /**
     * キーワード3
     */
    private String keyword3;

}