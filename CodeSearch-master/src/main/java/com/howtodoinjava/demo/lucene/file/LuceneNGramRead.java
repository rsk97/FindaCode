package com.howtodoinjava.demo.lucene.file;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class LuceneNGramRead {
    //directory contains the lucene indexes
    private static final String INDEX_DIR = "indexedFiles";

    public void LuceneNGramReadmain(String texttofind) throws Exception
    {
        //Create lucene searcher. It search over a single IndexReader.
        IndexSearcher searcher = createSearcher();

        //Search indexed contents using search term
        TopDocs foundDocs = searchInContent(texttofind, searcher);

        //Total found documents
        System.out.println("Total Results :: " + foundDocs.totalHits);

        //Let's print out the path of files which have searched term
        for (ScoreDoc sd : foundDocs.scoreDocs)
        {
            Document d = searcher.doc(sd.doc);
            System.out.println("Path : "+ d.get("path") + ", Score : " + sd.score);
        }
    }

    private static TopDocs searchInContent(String textToFind, IndexSearcher searcher) throws Exception
    {
        //Create search query
        QueryParser qp = new QueryParser("contents", new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String s) {
                Tokenizer source = new NGramTokenizer(LuceneNGramWrite.MIN_N_GRAMS, LuceneNGramWrite.MAX_N_GRAMS);
                //TokenStream filter = new NGramTokenFilter(source, LuceneNGramWrite.MIN_N_GRAMS, LuceneNGramWrite.MAX_N_GRAMS);
                TokenStream firstfilter = new LowerCaseFilter(source);
                TokenStream filter = new SnowballFilter(firstfilter, "English");
                return new TokenStreamComponents(source, filter);
            }
        });
        Query query = qp.parse(textToFind);

        //search the index
        TopDocs hits = searcher.search(query, 10);
        return hits;
    }

    private static IndexSearcher createSearcher() throws IOException
    {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        //It is an interface for accessing a point-in-timinputFiles/fibonacci_search.ce view of a lucene index
        IndexReader reader = DirectoryReader.open(dir);

        IndexSearcher searcher = new IndexSearcher(reader);
        return searcher;
    }
}
