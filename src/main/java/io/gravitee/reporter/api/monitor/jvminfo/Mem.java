package io.gravitee.reporter.api.monitor.jvminfo;

import java.util.LinkedList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mem {
	private long heapCommitted;
	private long heapUsed;
	private long heapMax;
	private long nonHeapCommitted;
	private long nonHeapUsed;
	private List<MemoryPool> pools = new LinkedList<>();

	public short getHeapUsedPercent() {
		if (heapMax == 0) {
			return -1;
		}
		return (short) (heapUsed * 100 / heapMax);
	}
}