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
package com.ee.tayra.io.writer;

import com.ee.tayra.domain.operation.Operation;
import com.ee.tayra.domain.operation.OperationFailed;
import com.ee.tayra.domain.operation.OperationsFactory;

public class OplogReplayer implements Replayer {

  private final OperationsFactory operations;
  private WriteNotifier notifier;

  public OplogReplayer(final OperationsFactory operations) {
    this.operations = operations;
    notifier = WriteNotifier.NONE;
  }

  public final void setNotifier(final WriteNotifier notifier) {
    this.notifier = notifier;
  }

  @Override
  public void replay(final String document) {
    try {
      notifier.notifyWriteStart(document);
      final String operationCode = extractOpcode(document);
      Operation operation = operations.get(operationCode);
      operation.execute(document);
      notifier.notifyWriteSuccess(document);
    } catch (OperationFailed problem) {
      notifier.notifyWriteFailure(document, problem);
      if (problem.isConnectionLost()) {
        throw problem;
      }
    }
  }

  private String extractOpcode(final String document) {
    int opcodeStartIndex = document.indexOf("op") - 1;
    int opcodeEndIndex = document.indexOf(",", opcodeStartIndex);
    String opcodeSpec = document
        .substring(opcodeStartIndex, opcodeEndIndex);
    String quotedOpcode = opcodeSpec.split(":")[1];
    return quotedOpcode.replaceAll("\"", "").trim();
  }
}
