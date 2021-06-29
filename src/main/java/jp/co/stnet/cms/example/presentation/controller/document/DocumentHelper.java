package jp.co.stnet.cms.example.presentation.controller.document;

import jp.co.stnet.cms.base.domain.model.authentication.LoggedInUser;
import jp.co.stnet.cms.base.domain.model.common.Status;
import jp.co.stnet.cms.common.constant.Constants;
import jp.co.stnet.cms.common.util.StateMap;
import jp.co.stnet.cms.example.domain.model.document.Document;
import lombok.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.validation.groups.Default;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class DocumentHelper {

    // 許可されたOperation
    private static final Set<String> allowedOperation = Set.of(
            Constants.OPERATION.CREATE,
            Constants.OPERATION.UPDATE,
            Constants.OPERATION.VIEW
    );

    // 表示するボタン
    private static final Set<String> buttons = Set.of(
            Constants.BUTTON.GOTOLIST,
            Constants.BUTTON.GOTOUPDATE,
            Constants.BUTTON.VIEW,
            Constants.BUTTON.SAVE,
            Constants.BUTTON.VALID,
            Constants.BUTTON.INVALID,
            Constants.BUTTON.DELETE
    );

    // 対象のフォーム
    private static final Class formClass = DocumentForm.class;

    /**
     * 許可されたOperationか
     *
     * @param operation 操作を表す定数
     */
    private void validate(String operation) {
        if (!allowedOperation.contains(operation)) {
            throw new IllegalArgumentException("Operation not allowed.");
        }
    }

    /**
     * 画面に応じたボタンの状態を定義
     *
     * @param operation 操作
     * @param record    DBから取り出したデータ
     * @param form      画面から入力されたデータ
     * @return StateMap
     */
    StateMap getButtonStateMap(@NonNull String operation, Document record, DocumentForm form) {

        // 入力チェック
        validate(operation);

        if (record == null) {
            record = new Document();
        }

        // StateMap初期化
        List<String> includeKeys = new ArrayList<>();
        includeKeys.addAll(buttons);
        StateMap buttonState = new StateMap(Default.class, includeKeys, new ArrayList<>());

        // 常に表示
        buttonState.setViewTrue(Constants.BUTTON.GOTOLIST);

        // 新規作成
        if (Constants.OPERATION.CREATE.equals(operation)) {
            buttonState.setViewTrue(Constants.BUTTON.SAVE);
        }

        // 編集
        else if (Constants.OPERATION.UPDATE.equals(operation)) {

            // ステータス有効
            if (Status.VALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.SAVE);
                buttonState.setViewTrue(Constants.BUTTON.VIEW);
                buttonState.setViewTrue(Constants.BUTTON.INVALID);
            }

            // ステータス無効
            if (Status.INVALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.VIEW);
                buttonState.setViewTrue(Constants.BUTTON.VALID);
                buttonState.setViewTrue(Constants.BUTTON.DELETE);
            }

        }

        // 参照
        else if (Constants.OPERATION.VIEW.equals(operation)) {

            // スタータス有効
            if (Status.VALID.getCodeValue().equals(record.getStatus())) {
                buttonState.setViewTrue(Constants.BUTTON.GOTOUPDATE);
            } else {
                // ステータス無効
                buttonState.setViewTrue(Constants.BUTTON.GOTOUPDATE);
            }
        }

        return buttonState;
    }

    /**
     * 画面に応じたフィールドの状態を定義
     *
     * @param operation 操作
     * @param record    DBから取り出したデータ
     * @param form      画面から入力されたデータ
     * @return StateMap
     */
    StateMap getFiledStateMap(String operation, Document record, DocumentForm form) {
        List<String> excludeKeys = new ArrayList<>();
        List<String> includeKeys = List.of("status");

        // 常設の隠しフィールドは状態管理しない
        StateMap fieldState = new StateMap(formClass, includeKeys, excludeKeys);

        // 新規作成
        if (Constants.OPERATION.CREATE.equals(operation)) {
            fieldState.setInputTrueAll();
            fieldState.setInputFalse("status");
            fieldState.setInputFalse("saveRevision");

        }

        // 編集
        else if (Constants.OPERATION.UPDATE.equals(operation)) {
            fieldState.setInputTrueAll();
            fieldState.setViewTrue("status");

            // スタータスが無効
            if (Status.INVALID.getCodeValue().equals(record.getStatus())) {
                fieldState.setDisabledTrueAll();
            }
        }

        // 参照
        else if (Constants.OPERATION.VIEW.equals(operation)) {
            fieldState.setViewTrueAll();
        }

        return fieldState;
    }

    /**
     * ユーザIDからユーザ名を返すメソッド
     * @param userId ユーザID
     * @return ユーザ名
     */
    String getUserName(String userId) {
        return null;
    }

    /**
     * ロールから公開区分のSetを返すメソッド
     * @param loggedInUser ユーザ情報
     * @return 公開区分
     */
    Set<String> getPublicScope(LoggedInUser loggedInUser) {
        Set<String> setScope = new HashSet<>();
        if (loggedInUser.getAuthorities().contains(new SimpleGrantedAuthority("DOC_VIEW_ALL"))){
            setScope.add("10");
            setScope.add("20");
            setScope.add("99");
        }

        return setScope;
    }
}