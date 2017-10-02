package io.gravitee.reporter.api.monitor.jvminfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GarbageCollector {
    private String name;
    private long collectionCount;
    private long collectionTime;
}