package org.bric.gui.state;

import java.util.Properties;

public interface StatefulComponent {

    Properties saveState(Properties properties);

    void restoreState(Properties properties);
}
