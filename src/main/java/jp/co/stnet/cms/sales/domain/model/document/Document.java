package jp.co.stnet.cms.sales.domain.model.document;

import com.fasterxml.jackson.annotation.JsonFormat;
import jp.co.stnet.cms.base.domain.model.AbstractEntity;
import jp.co.stnet.cms.base.domain.model.StatusInterface;
import jp.co.stnet.cms.base.domain.model.variable.Variable;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@EntityListeners(AuditingEntityListener.class)
public class Document extends AbstractEntity<Long> implements Serializable, StatusInterface {

    // DocumentIndex, DocumentRevisionエンティティとフィールドを一致させること

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
    private String status;

    /**
     * 変更履歴を残す
     */
    private boolean saveRevision;

    /**
     * タイトル
     */
    private String title;

    /**
     * 本文
     */
    @Column(columnDefinition = "TEXT")
    private String body;

    /**
     * 公開区分
     */
    @Column(nullable = false)
    private String publicScope = DocPublicScope.ALL.getValue();

    /**
     * ドキュメント管理番号
     */
    private String documentNumber;

    /**
     * 管理部門
     */
    private String chargeDepartment;

    /**
     * 管理担当者
     */
    private String chargePerson;

    /**
     * 制定日
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate enactmentDate;

    /**
     * 最終改定日
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate lastRevisedDate;

    /**
     * 実施日
     */
    @JsonFormat(pattern = "yyyy/MM/dd")
    private LocalDate implementationDate;

    /**
     * 制定箇所
     */
    private String enactmentDepartment;

    /**
     * 変更理由
     */
    private String reasonForChange;

    /**
     * 活用シーン
     */
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> useStage;

    /**
     * 区分
     */
    private Long docCategory;

    /**
     * 区分(Variable)
     */
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "docCategory", referencedColumnName = "id", unique = false, insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'DOC_CATEGORY'", referencedColumnName = "type")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "docCategory", referencedColumnName = "code", unique = false, insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    })
    private Variable docCategoryVariable;

    /**
     * サービス
     */
    private Long docService;

    /**
     * サービス(Variable)
     */
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
//    @JoinColumn(name = "docService", referencedColumnName = "code", unique = false, insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT))
//    @WhereJoinTable(clause = "type = 'DOC_SERVICE'")
    @JoinColumnsOrFormulas({
            @JoinColumnOrFormula(formula = @JoinFormula(value = "'DOC_SERVICE'", referencedColumnName = "type")),
            @JoinColumnOrFormula(column = @JoinColumn(name = "docService", referencedColumnName = "code", unique = false, insertable = false, updatable = false, foreignKey = @ForeignKey(value = ConstraintMode.NO_CONSTRAINT)))
    })
    private Variable docServiceVariable;

    /**
     * ファイル
     */
    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    private List<File> files;

    /**
     * 想定読者
     */
    private String intendedReader;

    /**
     * 概要
     */
    private String summary;

    /**
     * 顧客公開区分
     */
    @Column(nullable = false)
    private String customerPublic = CustomerPublic.OPEN.getCodeValue();

}
