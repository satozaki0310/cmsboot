package jp.co.stnet.cms.base.application.service.filemanage;


import jp.co.stnet.cms.base.domain.model.filemanage.FileManaged;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * �t�@�C���𑀍삷��N���X
 */
public interface FileManagedService {

    /**
     * id�Ńt�@�C�����擾����B
     *
     * @param id id
     * @return �t�@�C��
     */
    byte[] getFile(Long id);

//    /**
//     * uuid�Ńt�@�C�����擾����B
//     *
//     * @param uuid uuid
//     * @return �t�@�C��
//     */
//    byte[] getFile(String uuid);

    /**
     * id��FileManaged����������B
     *
     * @param id id
     * @return FileManaged
     */
    FileManaged findById(Long id);

    /**
     * uuid��FileManaged����������B
     *
     * @param uuid uuid
     * @return FileManaged
     */
    FileManaged findByUuid(String uuid);

    /**
     * �t�@�C����ۑ�����B(�ꎞ�ۑ�)
     *
     * @param file     MultipartFile
     * @param fileType �t�@�C���^�C�v
     * @return FileManaged
     * @throws IOException �t�@�C�������O
     */
    FileManaged store(MultipartFile file, String fileType) throws IOException;

//    /**
//     * �t�@�C����ۑ�����B(�ꎞ�ۑ�)
//     *
//     * @param file     File
//     * @param fileType �t�@�C���^�C�v
//     * @return FileManaged
//     * @throws IOException �t�@�C�������O
//     */
//    FileManaged store(File file, String fileType) throws IOException;

    /**
     * �t�@�C���̃X�e�[�^�X���ꎞ�ۑ�����i�v�ۑ��ɕύX����B
     *
     * @param uuid uuid
     */
    void permanent(String uuid);

    /**
     * id��FileManaged�ƃt�@�C�����폜����B
     *
     * @param id id
     */
    void delete(Long id);

    /**
     * uuid��FileManaged�ƃt�@�C�����폜����B
     *
     * @param uuid uuid
     */
    void delete(String uuid);

    /**
     * �w������ȑO�̈ꎞ�ۑ���Ԃ�FileManaged�ƃt�@�C�����폜����B
     *
     * @param deleteTo ����
     */
    void cleanup(LocalDateTime deleteTo);

    /**
     * �t�@�C����ۑ�����f�B���N�g�����擾����B
     *
     * @return �f�B���N�g��
     */
    String getFileStoreBaseDir();

    /**
     * �t�@�C���̓��e���擾����B
     *
     * @param uuid UUID
     * @return �t�@�C���̓��e
     * @throws IOException   �t�@�C���̓ǂݍ��݂Ɏ��s����ꍇ
     * @throws TikaException Tika�Ńt�@�C���̓��e��ǂݍ��݂Ɏ��s����ꍇ
     */
    String getContent(String uuid) throws IOException, TikaException;

    /**
     * FileManaged�ƃt�@�C�����R�s�[���A�V����UUID�𔭔Ԃ���B
     *
     * @param uuid UUID
     * @return �R�s�[���FileManaged
     * @throws IOException �t�@�C���̑���Ɏ��s�����ꍇ
     */
    FileManaged copyFile(String uuid) throws IOException;

    /**
     * �w�肵��URI�Ńt�@�C�����폜����B(�����t�@�C���̂�)
     *
     * @param uri URI
     */
    void deleteFile(String uri);

    /**
     * HTML�G�X�P�[�v����јA������󔒂̏���
     *
     * @param rawContent ������
     * @return �G�X�P�[�X���ꂽ������
     */
    String escapeContent(String rawContent);


}
