package jp.co.stnet.cms.base.application.service.index;

/**
 * Lucene�����𑀍삷��
 */
public interface IndexService {

    /**
     * Lucene�������X�V����(�񓯊�����)
     *
     * @param entityName �G���e�B�e�B��
     * @throws InterruptedException   ?
     * @throws ClassNotFoundException �G���e�B�e�B���̃N���X�����݂��Ȃ��ꍇ
     */
    void reindexing(String entityName) throws InterruptedException, ClassNotFoundException;

    /**
     * Lucene�������X�V����(��������)
     *
     * @param entityName �G���e�B�e�B��
     * @return true:����
     * @throws InterruptedException   ?
     * @throws ClassNotFoundException �G���e�B�e�B���̃N���X�����݂��Ȃ��ꍇ
     */
    boolean reindexingSync(String entityName) throws InterruptedException, ClassNotFoundException;

}
