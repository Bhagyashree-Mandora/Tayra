package com.ee.tayra.io.reader.nio

import java.io.Reader
import java.util.Iterator
import com.ee.tayra.io.reader.nio.Chunk
import com.ee.tayra.io.reader.nio.Chunker
import com.ee.tayra.io.reader.nio.Chunker.ChunkIterator
import com.ee.tayra.io.reader.nio.Chunk.DocumentIterator
import com.ee.tayra.io.reader.DocumentReader
import com.ee.tayra.io.reader.ReadNotifier
import spock.lang.Ignore
import spock.lang.Specification


class MemoryMappedDocumentReaderSpecs extends Specification {

	private static final String NEW_LINE = System.getProperty('line.separator')
	private ReadNotifier mockNotifier
	private DocumentReader reader
	private final String document = "\"ts\""
	private Chunker mockChunker = Stub(Chunker)
	private Chunk mockedChunk = Stub(Chunk)
	private Iterator<Chunk> mockChunkIterator = Stub(ChunkIterator)
	private Iterator<String> mockDocIterator = Stub(DocumentIterator)
	private File file

	def setup() {
		mockNotifier = Mock(ReadNotifier)
		file = File.createTempFile('test', 'out')
		file.withWriter { writer ->
			writer.write document
			writer.write NEW_LINE
		}
		String bufferSize ='1KB'
		reader = new MemoryMappedDocumentReader(file.absolutePath, bufferSize)
		reader.notifier = mockNotifier
	}

	def cleanup() {
		reader.close()
		file.delete()
	}

	def readsADocument() {
		when: 'read is invoked'
		   String document = reader.readDocument()

		then: 'the document is read'
		    document == this.document
	}

	def notifiesBeforeStartingToReadADocument() {
		when: 'document is read'
			reader.readDocument()

		then: 'a notification of successful read is given'
			1 * mockNotifier.notifyReadStart("")
	}
}
