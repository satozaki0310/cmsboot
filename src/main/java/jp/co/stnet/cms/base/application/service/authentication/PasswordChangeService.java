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

/**
 * PasswordChangeService
 */
public interface PasswordChangeService {

    /**
     * パスワード変更する。
     *
     * @param username    ユーザ名
     * @param rawPassword 新しいパスワード(ハッシュなし)
     * @return true:パスワード変更できた, false:できなかった
     */
    boolean updatePassword(String username, String rawPassword);

}