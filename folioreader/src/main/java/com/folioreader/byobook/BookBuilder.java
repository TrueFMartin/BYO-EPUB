package com.folioreader.byobook;

import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;

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

    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    private void createFile(Uri pickerInitialUri) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "invoice.pdf");

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, pickerInitialUri);

//        startActivityForResult(intent, CREATE_FILE);
    }
}