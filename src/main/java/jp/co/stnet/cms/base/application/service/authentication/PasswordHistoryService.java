/*
 * Copyright(c) 2013 NTT DATA Corporation. Copyright(c) 2013 NTT Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package jp.co.stnet.cms.base.application.service.authentication;


import jp.co.stnet.cms.base.domain.model.authentication.PasswordHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * PasswordHistorySharedService
 */
public interface PasswordHistoryService {

    /**
     * �o�^����B
     *
     * @param history �G���e�B�e�B
     * @return �o�^��������
     */
    int insert(PasswordHistory history);

    /**
     * �w�肵�����[�U���ƈ�v���A���p�J�n�����V�����f�[�^����������B
     *
     * @param username ���[�U��
     * @param useFrom  ���p�J�n��
     * @return �q�b�g�����p�X���[�h�����̃��X�g
     */
    List<PasswordHistory> findHistoriesByUseFrom(String username, LocalDateTime useFrom);

    /**
     * �w�肵�����[�U���ƈ�v���āA�o�^�����V����������w�肵����������������B
     *
     * @param username ���[�U��
     * @param limit    �擾����
     * @return �q�b�g�����f�[�^�̃��X�g
     */
    List<PasswordHistory> findLatest(String username, int limit);

}
