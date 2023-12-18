package er.plugin;

import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsBuilder;
import org.openide.util.lookup.ServiceProvider;

// @author Ebrahim Razmdideh

@ServiceProvider (service = StatisticsBuilder.class)
public class BigClamBuilder implements StatisticsBuilder {
    @Override
    public String getName() {
        return "Big Clam";
    }

    @Override
    public Statistics getStatistics() {
        return (Statistics) new BigClam();
    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return (Class<? extends Statistics>) BigClam.class;
    }
}
