/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package saulmm.avengers.injector.modules;

import dagger.Module;
import dagger.Provides;
import saulmm.avengers.CharacterDetailsUsecase;
import saulmm.avengers.GetCollectionUsecase;
import saulmm.avengers.injector.Activity;
import saulmm.avengers.repository.CharacterRepository;

@Module
public class AvengerInformationModule {
    private final int mCharacterId;

    public AvengerInformationModule(int characterId) {
        mCharacterId = characterId;
    }

    @Provides @Activity CharacterDetailsUsecase provideGetCharacterInformationUsecase (CharacterRepository repository) {
        return new CharacterDetailsUsecase(mCharacterId, repository);
    }

    @Provides @Activity GetCollectionUsecase provideGetCharacterComicsUsecase (CharacterRepository repository) {
        return new GetCollectionUsecase(mCharacterId, repository);
    }
}
