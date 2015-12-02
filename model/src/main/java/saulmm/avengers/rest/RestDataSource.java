/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package saulmm.avengers.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import java.util.List;
import javax.inject.Inject;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import retrofit.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.functions.Func1;
import saulmm.avengers.entities.CollectionItem;
import saulmm.avengers.entities.MarvelCharacter;
import saulmm.avengers.repository.Repository;
import saulmm.avengers.rest.exceptions.ServerErrorException;
import saulmm.avengers.rest.exceptions.UknownErrorException;
import saulmm.avengers.rest.utils.deserializers.MarvelResultsDeserializer;
import saulmm.avengers.rest.utils.interceptors.MarvelSigningIterceptor;

import static com.squareup.okhttp.logging.HttpLoggingInterceptor.*;
import static saulmm.avengers.entities.CollectionItem.COMIC;
import static saulmm.avengers.entities.CollectionItem.EVENT;
import static saulmm.avengers.entities.CollectionItem.SERIES;
import static saulmm.avengers.entities.CollectionItem.STORY;

public class RestDataSource implements Repository {

    private final MarvelApi mMarvelApi;
    public final static int MAX_ATTEMPS = 3;

    @Inject
    public RestDataSource() {
        OkHttpClient client = new OkHttpClient();

        MarvelSigningIterceptor signingIterceptor =
            new MarvelSigningIterceptor("74129ef99c9fd5f7692608f17abb88f9", "281eb4f077e191f7863a11620fa1865f2940ebeb");

        HttpLoggingInterceptor logginInterceptor = new HttpLoggingInterceptor();
        logginInterceptor.setLevel(Level.BODY);

        client.interceptors().add(signingIterceptor);
        client.interceptors().add(logginInterceptor);

        Gson customGsonInstance = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<List<Character>>() {}.getType(),
                new MarvelResultsDeserializer<Character>())

            .registerTypeAdapter(new TypeToken<List<CollectionItem>>() {}.getType(),
                new MarvelResultsDeserializer<CollectionItem>())

            .create();

        Retrofit marvelApiAdapter = new Retrofit.Builder()
            .baseUrl(MarvelApi.END_POINT)
            .addConverterFactory(GsonConverterFactory.create(customGsonInstance))
            .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
            .client(client)
            .build();

        mMarvelApi =  marvelApiAdapter.create(MarvelApi.class);
    }

	@Override
    public Observable<MarvelCharacter> getCharacter(final int characterId) {
           return mMarvelApi.getCharacterById(characterId)
               .flatMap(new Func1<List<MarvelCharacter>, Observable<MarvelCharacter>>() {
                   @Override public Observable<MarvelCharacter> call(List<MarvelCharacter> characters) {
                       return Observable.just(characters.get(0));
                   }
               });

	}

    @Override
    public Observable<List<MarvelCharacter>> getCharacters(int currentOffset) {
        return mMarvelApi.getCharacters(currentOffset)
            .onErrorResumeNext(new Func1<Throwable, Observable<? extends List<MarvelCharacter>>>() {
                @Override
                public Observable<? extends List<MarvelCharacter>> call(Throwable throwable) {
                    boolean serverError = throwable.getMessage().equals(HttpErrors.SERVER_ERROR);
                    return Observable.error(
                        (serverError) ? new ServerErrorException() : new UknownErrorException());
                }
            });
    }

    @Override
    public Observable<List<CollectionItem>> getCharacterCollection(int characterId, String type) {
        if (!type.equals(COMIC) && !type.equals(EVENT) && !type.equals(SERIES) && !type.equals(STORY))
            throw new IllegalArgumentException("Collection type must be: events|series|comics|stories");

        return mMarvelApi.getCharacterCollection(characterId, type);
    }
}
