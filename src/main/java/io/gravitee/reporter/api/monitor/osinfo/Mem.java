package io.gravitee.reporter.api.monitor.osinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mem {
    private long total = -1;
    private long free = -1;


    public long getUsed() {
        return total - free;
    }

    public short getUsedPercent() {
        return calculatePercentage(getUsed(), getTotal());
    }

    public short getFreePercent() {
        return calculatePercentage(getFree(), getTotal());
    }
    
    private short calculatePercentage(long used, long max) {
        return max <= 0 ? 0 : (short) (Math.round((100d * used) / max));
    }
}