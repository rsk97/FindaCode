package com.howtodoinjava.demo.lucene.file;

public class mainclass {

    public static void main(String[] args)
    {
        String query="search";

        String wq="*search";
        LuceneWriteIndexFromFileExample luceneWriteIndexFromFileExample=new LuceneWriteIndexFromFileExample();
        luceneWriteIndexFromFileExample.LuceneWriteIndexFromFileExamplemain();
        System.out.println("Created Inverted Index from Regular Files.\n");
        LuceneReadIndexFromFileExample luceneReadIndexFromFileExample=new LuceneReadIndexFromFileExample();

        try {
            luceneReadIndexFromFileExample.LuceneReadIndexFromFileExamplemain(query);
        }
        catch (Exception e)
        {
            ;
        }

        System.out.println("Wildcard Query:\n");
        Wildcardquery wildcardquery=new Wildcardquery();

        try {
            wildcardquery.Wildcardquerymain(wq);
        }
        catch (Exception e)
        {
            ;
        }

        System.out.println();
        //System.out.println("NGRAM INDEXING");
        System.out.println();

        LuceneNGramWrite luceneNGramWrite=new LuceneNGramWrite();
        luceneNGramWrite.LuceneNGramWritemain();
        System.out.println("Created N Gram Inverted Index using input files.\n");
        LuceneNGramRead luceneNGramRead=new LuceneNGramRead();
        try {

            luceneNGramRead.LuceneNGramReadmain(query);

        }
        catch (Exception e){
            ;
        }

    }
}
