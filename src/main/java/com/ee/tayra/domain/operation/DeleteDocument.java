/*******************************************************************************
 * Copyright (c) 2013, Equal Experts Ltd
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation
 * are those of the authors and should not be interpreted as representing
 * official policies, either expressed or implied, of the Tayra Project.
 ******************************************************************************/
package com.ee.tayra.domain.operation;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.WriteResult;

class DeleteDocument extends Operation {

  private final MongoClient mongo;

  public DeleteDocument(final MongoClient mongo) {
    this.mongo = mongo;
  }

  @Override
  protected final void doExecute(final DBObject document) {
    final String ns = (String) document.get("ns");
    int index = ns.indexOf(".");
    if (index != -1) {
      String dbName = ns.substring(0, index);
      String collectionName = ns.substring(index + 1, ns.length());
      DBObject deleteSpec = (DBObject) document.get("o");
      WriteResult writeResult = mongo.getDB(dbName)
                                     .getCollection(collectionName)
                                     .remove(deleteSpec);
      int deletedObjCount = writeResult.getN();
      if (deletedObjCount == 0) {
        throw new OperationFailed("Document does not exist "
              + deleteSpec.toString());
      }
    }
  }
}
