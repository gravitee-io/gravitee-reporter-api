package io.gravitee.reporter.api.monitor.osinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Swap {
    private long total = -1;
    private long free = -1;
}