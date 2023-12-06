package com.folioreader.byobook;

import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;

import epub4j.domain.Author;
import epub4j.domain.Book;
import epub4j.domain.Metadata;
import epub4j.domain.Resource;
import epub4j.epub.EpubWriter;
public class BookBuilder {
    private static byte[] getResource(String path ) {
        return path.getBytes();
    }

    private InputStream getResourceFromFile(String path ) {
        return BookBuilder.class.getResourceAsStream( path );
    }
    private static Resource getResource(String data, String href ) {
        return new Resource( getResource( data ), href );
    }

    private Resource getResource(InputStream stream, String href) {
        try {
            return new Resource(stream, href);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public void setAuthor(String name) {
        metadata.addAuthor(new Author(name));
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
                + ".xhtml"));
    }
    public void build(String uri) {
        try {
            // Create EpubWriter
            EpubWriter epubWriter = new EpubWriter();
            Log.d("BookBuilder", "writing book" + uri);
            // Write the Book as Epub
            epubWriter.write(book, new FileOutputStream(uri));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void build(FileOutputStream stream) {
        EpubWriter epubWriter = new EpubWriter();
        try {
            epubWriter.write(book, stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setStyleSheet(InputStream resourceStream) {
        book.addResource(getResource(resourceStream, "epub.css"));
    }
}