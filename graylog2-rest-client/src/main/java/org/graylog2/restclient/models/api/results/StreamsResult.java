/**
 * This file is part of Graylog.
 *
 * Graylog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog2.restclient.models.api.results;

import java.util.List;

import com.google.common.collect.Lists;

import javax.inject.Inject;
import org.graylog2.restclient.models.Stream;
import org.graylog2.restclient.models.StreamService;
import org.graylog2.restclient.models.api.responses.streams.StreamSummaryResponse;

public class StreamsResult {

	private final int total;
	private final List<StreamSummaryResponse> streams;

    private final Stream.Factory streamFactory;

    @Inject
	public StreamsResult(Stream.Factory streamFactory, int total, List<StreamSummaryResponse> streams) {
		this.total = total;
		this.streams = streams;
        this.streamFactory = streamFactory;
	}

	public int getTotal() {
		return total;
	}
	
	public List<StreamSummaryResponse> getStreams() {
		/*List<Stream> streams = Lists.newArrayList();
		
		for (StreamSummaryResponse ssr : this.streams) {
            streams.add(streamFactory.fromSummaryResponse(ssr));
		}

		return streams;*/
        return streams;
	}

    /*public static StreamsResult mergeResults(List<StreamsResult> streamsResults) {
        int total = 0;
        List<StreamSummaryResponse> streams = Lists.newArrayList();

        for (StreamsResult streamsResult : streamsResults) {
            total += streamsResult.total;
            streams.addAll(streamsResult.streams);
        }

        return new StreamsResult(total, streams);
    }*/
}
