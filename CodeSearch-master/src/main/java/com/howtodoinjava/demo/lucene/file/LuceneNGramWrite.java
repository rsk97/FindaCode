package com.howtodoinjava.demo.lucene.file;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.ngram.NGramTokenizer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class LuceneNGramWrite {
    //minimum number of N-Grams
    static int MIN_N_GRAMS = 3;
    //Maximum Number of N-Grams
    static int MAX_N_GRAMS = 5;

    public  void LuceneNGramWritemain()
    {
        //Input folder
        String docsPath = "inputFiles";

        //Output folder
        String indexPath = "indexedFiles";


        //Input Path Variable
        final Path docDir = Paths.get(docsPath);

        try
        {
            //org.apache.lucene.store.Directory instance
            Directory dir = FSDirectory.open( Paths.get(indexPath) );

            //analyzer with the default stop words
            //Analyzer analyzer = new StandardAnalyzer();

            //Using new Analyzer to create a N-Gram Tokenizer to create a n-gram index.
            // NGramTokenizer generates all tokens with MIN_N_GRAMS to MAX_N_GRAMS.
            Analyzer analyzer = new Analyzer() {
                @Override
                protected TokenStreamComponents createComponents(String s) {
                    Tokenizer source = new NGramTokenizer(MIN_N_GRAMS, MAX_N_GRAMS);
                    TokenStream firstfilter = new LowerCaseFilter(source);
                    TokenStream filter = new SnowballFilter(firstfilter, "English");
                    return new TokenStreamComponents(source, filter);

                }
            };

            //IndexWriter Configuration
            IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

            //IndexWriter writes new index files to the directory
            IndexWriter writer = new IndexWriter(dir, iwc);

            //Its recursive method to iterate all files and directories
            indexDocs(writer, docDir);

            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static void indexDocs(final IndexWriter writer, Path path) throws IOException
    {
        //Directory?
        if (Files.isDirectory(path))
        {
            //Iterate directory
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException
                {
                    try
                    {
                        //Index this file
                        indexDoc(writer, file, attrs.lastModifiedTime().toMillis());
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        else
        {
            //Index this file
            indexDoc(writer, path, Files.getLastModifiedTime(path).toMillis());
        }
    }

    static void indexDoc(IndexWriter writer, Path file, long lastModified) throws IOException
    {

       // BufferedReader in = new BufferedReader(new FileReader(file.toString()));
       // String line;
        //while((line = in.readLine()) != null)
        //{
         //   System.out.println(line);
        //}
        //in.close();
        try (InputStream stream = Files.newInputStream(file))
        {
            //Create lucene Document
            Document doc = new Document();
            // System.out.println(file.toString());
            doc.add(new StringField("path", file.toString(), Field.Store.YES));
            doc.add(new LongPoint("modified", lastModified));
            doc.add(new TextField("contents", new String(Files.readAllBytes(file)), Field.Store.YES));

            //Updates a document by first deleting the document(s)
            //containing <code>term</code> and then adding the new
            //document.  The delete and then add are atomic as seen
            //by a reader on the same index
            writer.updateDocument(new Term("path", file.toString()), doc);
        }
    }
}
