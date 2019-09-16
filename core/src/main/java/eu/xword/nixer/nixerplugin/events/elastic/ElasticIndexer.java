package eu.xword.nixer.nixerplugin.events.elastic;

import eu.xword.nixer.nixerplugin.events.BlockEvent;
import eu.xword.nixer.nixerplugin.events.JsonSerializer;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationListener;

public class ElasticIndexer implements ApplicationListener<BlockEvent> {

    private final Log logger = LogFactory.getLog(getClass());

    private JestClient jestClient;

    private String index;
    private String type;

    public ElasticIndexer(JestClient jestClient, String index, String type) {
        this.jestClient = jestClient;
        this.index = index;
        this.type = type;
    }

    @Override
    public void onApplicationEvent(final BlockEvent event) {

        final Index index = prepareRequest(event);

        jestClient.executeAsync(index, new JestResultHandler<DocumentResult>() {
            @Override
            public void completed(final DocumentResult result) {
            }

            @Override
            public void failed(final Exception ex) {
                logger.warn("Failed indexing event " + event, ex);
            }
        });
    }

    private Index prepareRequest(BlockEvent event) {
        final JsonSerializer jsonSerializer = new JsonSerializer();

        event.accept(jsonSerializer);

        //TODO set id consider event hash

        return new Index.Builder(jsonSerializer.toString())
                .index(this.index)
                .type(this.type)
                .build();
    }
}
