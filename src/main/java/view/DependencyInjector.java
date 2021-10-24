package view;

import com.google.inject.Guice;

public enum DependencyInjector {
    INSTANCE;

    public com.google.inject.Injector injector = Guice.createInjector(new BasicModule());
}
