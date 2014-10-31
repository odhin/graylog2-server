package org.graylog2.benchmarks.pipeline;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventTranslator;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InputBuffer {

    private final RingBuffer<Event> ringBuffer;

    @AssistedInject
    public InputBuffer(MetricRegistry metricRegistry,
                       FilterHandler.Factory handlerFactory,
                       @Assisted OutputBuffer outputBuffer,
                       @Assisted("bufferSize") int bufferSize,
                       @Assisted("numFilterHander") int numFilterHandler) {
        final ExecutorService executor = Executors.newCachedThreadPool(
                new ThreadFactoryBuilder()
                        .setNameFormat("inputbuffer-%d")
                        .build()
        );

        final Disruptor<Event> disruptor = new Disruptor<>(
                Event.FACTORY,
                bufferSize,
                executor,
                ProducerType.MULTI,
                new BlockingWaitStrategy()
        );

        final FilterHandler[] handlers = new FilterHandler[numFilterHandler];
        for (int i = 0; i < numFilterHandler; i++) {
            handlers[i] = handlerFactory.create(outputBuffer, i, numFilterHandler);
        }

        disruptor.handleEventsWith(handlers);

        ringBuffer = disruptor.start();

        metricRegistry.register("input-buffer-remaining", new Gauge<Long>() {
            @Override
            public Long getValue() {
                return ringBuffer.remainingCapacity();
            }
        });
    }

    public void publish(final ProcessedMessage processedMessage) {
        ringBuffer.publishEvent(new EventTranslator<Event>() {
            @Override
            public void translateTo(Event event, long sequence) {
                event.message = processedMessage;
            }
        });
    }

    public interface Factory {
        InputBuffer create(@Assisted OutputBuffer outputBuffer, @Assisted("bufferSize") int bufferSize, @Assisted("numFilterHander") int numFilterHandler);
    }
}