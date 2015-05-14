package saulmm.avengers.model.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import javax.inject.Inject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import rx.Observable;
import saulmm.avengers.model.Comic;
import saulmm.avengers.model.Repository;

public class RestRepository implements Repository {

    private final MarvelApi mMarvelApi;

    String publicKey    = "";
    String privateKey   = "";

    @Inject
    public RestRepository() {

        Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(new CharacterItemAdapterFactory())
            .create();

        RestAdapter marvelApiAdapter = new RestAdapter.Builder()
            .setEndpoint(MarvelApi.END_POINT)
            .setLogLevel(RestAdapter.LogLevel.HEADERS_AND_ARGS)
            .setRequestInterceptor(authorizationInterceptor)
            .setConverter(new GsonConverter(gson))
            .build();

        mMarvelApi = marvelApiAdapter.create(MarvelApi.class);
    }

    RequestInterceptor authorizationInterceptor = new RequestInterceptor() {
        @Override
        public void intercept(RequestFacade request) {

            String marvelHash = MarvelApiUtils.generateMarvelHash(publicKey, privateKey);
            request.addQueryParam(MarvelApi.PARAM_API_KEY, publicKey);
            request.addQueryParam(MarvelApi.PARAM_TIMESTAMP, MarvelApiUtils.getUnixTimeStamp());
            request.addQueryParam(MarvelApi.PARAM_HASH, marvelHash);
        }
    };

    @Override
    public Observable<saulmm.avengers.model.Character> getCharacter(int characterId) {
        return mMarvelApi.getCharacter(characterId);
    }

    @Override
    public Observable<List<Comic>> getCharacterComics(int characterId) {

        final String comicsFormat   = "comic";
        final String comicsType     = "comic";

        return mMarvelApi.getCharacterComics(characterId, comicsFormat, comicsType);
    }
}
