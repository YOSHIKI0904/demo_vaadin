package com.example.vaadin.services;

import com.example.vaadin.model.GeneralAffairsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 総務部門向け依頼データの保存・取得を担当するサービス。
 * <p>
 * 実運用ではデータベースや外部APIと連携させるが、このサンプルはメモリ上に履歴を残すのみとする。
 */
@Service
public class GeneralAffairsRequestService {

    private static final Logger log = LoggerFactory.getLogger(GeneralAffairsRequestService.class);

    private final CopyOnWriteArrayList<SubmissionRecord> submissionLogs = new CopyOnWriteArrayList<>();

    /**
     * 申請内容を登録し、簡易的な履歴として保持する。
     *
     * @param request 画面で入力された申請情報
     */
    public void submit(GeneralAffairsRequest request) {
        submissionLogs.add(new SubmissionRecord(LocalDateTime.now(), request));
        log.info("General affairs request submitted. applicantId={}, requestType={}",
            request.getApplicantId(), request.getRequestType());
    }

    /**
     * 履歴の最新5件を取得する。
     *
     * @return 登録順の新しい順で最大5件
     */
    public List<SubmissionRecord> findLatest() {
        int size = submissionLogs.size();
        if (size == 0) {
            return Collections.emptyList();
        }
        int fromIndex = Math.max(0, size - 5);
        List<SubmissionRecord> copy = new ArrayList<>(submissionLogs.subList(fromIndex, size));
        Collections.reverse(copy); // 新しいものを先頭に並べる
        return copy;
    }

    /**
     * 申請登録のメタデータ。
     */
    public record SubmissionRecord(LocalDateTime submittedAt, GeneralAffairsRequest request) {
    }
}
