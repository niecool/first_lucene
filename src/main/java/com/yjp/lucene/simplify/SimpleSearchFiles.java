/**
 * Copyright  2019 北京易酒批电子商务有限公司. All rights reserved.
 */
package com.yjp.lucene.simplify;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * SimpleSearchFiles
 *
 * @author zhaochengye
 * @date 2019/12/18
 */
public class SimpleSearchFiles {

    public static void main(String[] args) throws IOException, ParseException {
        String indexPath = "C:\\lucene\\index";
        String searchKey = "mozilla";

        //1.create reader
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexPath)));
        //2.create searcher
        IndexSearcher searcher = new IndexSearcher(reader);
        //3.create query
        QueryParser parser = new QueryParser("contents", new StandardAnalyzer());
        Query query = parser.parse(searchKey);
        //4.search
        TopDocs results = searcher.search(query, 10);
        printResult(results,searcher);


    }

    static void printResult(TopDocs results, IndexSearcher searcher) throws IOException {
        ScoreDoc[] hits = results.scoreDocs;
        int numTotalHits = results.totalHits;
        for (int i = 0; i < numTotalHits; i++) {
            Document doc = searcher.doc(hits[i].doc);
            System.out.println("score:["+hits[i].score+"]    path:["+doc.get("path")+"]");
        }
    }
}