package io.gravitee.reporter.api.monitor.jvminfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemoryPool {
	private String name;
	private long used;
	private long max;

	private long peakUsed;
	private long peakMax;
}