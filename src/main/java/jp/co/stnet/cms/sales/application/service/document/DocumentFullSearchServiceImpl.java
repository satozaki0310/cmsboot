package jp.co.stnet.cms.sales.application.service.document;

import jp.co.stnet.cms.base.domain.model.variable.Variable;
import jp.co.stnet.cms.common.util.StringUtils;
import jp.co.stnet.cms.sales.domain.model.document.DocumentIndex;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ja.JapaneseTokenizer;
import org.apache.lucene.analysis.ja.tokenattributes.BaseFormAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.InflectionAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.PartOfSpeechAttribute;
import org.apache.lucene.analysis.ja.tokenattributes.ReadingAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.*;
import org.hibernate.search.backend.lucene.LuceneBackend;
import org.hibernate.search.engine.backend.Backend;
import org.hibernate.search.engine.backend.index.IndexManager;
import org.hibernate.search.engine.search.aggregation.AggregationKey;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.mapping.SearchMapping;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class DocumentFullSearchServiceImpl implements DocumentFullSearchService {

    private static final String sort1 = "lastModifiedDate";
    private static final String Sort2 = "title";
    private static final String Sort3 = "docCategory1";

    @PersistenceContext
    EntityManager entityManager;

    /**
     * 入力フォームに入力した内容で全文検索を行う
     * ① 入力内容を形態素解析する
     * ② ①の値を用いて全文検索
     * where: ①の結果がcontentにマッチしたデータ and 閲覧権限がある
     * aggregation: 指定したフィールドのジャンル別の数
     * sort: sort項目の内容
     * .fetch: ページの表示数
     *
     * @param term     　入力フォームに入力した内容
     * @param period
     * @param sort
     * @param pageable 　ページ情報
     * @return
     */
    @Override
    public SearchResult<DocumentIndex> search(String term, String period, String sort, List<String> facets, Pageable pageable) {
        SearchSession searchSession = Search.session(entityManager);

        List<String> tokens = analyze(term);
        System.out.println(StringUtils.join(tokens, " + "));

        AggregationKey<Map<String, Long>> countsByDocCategory1 = AggregationKey.of("countsByDocCategory1");
        AggregationKey<Map<String, Long>> countsByDocCategory2 = AggregationKey.of("countsByDocCategory2");
        AggregationKey<Map<String, Long>> countsByDocService1 = AggregationKey.of("countsByDocService1");
        AggregationKey<Map<String, Long>> countsByDocService2 = AggregationKey.of("countsByDocService2");
        AggregationKey<Map<String, Long>> countsByDocService3 = AggregationKey.of("countsByDocService3");

        int pageSize = 5;
        long offset = 0;

        if (pageable != null) {
            pageSize = pageable.getPageSize();
            offset = pageable.getOffset();
        }

        SearchResult<DocumentIndex> result = searchSession.search(DocumentIndex.class)
                .where(
                        f -> f.simpleQueryString()
                                .field("content")
                                .matching(StringUtils.join(tokens, " + "))
                )
                .aggregation(countsByDocCategory1, f -> f.terms()
                        .field("docCategory1", String.class))
                .aggregation(countsByDocCategory2, f -> f.terms()
                        .field("docCategory2", String.class))
                .aggregation(countsByDocService1, f -> f.terms()
                        .field("docService1", String.class))
                .aggregation(countsByDocService2, f -> f.terms()
                        .field("docService2", String.class))
                .aggregation(countsByDocService3, f -> f.terms()
                        .field("docService3", String.class))
                .sort(f -> f.field(sort).desc())

//                .sort(
//                        f -> {
//                            if (sort.equals(sort1)) {
//                                f.field(sort);
//                            }
//                            return f.field(sort);
//                        }
//                    )


                .fetch((int) offset, pageSize);

        return result;
    }

    /**
     * textからtermの箇所を検索し、その前後の文字を抽出する
     *
     * @param text ヒットした添付ファイルの内容(content)
     * @param term 入力フォームに入力した内容
     * @return ヒットした箇所前後の文字列
     */
    @Override
    public String highlight(String text, String term) {
        try {


            SearchMapping mapping = Search.mapping(entityManager.getEntityManagerFactory());
            IndexManager indexManager = mapping.indexManager("DocumentIndex");
            Backend backend = mapping.backend();
            LuceneBackend luceneBackend = backend.unwrap(LuceneBackend.class);
            Analyzer analyzer = luceneBackend.analyzer("japanese").orElseThrow(() -> new IllegalStateException());
//
//            Analyzer analyzer2 = new JapaneseAnalyzer();
            QueryParser queryParser = new QueryParser("content", analyzer);
//            queryParser.setAllowLeadingWildcard(true);
            Query query = queryParser.parse(term);
            QueryScorer scorer = new QueryScorer(query, "content");
            Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
            Highlighter highlighter = new Highlighter(formatter, scorer);

            Fragmenter fragmenter = new SimpleSpanFragmenter(scorer);
            highlighter.setTextFragmenter(fragmenter);

            TokenStream stream = analyzer.tokenStream("content", text);
//
            String[] frags = highlighter.getBestFragments(stream, text, 1);


            String fragsString = "";
            for (String f : frags) {
                fragsString = fragsString + f + " ";
            }

            return fragsString;

            //
//
        } catch (IOException | ParseException | InvalidTokenOffsetsException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 日本語形態素解析を行う
     *
     * @param q 入力フォームに入力した内容
     * @return 形態素解析の結果(List)
     */
    private List<String> analyze(String q) {

        List<String> tokens = new ArrayList<>();

        JapaneseTokenizer tokenizer = new JapaneseTokenizer(null, false, JapaneseTokenizer.Mode.NORMAL);

        CharTermAttribute term = tokenizer.addAttribute(CharTermAttribute.class);
        OffsetAttribute offset = tokenizer.addAttribute(OffsetAttribute.class);
        PartOfSpeechAttribute partOfSpeech = tokenizer.addAttribute(PartOfSpeechAttribute.class);
        InflectionAttribute inflection = tokenizer.addAttribute(InflectionAttribute.class);
        BaseFormAttribute baseForm = tokenizer.addAttribute(BaseFormAttribute.class);
        ReadingAttribute reading = tokenizer.addAttribute(ReadingAttribute.class);

        tokenizer.setReader(new StringReader(q));
        try {
            tokenizer.reset();
            while (tokenizer.incrementToken()) {

                tokens.add(term.toString());
                System.out.println(term + "\t" // 表層形
                        + offset.startOffset() + "-" + offset.endOffset() + "," // 文字列中の位置
                        + partOfSpeech.getPartOfSpeech() + "," // 品詞-品詞細分類1-品詞細分類2
                        + inflection.getInflectionType() + "," // 活用型
                        + inflection.getInflectionForm() + "," // 活用形
                        + baseForm.getBaseForm() + "," // 原形 (活用しない語では null)
                        + reading.getReading() + "," // 読み
                        + reading.getPronunciation()); // 発音
            }
            tokenizer.close();

        } catch (IOException e) {
            e.printStackTrace();
            // TODO throw new Exception
        }

        return tokens;
    }

    /**
     * ジャンル別に集約したデータからコードを抜き出し、コードで検索する
     *
     * @param text ジャンル別に集約したコード
     * @return
     */
    @Override
    public List<Variable> label(Map<String, Long> text) {
        // Mapのコードの値だけをリストに格納する
        List<String> code = new ArrayList<>();
        //variableRepository.findByCode(code);
        return null;
    }
}
