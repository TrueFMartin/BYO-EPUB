package com.folioreader.builder;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.regex.Pattern;

import io.documentnode.epub4j.domain.Author;
import io.documentnode.epub4j.domain.Book;
import io.documentnode.epub4j.domain.Metadata;
import io.documentnode.epub4j.domain.Resource;
import io.documentnode.epub4j.epub.EpubWriter;

public class BookBuilder {
    private static byte[] getResource(String path ) {
        return path.getBytes();
    }

    private InputStream getResourceFromFile(String path ) {
        return BookBuilder.class.getResourceAsStream( path );
    }
    private static Resource getResource( String data, String href ) {
        return new Resource( getResource( data ), href );
    }

    Book book;
    Metadata metadata;
    Pattern rgx = Pattern.compile("[^a-zA-Z0-9-]");
    public BookBuilder(String title) {
        this.book = new Book();
        this.metadata = this.book.getMetadata();
        this.metadata.addTitle(title);
    }

    public void setAuthor(String first, String last) {
        metadata.addAuthor(new Author(first, last));
    }

    // FIXME NOT USED YET
    public void setCoverImage(){
        book.setCoverImage(
                getResource("/book1/test_cover.png", "cover.png"));
        throw new RuntimeException();
    }

    public void addChapter(String chapterName, String chapterText) {
        book.addSection(chapterName,
                getResource(chapterText, rgx.matcher(chapterName.toLowerCase()).replaceAll("")
                + ".html"));
    }
    public void build(String fileName) {
        try {
            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();

            // Write the Book as Epub
            epubWriter.write(book, new FileOutputStream(fileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}