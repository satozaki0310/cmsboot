package jp.co.stnet.cms.example.domain.model.pageidx;


import jp.co.stnet.cms.base.domain.model.AbstractEntity;
import jp.co.stnet.cms.base.domain.model.StatusInterface;
import jp.co.stnet.cms.base.domain.model.common.Status;
import jp.co.stnet.cms.example.domain.model.document.Document;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.search.engine.backend.types.Aggregable;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * ページ索引エンティティ.
 */
@SuppressWarnings({"LombokDataInspection", "LombokEqualsAndHashCodeInspection"})
@Indexed
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class PageIdx extends AbstractEntity<Long> implements Serializable, StatusInterface {

    /**
     * 内部ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ステータス
     */
    @Column(nullable = false)
    @KeywordField
    private String status;

    public String getStatusLabel() {
        return Status.getByValue(this.status).getCodeLabel();
    }

    /**
     * お客さま番号
     */
    @KeywordField(aggregable = Aggregable.YES)
    @Column(nullable = false)
    private String customerNumber;

    /**
     * お客さま名
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String customerName;

    /**
     * 開始ページ番号
     */
    @Column(nullable = false)
    private Integer startPage;

    /**
     * 添付ファイル
     */
    @Column(nullable = false)
    private String attachedFileUuid;

    /**
     * ドキュメントID
     */
    @Column(nullable = false)
    private Long documentId;

    @IndexedEmbedded
//    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.NO)
    @IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
    @NotFound(action = NotFoundAction.IGNORE)
    @ManyToOne
    @JoinColumn(name = "documentId", referencedColumnName = "id", unique = false, insertable = false, updatable = false, foreignKey = @javax.persistence.ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    private Document document;

    /**
     * キーワード1
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword1;

    /**
     * キーワード2
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword2;

    /**
     * キーワード3
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword3;

    /**
     * キーワード4
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword4;

    /**
     * キーワード5
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword5;

    /**
     * キーワード6
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword6;

    /**
     * キーワード7
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword7;

    /**
     * キーワード8
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword8;

    /**
     * キーワード9
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword9;

    /**
     * キーワード10
     */
    @KeywordField(aggregable = Aggregable.YES)
    private String keyword10;

    /**
     * キー日付1
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate1;

    /**
     * キー日付2
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate2;

    /**
     * キー日付3
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate3;

    /**
     * キー日付4
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate4;

    /**
     * キー日付5
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate5;

    /**
     * キー日付6
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate6;

    /**
     * キー日付7
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate7;

    /**
     * キー日付8
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate8;

    /**
     * キー日付9
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate9;

    /**
     * キー日付10
     */
    @GenericField(aggregable = Aggregable.YES)
    private LocalDate keydate10;

    @Override
    public boolean isNew() {
        return id == null;
    }
}
