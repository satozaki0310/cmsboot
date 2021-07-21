package jp.co.stnet.cms.sales.domain.model.document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * ���P�ʂ̃A�N�Z�X
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(indexes = {@Index(name = "UK_DOCUMENT_ACCESS_01", columnList = "accessDate, username, documentId", unique = true)})
public class DocumentAccess implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * �A�N�Z�X��
     */
    private LocalDate accessDate;

    /**
     * ���[�U��
     */
    private String username;

    /**
     * �h�L�������g��ID
     */
    private Long documentId;

}
