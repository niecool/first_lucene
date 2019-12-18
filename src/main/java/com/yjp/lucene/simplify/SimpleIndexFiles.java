/**
 * Copyright  2019 北京易酒批电子商务有限公司. All rights reserved.
 */
package com.yjp.lucene.simplify;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * SimpleIndexFiles
 *
 * @author zhaochengye
 * @date 2019/12/18
 */
public class SimpleIndexFiles {

    public static void main(String[] args) throws IOException {
        String indexPath = "C:\\lucene\\index";
        String docsPath = "C:\\lucene\\data";

        //1.create directory
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        //2.create config
        IndexWriterConfig iwc = new IndexWriterConfig(new StandardAnalyzer());
        iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        //3.create indexWriter
        IndexWriter writer = new IndexWriter(dir, iwc);
        final Path docDir = Paths.get(docsPath);
        //4.write index
        indexDocs(writer, docDir);
        //5.关闭流
        writer.close();
    }

    /**
     * write index
     */
    static void indexDocs(final IndexWriter writer, Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                try {
                    indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                } catch (IOException ignore) {
                    // don't index files that can't be read.
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException {
        try (InputStream stream = Files.newInputStream(file)) {
            //1.create document
            Document doc = new Document();
            //2.add field
            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))));
            //3.add document
            writer.addDocument(doc);
        }
    }
}