package lucenex;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.LowerCaseFilterFactory;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceTokenizerFactory;
import org.apache.lucene.analysis.custom.CustomAnalyzer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilterFactory;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.document.Field;


public class Main {

	public static void main(String[] args) throws IOException, ParseException {
		//addFileToIndex("C:\\Users\\renzi\\OneDrive\\Documents\\file\\Introduzione all'Intelligenza Artificiale.txt");
		//addFileToIndex("C:\\Users\\renzi\\OneDrive\\Documents\\file\\Storia della Rivoluzione Industriale.txt");
		//addFileToIndex("C:\\Users\\renzi\\OneDrive\\Documents\\file\\Il Cambiamento Climatico e le Sue Implicazioni.txt");
		System.out.print("inserire query:");
		try (Scanner scanner = new Scanner(System.in)) {
			String query = scanner.nextLine();
			eeseguiQuery("contenuto", query);
		}
		
	}
	
	public static void addFileToIndex(String filepath) throws IOException {

	    Path path = Paths.get(filepath);
	    File file = path.toFile();
	    
	    Analyzer analyzer = CustomAnalyzer.builder()
	    		 .withTokenizer(WhitespaceTokenizerFactory.class)
	    		 .addTokenFilter(LowerCaseFilterFactory.class)
	    		 .addTokenFilter(WordDelimiterGraphFilterFactory.class)
	    		 .build();
	    IndexWriterConfig indexWriterConfig= new IndexWriterConfig(analyzer);
	    
		Directory indexDirectory = FSDirectory.open(Paths.get("opt/lucene-index"));
	    IndexWriter indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
	    Document document = new Document();

	    FileReader fileReader = new FileReader(file);
	    document.add(new TextField("contenuto", fileReader));
	    document.add(new StringField("titolo", file.getName(), Field.Store.YES));

	    indexWriter.addDocument(document);
	    indexWriter.close();
	}
	
	public static void eeseguiQuery(String inField, String queryString) throws ParseException, IOException {
	    Query query = new QueryParser(inField, new WhitespaceAnalyzer()).parse(queryString);
	    Directory indexDirectory = FSDirectory.open(Paths.get("opt/lucene-index"));
	    IndexReader indexReader = DirectoryReader.open(indexDirectory);
	    IndexSearcher searcher = new IndexSearcher(indexReader);
	    TopDocs topDocs = searcher.search(query, 10);
	    List<Document> docs= new ArrayList<Document>();
	    for (int i = 0; i < topDocs.scoreDocs.length; i++) {
	    	ScoreDoc scoreDoc = topDocs.scoreDocs[i];
	    	docs.add(searcher.doc(scoreDoc.doc));
	    }
	    for (int i = 0; i < docs.size(); i++) {
	    	System.out.print(docs.get(i)+"\n");
	    }
	}
}
