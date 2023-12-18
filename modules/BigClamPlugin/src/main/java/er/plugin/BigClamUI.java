package er.plugin;

import javax.swing.JPanel;
import org.gephi.statistics.spi.Statistics;
import org.gephi.statistics.spi.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mir Saman Tajbakhsh
 */
@ServiceProvider(service = StatisticsUI.class)
public class BigClamUI implements StatisticsUI {

    private BigClamPanel panel;
    private BigClam myCliqueDetector;

    @Override
    public JPanel getSettingsPanel() {
        panel = new BigClamPanel();
        return panel;
    }

    @Override
    public void setup(Statistics statistics) {

    }

    @Override
    public void unsetup() {

    }

    @Override
    public Class<? extends Statistics> getStatisticsClass() {
        return null;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Clique Percolation Method";
    }

    @Override
    public String getShortDescription() {
        return "Clique Percolation Method implementaion in gephi";
    }

    @Override
    public String getCategory() {
        return CATEGORY_NETWORK_OVERVIEW;
    }

    @Override
    public int getPosition() {
        return 800;
    }

}
