/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package saulmm.avengers.injector;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import saulmm.avengers.AvengersApplication;
import saulmm.avengers.BuildConfig;
import saulmm.avengers.repository.CharacterRepository;
import saulmm.avengers.rest.Endpoint;
import saulmm.avengers.rest.MarvelAuthorizer;
import saulmm.avengers.rest.RestDataSource;

@Module
public class AppModule {
    private final AvengersApplication mAvengersApplication;

    public AppModule(AvengersApplication avengersApplication) {
        this.mAvengersApplication = avengersApplication;
    }

    @Provides @Singleton
    AvengersApplication provideAvengersApplicationContext() {
        return mAvengersApplication; }

    @Provides
    MarvelAuthorizer provideMarvelAuthorizer() {
        return new MarvelAuthorizer(BuildConfig.MARVEL_PUBLIC_KEY, BuildConfig.MARVEL_PRIVATE_KEY);
    }

    @Provides
    Endpoint provideRestEndpoint() {
        return new Endpoint("http://gateway.marvel.com/");
    }

    @Provides @Singleton
    CharacterRepository provideDataRepository(RestDataSource restDataSource) {
        return restDataSource; }
}
