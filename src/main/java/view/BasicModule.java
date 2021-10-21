package view;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import game.Config;
import net.Node;

public class BasicModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Node.class).in(Singleton.class);
    }
}
