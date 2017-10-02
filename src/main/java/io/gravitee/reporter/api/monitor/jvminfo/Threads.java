package io.gravitee.reporter.api.monitor.jvminfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Threads {
    private int count;
    private int peakCount;
}
