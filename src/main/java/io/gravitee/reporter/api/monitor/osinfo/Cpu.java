package io.gravitee.reporter.api.monitor.osinfo;

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
public class Cpu {
    private short percent = -1;
    private List<Double> loadAverage = new LinkedList<>();
}