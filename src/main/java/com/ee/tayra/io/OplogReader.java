package com.ee.tayra.io;

import com.ee.tayra.domain.MongoCollection;
import com.ee.tayra.domain.MongoCollectionIterator;

public class OplogReader implements CollectionReader {

  private MongoCollectionIterator<String> iterator;

  public OplogReader(final MongoCollection collection,
    final String fromDocument, final boolean tailable) {
    iterator = collection.find(fromDocument, tailable);
  }

  @Override
  public final boolean hasDocument() {
    if (iterator == null) {
        throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    return iterator.hasNext();
  }

  @Override
  public final String readDocument() {
    if (iterator == null) {
      throw new ReaderAlreadyClosed("Reader Already Closed");
    }
    return iterator.next();
  }

  public final void close() {
    iterator.close();
    iterator = null;
  }

}