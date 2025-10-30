package com.example.vaadin.session;

import com.vaadin.flow.spring.annotation.VaadinSessionScope;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * ポータル全体で利用するユーザーセッション管理クラス。
 *
 * 旧クライアント構成での static 変数の問題点：
 * - static変数は全ユーザー共通のため、複数ユーザーが同時にアクセスするとデータが混在
 * - スレッドセーフではない
 *
 * Vaadinの解決策：
 * - @VaadinSessionScope を使用してユーザーごとにインスタンスを作成
 * - Serializableを実装してセッションに保存可能にする
 */
@Component
@VaadinSessionScope  // ユーザーのセッションごとに1つのインスタンスを作成
public class PortalUserSession implements Serializable {

    private static final long serialVersionUID = 1L;

    // ユーザー情報
    private String userName;
    private String userId;

    private final Map<String, Object> sessionData = new HashMap<>();

    /**
     * 任意のキー/値ペアをセッションに保持する。
     * 既存キーがある場合は新しい値で上書きされる。
     */
    public void setAttribute(String key, Object value) {
        sessionData.put(key, value);
    }

    /**
     * 型情報を指定してセッションデータを取得する。
     * 要求した型と値の型が一致しない場合はnullを返す。
     */
    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key, Class<T> type) {
        Object value = sessionData.get(key);
        if (value != null && type.isInstance(value)) {
            return (T) value;
        }
        return null;
    }

    /**
     * 指定したキーに紐づくデータだけを削除する。
     * 他のセッションデータは保持されるためポイント的なクリアに利用する。
     */
    public void removeAttribute(String key) {
        sessionData.remove(key);
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "PortalUserSession{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", sessionDataCount=" + sessionData.size() +
                '}';
    }
}
