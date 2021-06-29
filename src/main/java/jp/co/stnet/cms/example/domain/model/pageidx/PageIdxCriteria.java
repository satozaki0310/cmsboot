package jp.co.stnet.cms.example.domain.model.pageidx;

import lombok.Data;

import java.time.LocalDate;
import java.util.Collection;

/**
 * ページ索引の検索条件格納用
 */
@Data
public class PageIdxCriteria {
    private String CustomerNumber;
    private String CustomerName;
    private Collection<String> shopCodes;
    private Collection<Integer> year;
    private Collection<Integer> period;
    private Collection<Integer> hatsujun;
    private String keyword1;
    private String keyword2;
    private String keyword3;
    private String keyword4;
    private String keyword5;
    private String keyword6;
    private String keyword7;
    private String keyword8;
    private String keyword9;
    private String keyword10;
    private LocalDate keydate1;
    private LocalDate keydate2;
    private LocalDate keydate3;
    private LocalDate keydate4;
    private LocalDate keydate5;
    private LocalDate keydate6;
    private LocalDate keydate7;
    private LocalDate keydate8;
    private LocalDate keydate9;
    private LocalDate keydate10;
}

