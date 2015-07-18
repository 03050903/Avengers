/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package saulmm.avengers.mvp.views;

import android.app.ActivityOptions;
import java.util.List;
import saulmm.avengers.model.entities.Character;

public interface AvengersView extends View {

    void showAvengersList (List<Character> avengers);

    void hideAvengersList();

    void showLoadingIndicator ();

    void hideLoadingIndicator ();

    void showLoadingView();

    void hideLoadingView();

    void showLightError();

    void showErrorView(String errorMessage);

    void hideErrorView();

    void showEmptyIndicator();

    void hideEmptyIndicator();

    void showAvengersList();

    ActivityOptions getActivityOptions (int position, android.view.View clickedView);
}
